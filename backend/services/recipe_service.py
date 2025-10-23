"""
Tarif (Recipe) servisi
Demo veriler ve tarif öneri mantığı
"""
from models.recipe import Recipe
from typing import List

class RecipeService:
    """Tarif servisi - demo implementation"""
    
    def __init__(self):
        self.demo_recipes = self._load_demo_recipes()
    
    def _load_demo_recipes(self) -> List[Recipe]:
        """Demo tarifleri yükle"""
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
        # Basit filtreleme mantığı
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
        
        return filtered_recipes, matched_ingredients
    
    def search_recipes(self, query: str, limit: int = 20) -> List[Recipe]:
        """Tarif ara"""
        if not query:
            return self.demo_recipes[:limit]
        
        query_lower = query.lower()
        return [
            r for r in self.demo_recipes 
            if query_lower in r.title.lower()
        ][:limit]
    
    def get_recipe_by_id(self, recipe_id: int) -> Recipe:
        """ID'ye göre tarif getir"""
        for recipe in self.demo_recipes:
            if recipe.id == recipe_id:
                return recipe
        return None
