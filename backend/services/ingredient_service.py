"""
Malzeme (Ingredient) servis katmanı
"""
import json
import time
from pathlib import Path
from typing import List, Optional
from models.ingredient import Ingredient
from utils.search_utils import SearchEngine
from sqlalchemy.orm import Session
from db import models as db_models

class IngredientService:
    """Malzeme yönetim servisi"""

    def __init__(self, db: Session = None):
        self.db = db
        self.search_engine = SearchEngine()
        # For backward compatibility, cache ingredients if no DB session
        if not self.db:
            self.ingredients: List[Ingredient] = []
            self._load_ingredients()
        else:
            self.ingredients = None  # Use DB directly

    def _load_ingredients(self):
        """JSON dosyasından malzemeleri yükle (fallback for no DB)"""
        json_path = Path(__file__).parent.parent / "data" / "ingredients.json"

        try:
            with open(json_path, 'r', encoding='utf-8') as f:
                data = json.load(f)
                self.ingredients = [Ingredient(**item) for item in data]
            print(f"✅ {len(self.ingredients)} malzeme yüklendi (JSON)")
        except Exception as e:
            print(f"❌ Malzemeler yüklenirken hata: {e}")
            self.ingredients = []

    def search_ingredients(self, query: Optional[str] = None, limit: int = 50) -> List[Ingredient]:
        """
        Gelişmiş malzeme arama - Trendyol benzeri

        Features:
        - Fuzzy matching (typo tolerance)
        - Turkish character support
        - Partial matching
        - Relevance scoring
        """
        start_time = time.time()
        print(f"\n🔎 IngredientService.search_ingredients() çağrıldı")
        print(f"   Query: '{query}'")
        print(f"   Limit: {limit}")

        # Get ingredients from DB or cache
        if self.db:
            db_ingredients = self.db.query(db_models.Ingredient).all()
            ingredients_list = [
                Ingredient(
                    name=ing.name,
                    portion_g=ing.portion_g,
                    calories=ing.calories,
                    fat_g=ing.fat_g,
                    carbs_g=ing.carbs_g,
                    protein_g=ing.protein_g,
                    sugar_g=ing.sugar_g,
                    fiber_g=ing.fiber_g
                ) for ing in db_ingredients
            ]
            print(f"   Toplam malzeme sayısı (DB): {len(ingredients_list)}")
        else:
            ingredients_list = self.ingredients
            print(f"   Toplam malzeme sayısı (JSON): {len(ingredients_list)}")

        if not query:
            print(f"   ⚠️  Query boş, ilk {limit} malzeme döndürülüyor")
            return ingredients_list[:limit]

        # Tüm malzeme isimlerini çıkar
        ingredient_names = [ing.name for ing in ingredients_list]
        print(f"   📋 Malzeme isimleri hazırlandı: {len(ingredient_names)} adet")

        # SearchEngine ile ara (threshold=30, minimum %30 match)
        print(f"   🔍 SearchEngine.search() çağrılıyor...")
        search_results = self.search_engine.search(
            query=query,
            items=ingredient_names,
            threshold=30.0,
            limit=limit
        )
        print(f"   ✅ SearchEngine {len(search_results)} sonuç döndürdü")

        # Sonuçları Ingredient objelerine çevir (sıralı)
        results = []
        for name, score in search_results:
            # İlgili ingredient'i bul
            ingredient = next((ing for ing in ingredients_list if ing.name == name), None)
            if ingredient:
                results.append(ingredient)

        # Log latency
        latency_ms = (time.time() - start_time) * 1000
        print(f"   ✅ {len(results)} Ingredient objesi döndürülüyor, latency={latency_ms:.1f}ms")
        if results:
            print(f"   📋 İlk 3: {[r.name for r in results[:3]]}")

        return results

    def get_ingredient_by_name(self, name: str) -> Optional[Ingredient]:
        """İsme göre malzeme bul"""
        if self.db:
            db_ing = self.db.query(db_models.Ingredient).filter(
                db_models.Ingredient.name.ilike(name)
            ).first()
            if db_ing:
                return Ingredient(
                    name=db_ing.name,
                    portion_g=db_ing.portion_g,
                    calories=db_ing.calories,
                    fat_g=db_ing.fat_g,
                    carbs_g=db_ing.carbs_g,
                    protein_g=db_ing.protein_g,
                    sugar_g=db_ing.sugar_g,
                    fiber_g=db_ing.fiber_g
                )
            return None
        else:
            for ing in self.ingredients:
                if ing.name.lower() == name.lower():
                    return ing
            return None

    def get_all_ingredients(self) -> List[Ingredient]:
        """Tüm malzemeleri getir"""
        if self.db:
            db_ingredients = self.db.query(db_models.Ingredient).all()
            return [
                Ingredient(
                    name=ing.name,
                    portion_g=ing.portion_g,
                    calories=ing.calories,
                    fat_g=ing.fat_g,
                    carbs_g=ing.carbs_g,
                    protein_g=ing.protein_g,
                    sugar_g=ing.sugar_g,
                    fiber_g=ing.fiber_g
                ) for ing in db_ingredients
            ]
        else:
            return self.ingredients

    def get_ingredient_names(self) -> List[str]:
        """Tüm malzeme isimlerini getir"""
        if self.db:
            names = self.db.query(db_models.Ingredient.name).all()
            return [name[0] for name in names]
        else:
            return [ing.name for ing in self.ingredients]
