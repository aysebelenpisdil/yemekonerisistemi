"""
Tarif (Recipe) servisi
Demo veriler ve tarif öneri mantığı
"""
import time
import json
import random
from pathlib import Path
from models.recipe import Recipe
from models.user_context import UserContext
from typing import List, Optional
from sqlalchemy.orm import Session
from sqlalchemy import or_
from db import models as db_models
from utils.allergen_mapping import filter_recipes_by_allergens


# Varsayılan yemek görselleri (placeholder)
DEFAULT_FOOD_IMAGES = [
    "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400",
    "https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=400",
    "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400",
    "https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=400",
    "https://images.unsplash.com/photo-1565958011703-44f9829ba187?w=400",
    "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=400",
    "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=400",
]


def get_default_image_url() -> str:
    """Rastgele varsayılan görsel URL'i döndür"""
    return random.choice(DEFAULT_FOOD_IMAGES)


def ensure_image_url(image_url: Optional[str]) -> str:
    """Görsel URL'i kontrol et, boşsa varsayılan döndür"""
    if image_url and image_url.strip() and not image_url.startswith("https://example.com"):
        return image_url
    return get_default_image_url()


class RecipeService:
    """Tarif servisi - DB + demo + JSON implementation"""

    def __init__(self, db: Session = None):
        self.db = db
        if not self.db:
            hardcoded = self._load_hardcoded_recipes()
            json_recipes = self._load_json_recipes()

            if hardcoded and json_recipes:
                max_id = max(r.id for r in hardcoded)
                for recipe in json_recipes:
                    recipe.id = recipe.id + max_id

            self.demo_recipes = hardcoded + json_recipes
            print(f"[RecipeService] Yuklendi: {len(hardcoded)} hardcoded + {len(json_recipes)} JSON = {len(self.demo_recipes)} toplam tarif")
        else:
            self.demo_recipes = None

    def _load_hardcoded_recipes(self) -> List[Recipe]:
        """Turkce demo tarifleri recipes.json dosyasindan yukle"""
        current_dir = Path(__file__).parent
        json_path = current_dir.parent / "data" / "recipes.json"

        try:
            with open(json_path, "r", encoding="utf-8") as f:
                data = json.load(f)

            recipes = [
                Recipe(
                    id=item["id"],
                    title=item["title"],
                    cooking_time=item["cooking_time"],
                    calories=item["calories"],
                    servings=item.get("servings", 4),
                    recommendation_reason=item.get("recommendation_reason"),
                    available_ingredients=item.get("available_ingredients", ""),
                    image_url=ensure_image_url(item.get("image_url", "")),
                    instructions=item.get("instructions", [])
                )
                for item in data
            ]
            return recipes

        except Exception as e:
            print(f"[RecipeService] Error loading recipes.json: {e}")
            return []

    def _load_json_recipes(self) -> List[Recipe]:
        """Ingilizce tarifleri recipes_en.json dosyasindan yukle"""
        current_dir = Path(__file__).parent
        json_path = current_dir.parent / "data" / "recipes_en.json"

        try:
            with open(json_path, "r", encoding="utf-8") as f:
                data = json.load(f)

            recipes = [
                Recipe(
                    id=item["id"],
                    title=item["title"],
                    cooking_time=item.get("cooking_time", 30),
                    calories=item.get("calories", 300),
                    servings=item.get("servings", 4),
                    recommendation_reason=item.get("recommendation_reason"),
                    available_ingredients=item.get("available_ingredients", ""),
                    image_url=ensure_image_url(item.get("image_url", "")),
                    instructions=item.get("instructions", [])
                )
                for item in data
            ]
            return recipes

        except Exception as e:
            print(f"[RecipeService] Error loading recipes_en.json: {e}")
            return []

    def _ensure_recipe_image(self, recipe: Recipe) -> Recipe:
        """Tarif görselini kontrol et ve gerekirse varsayılan ekle"""
        if not recipe.image_url or recipe.image_url.startswith("https://example.com"):
            recipe.image_url = get_default_image_url()
        return recipe

    def filter_recipes(self, q: Optional[str] = None, max_time: Optional[int] = None, limit: int = 20) -> List[Recipe]:
        """Filter recipes by query and max cooking time"""
        start_time = time.time()

        if self.db:
            query = self.db.query(db_models.Recipe)

            if q:
                query = query.filter(
                    or_(
                        db_models.Recipe.title.ilike(f"%{q}%"),
                        db_models.Recipe.available_ingredients.ilike(f"%{q}%")
                    )
                )

            if max_time:
                query = query.filter(db_models.Recipe.cooking_time <= max_time)

            db_recipes = query.limit(limit).all()

            results = [
                self._ensure_recipe_image(Recipe(
                    id=r.id,
                    title=r.title,
                    cooking_time=r.cooking_time,
                    calories=r.calories,
                    servings=r.servings,
                    recommendation_reason=r.recommendation_reason,
                    available_ingredients=r.available_ingredients,
                    image_url=r.image_url or "",
                    instructions=json.loads(r.instructions) if r.instructions else []
                )) for r in db_recipes
            ]
        else:
            results = []
            for recipe in self.demo_recipes:
                if max_time and recipe.cooking_time > max_time:
                    continue

                if q:
                    q_lower = q.lower()
                    if q_lower not in recipe.title.lower() and q_lower not in (recipe.available_ingredients or "").lower():
                        continue

                results.append(self._ensure_recipe_image(recipe))
                if len(results) >= limit:
                    break

        latency_ms = (time.time() - start_time) * 1000
        print(f"[RecipeService.filter] q='{q}', max_time={max_time}, results={len(results)}, latency={latency_ms:.1f}ms")

        return results

    def get_recipe_recommendations(
        self,
        ingredients: List[str],
        dietary_preferences: List[str] = None,
        max_cooking_time: int = None,
        max_calories: int = None,
        limit: int = 20,
        user_context: UserContext = None
    ) -> tuple[List[Recipe], List[str]]:
        """
        Malzemelere göre tarif önerileri döndür

        Args:
            ingredients: Kullanıcının mevcut malzemeleri
            dietary_preferences: Diyet tercihleri
            max_cooking_time: Maksimum pişirme süresi (dakika)
            max_calories: Maksimum kalori
            limit: Sonuç limiti
            user_context: Kullanıcı bağlamı (alerjenler, tercihler vs.)

        Returns:
            Tarif listesi ve eşleşen malzemeler
        """
        start_time = time.time()

        # Extract allergens from user_context if provided
        allergens = []
        if user_context and user_context.allergens:
            allergens = user_context.allergens

        # Use user_context values if not explicitly provided
        # Prioritize chip-based preferences over legacy fields
        if user_context:
            if max_cooking_time is None:
                max_cooking_time = user_context.get_max_cooking_time_from_prefs()
            if max_calories is None:
                max_calories = user_context.get_max_calories_from_prefs()

        if self.db:
            query = self.db.query(db_models.Recipe)

            if max_cooking_time:
                query = query.filter(db_models.Recipe.cooking_time <= max_cooking_time)

            if max_calories:
                query = query.filter(db_models.Recipe.calories <= max_calories)

            if ingredients:
                conditions = []
                for ing in ingredients:
                    conditions.append(db_models.Recipe.available_ingredients.ilike(f"%{ing}%"))
                if conditions:
                    query = query.filter(or_(*conditions))

            db_recipes = query.limit(limit).all()

            filtered_recipes = [
                self._ensure_recipe_image(Recipe(
                    id=r.id,
                    title=r.title,
                    cooking_time=r.cooking_time,
                    calories=r.calories,
                    servings=r.servings,
                    recommendation_reason=r.recommendation_reason,
                    available_ingredients=r.available_ingredients,
                    image_url=r.image_url or "",
                    instructions=json.loads(r.instructions) if r.instructions else []
                )) for r in db_recipes
            ]
        else:
            filtered_recipes = []

            for recipe in self.demo_recipes:
                if max_cooking_time and recipe.cooking_time > max_cooking_time:
                    continue

                if max_calories and recipe.calories > max_calories:
                    continue

                if ingredients:
                    recipe_ingredients = recipe.available_ingredients.lower() if recipe.available_ingredients else ""
                    has_match = any(ing.lower() in recipe_ingredients for ing in ingredients)
                    if has_match:
                        filtered_recipes.append(self._ensure_recipe_image(recipe))
                else:
                    filtered_recipes.append(self._ensure_recipe_image(recipe))

            filtered_recipes = filtered_recipes[:limit]

        # Apply allergen hard filter - remove recipes containing allergens
        if allergens:
            pre_filter_count = len(filtered_recipes)
            # Convert Recipe objects to dicts for filtering
            recipe_dicts = [
                {"available_ingredients": r.available_ingredients, "recipe": r}
                for r in filtered_recipes
            ]
            safe_recipe_dicts = filter_recipes_by_allergens(recipe_dicts, allergens)
            filtered_recipes = [rd["recipe"] for rd in safe_recipe_dicts]
            print(f"[RecipeService] Allergen filter: {pre_filter_count} -> {len(filtered_recipes)} recipes (allergens: {allergens})")

        matched_ingredients = [ing for ing in ingredients if any(
            ing.lower() in (r.available_ingredients or "").lower()
            for r in filtered_recipes
        )]

        latency_ms = (time.time() - start_time) * 1000
        print(f"[RecipeService.recommendations] ingredients={len(ingredients)}, results={len(filtered_recipes)}, latency={latency_ms:.1f}ms")

        return filtered_recipes, matched_ingredients

    def search_recipes(self, query: str, limit: int = 20) -> List[Recipe]:
        """Tarif ara"""
        if not query:
            return []
        return self.filter_recipes(q=query, limit=limit)

    def get_recipe_by_id(self, recipe_id: int) -> Optional[Recipe]:
        """ID'ye göre tarif getir"""
        if self.db:
            db_recipe = self.db.query(db_models.Recipe).filter_by(id=recipe_id).first()
            if db_recipe:
                return self._ensure_recipe_image(Recipe(
                    id=db_recipe.id,
                    title=db_recipe.title,
                    cooking_time=db_recipe.cooking_time,
                    calories=db_recipe.calories,
                    servings=db_recipe.servings,
                    recommendation_reason=db_recipe.recommendation_reason,
                    available_ingredients=db_recipe.available_ingredients,
                    image_url=db_recipe.image_url or "",
                    instructions=json.loads(db_recipe.instructions) if db_recipe.instructions else []
                ))
            return None
        else:
            for recipe in self.demo_recipes:
                if recipe.id == recipe_id:
                    return self._ensure_recipe_image(recipe)
            return None

