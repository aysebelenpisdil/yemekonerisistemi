"""
Tarif (Recipe) servisi
Demo veriler ve tarif öneri mantığı
"""
import time
import json
from pathlib import Path
from models.recipe import Recipe
from typing import List, Optional
from sqlalchemy.orm import Session
from sqlalchemy import or_, and_
from db import models as db_models

class RecipeService:
    """Tarif servisi - DB + demo + JSON implementation"""

    def __init__(self, db: Session = None):
        self.db = db
        if not self.db:
            # Hardcoded (Turkce) ve JSON (Ingilizce) tarifleri birlestir
            hardcoded = self._load_hardcoded_recipes()
            json_recipes = self._load_json_recipes()

            # ID cakismalarini onlemek icin JSON tariflerinin ID'lerini kaydır
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
                    image_url=item.get("image_url", ""),
                    instructions=item.get("instructions", [])
                )
                for item in data
            ]
            print(f"[RecipeService] recipes.json: {len(recipes)} tarif yuklendi")
            return recipes

        except FileNotFoundError:
            print(f"[RecipeService] Warning: {json_path} bulunamadi")
            return []
        except json.JSONDecodeError as e:
            print(f"[RecipeService] Error parsing {json_path}: {e}")
            return []
        except Exception as e:
            print(f"[RecipeService] Unexpected error loading {json_path}: {e}")
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
                    image_url=item.get("image_url", ""),
                    instructions=item.get("instructions", [])
                )
                for item in data
            ]
            print(f"[RecipeService] recipes_en.json: {len(recipes)} tarif yuklendi")
            return recipes

        except FileNotFoundError:
            print(f"[RecipeService] Warning: {json_path} bulunamadi, JSON tarifleri atlanıyor")
            return []
        except json.JSONDecodeError as e:
            print(f"[RecipeService] Error parsing {json_path}: {e}")
            return []
        except Exception as e:
            print(f"[RecipeService] Unexpected error loading {json_path}: {e}")
            return []

    def filter_recipes(self, q: Optional[str] = None, max_time: Optional[int] = None, limit: int = 20) -> List[Recipe]:
        """
        Filter recipes by query and max cooking time
        Logs latency in ms
        """
        start_time = time.time()

        if self.db:
            # Use database
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
                Recipe(
                    id=r.id,
                    title=r.title,
                    cooking_time=r.cooking_time,
                    calories=r.calories,
                    servings=r.servings,
                    recommendation_reason=r.recommendation_reason,
                    available_ingredients=r.available_ingredients,
                    image_url=r.image_url or "",
                    instructions=json.loads(r.instructions) if r.instructions else []
                ) for r in db_recipes
            ]
        else:
            # Use demo recipes
            results = []
            for recipe in self.demo_recipes:
                if max_time and recipe.cooking_time > max_time:
                    continue

                if q:
                    q_lower = q.lower()
                    if q_lower not in recipe.title.lower() and q_lower not in (recipe.available_ingredients or "").lower():
                        continue

                results.append(recipe)
                if len(results) >= limit:
                    break

        # Log latency
        latency_ms = (time.time() - start_time) * 1000
        print(f"[RecipeService.filter] q='{q}', max_time={max_time}, results={len(results)}, latency={latency_ms:.1f}ms")

        return results

    def get_recipe_recommendations(
        self,
        ingredients: List[str],
        dietary_preferences: List[str] = None,
        max_cooking_time: int = None,
        max_calories: int = None,
        limit: int = 20
    ) -> tuple[List[Recipe], List[str]]:
        """
        Malzemelere göre tarif önerileri döndür
        Returns: (recipes, matched_ingredients)
        """
        start_time = time.time()

        if self.db:
            query = self.db.query(db_models.Recipe)

            if max_cooking_time:
                query = query.filter(db_models.Recipe.cooking_time <= max_cooking_time)

            if max_calories:
                query = query.filter(db_models.Recipe.calories <= max_calories)

            # Simple ingredient matching for DB
            if ingredients:
                conditions = []
                for ing in ingredients:
                    conditions.append(db_models.Recipe.available_ingredients.ilike(f"%{ing}%"))
                if conditions:
                    query = query.filter(or_(*conditions))

            db_recipes = query.limit(limit).all()

            filtered_recipes = [
                Recipe(
                    id=r.id,
                    title=r.title,
                    cooking_time=r.cooking_time,
                    calories=r.calories,
                    servings=r.servings,
                    recommendation_reason=r.recommendation_reason,
                    available_ingredients=r.available_ingredients,
                    image_url=r.image_url or "",
                    instructions=json.loads(r.instructions) if r.instructions else []
                ) for r in db_recipes
            ]
        else:
            # Use original logic for demo recipes
            filtered_recipes = []

            for recipe in self.demo_recipes:
                # Cooking time filtresi
                if max_cooking_time and recipe.cooking_time > max_cooking_time:
                    continue

                # Calories filtresi
                if max_calories and recipe.calories > max_calories:
                    continue

                # Malzeme uyumu kontrolü (basit string matching)
                if ingredients:
                    recipe_ingredients = recipe.available_ingredients.lower() if recipe.available_ingredients else ""
                    has_match = any(ing.lower() in recipe_ingredients for ing in ingredients)
                    if has_match:
                        filtered_recipes.append(recipe)
                else:
                    filtered_recipes.append(recipe)

            # Limit uygula
            filtered_recipes = filtered_recipes[:limit]

        # Eşleşen malzemeleri bul
        matched_ingredients = [ing for ing in ingredients if any(
            ing.lower() in (r.available_ingredients or "").lower()
            for r in filtered_recipes
        )]

        # Log latency
        latency_ms = (time.time() - start_time) * 1000
        print(f"[RecipeService.recommendations] ingredients={len(ingredients)}, results={len(filtered_recipes)}, latency={latency_ms:.1f}ms")

        return filtered_recipes, matched_ingredients

    def search_recipes(self, query: str, limit: int = 20) -> List[Recipe]:
        """Tarif ara"""
        start_time = time.time()

        if not query:
            # Return empty list for empty query
            print(f"[RecipeService.search] Empty query, returning [], latency=0.1ms")
            return []

        results = self.filter_recipes(q=query, limit=limit)

        # Log latency
        latency_ms = (time.time() - start_time) * 1000
        print(f"[RecipeService.search] query='{query}', results={len(results)}, latency={latency_ms:.1f}ms")

        return results

    def get_recipe_by_id(self, recipe_id: int) -> Recipe:
        """ID'ye göre tarif getir"""
        if self.db:
            db_recipe = self.db.query(db_models.Recipe).filter_by(id=recipe_id).first()
            if db_recipe:
                return Recipe(
                    id=db_recipe.id,
                    title=db_recipe.title,
                    cooking_time=db_recipe.cooking_time,
                    calories=db_recipe.calories,
                    servings=db_recipe.servings,
                    recommendation_reason=db_recipe.recommendation_reason,
                    available_ingredients=db_recipe.available_ingredients,
                    image_url=db_recipe.image_url or "",
                    instructions=json.loads(db_recipe.instructions) if db_recipe.instructions else []
                )
            return None
        else:
            for recipe in self.demo_recipes:
                if recipe.id == recipe_id:
                    return recipe
            return None

