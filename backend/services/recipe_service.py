"""
Tarif (Recipe) servisi
Demo veriler ve tarif öneri mantığı
"""
import time
from models.recipe import Recipe
from typing import List, Optional
from sqlalchemy.orm import Session
from sqlalchemy import or_, and_
from db import models as db_models

class RecipeService:
    """Tarif servisi - DB + demo implementation"""

    def __init__(self, db: Session = None):
        self.db = db
        if not self.db:
            self.demo_recipes = self._load_demo_recipes()
        else:
            self.demo_recipes = None

    def _load_demo_recipes(self) -> List[Recipe]:
        """Demo tarifleri yükle (fallback for no DB)"""
        return [
            Recipe(
                id=1,
                title="Tavuk Sote",
                cooking_time=30,
                calories=280,
                servings=4,
                recommendation_reason="Tavuk ve sebzelerinle mükemmel uyum! Protein değeri yüksek ve hazırlanması kolay.",
                available_ingredients="Tavuk, Domates, Biber",
                image_url="",
                instructions=[
                    "Tavuk göğüslerini küp şeklinde doğrayın ve tuzlayın",
                    "Domatesleri ve biberleri küp şeklinde doğrayın",
                    "Soğanı ince ince doğrayın",
                    "Tavada sıvı yağı kızdırın ve tavukları ekleyin",
                    "Tavuklar renk alana kadar kavurun (yaklaşık 5-7 dakika)",
                    "Soğanları ekleyip pembeleşene kadar kavurun",
                    "Domatesleri ve biberleri ekleyin",
                    "Kapağını kapatıp kısık ateşte sebzeler yumuşayana kadar pişirin (15-20 dakika)",
                    "Tuz ve karabiberle tatlandırın",
                    "Sıcak servis yapın. Afiyet olsun!"
                ]
            ),
            Recipe(
                id=2,
                title="Kremalı Makarna",
                cooking_time=20,
                calories=420,
                servings=2,
                recommendation_reason="Hızlı yemek tercihine uygun! 20 dakikada hazır ve lezzetli.",
                available_ingredients="Makarna, Yoğurt, Tereyağı",
                image_url="",
                instructions=[
                    "Makarnayı tuzlu suda haşlayın",
                    "Tavada tereyağını eritin",
                    "Yoğurdu ekleyip karıştırın",
                    "Haşlanmış makarnayı süzün",
                    "Kremalı sos ile karıştırın",
                    "Sıcak servis yapın"
                ]
            ),
            Recipe(
                id=3,
                title="Sebze Çorbası",
                cooking_time=25,
                calories=150,
                servings=4,
                recommendation_reason="Sağlıklı ve hafif bir seçim. Vitamin deposu!",
                available_ingredients="Domates, Soğan, Biber, Havuç",
                image_url="",
                instructions=[
                    "Sebzeleri küp doğrayın",
                    "Soğanı kavurun",
                    "Diğer sebzeleri ekleyin",
                    "Su ekleyip kaynatın",
                    "20 dakika pişirin",
                    "Tuz ve baharatla tatlandırın"
                ]
            ),
            Recipe(
                id=4,
                title="Yumurtalı Menemen",
                cooking_time=15,
                calories=220,
                servings=2,
                recommendation_reason="Kahvaltının vazgeçilmezi! Yumurta ve sebzelerinizle enfes bir lezzet.",
                available_ingredients="Yumurta, Domates, Biber",
                image_url="",
                instructions=[
                    "Biberleri ince doğrayın ve kavurun",
                    "Domatesleri ekleyin ve suyunu çekmesini bekleyin",
                    "Yumurtaları kırıp karıştırın",
                    "Pişirin ve servis yapın"
                ]
            ),
            Recipe(
                id=5,
                title="Et Sote",
                cooking_time=40,
                calories=350,
                servings=4,
                recommendation_reason="Protein ihtiyacınızı karşılayan doyurucu bir ana yemek.",
                available_ingredients="Et, Soğan, Domates",
                image_url="",
                instructions=[
                    "Eti küp doğrayın",
                    "Soğanları kavurun",
                    "Eti ekleyip renk almasını sağlayın",
                    "Domates ve baharatları ekleyin",
                    "30 dakika kısık ateşte pişirin"
                ]
            )
        ]

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

import json  # Add at top for json.loads