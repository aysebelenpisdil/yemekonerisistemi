"""
Malzeme (Ingredient) servis katmanı
"""
import json
from pathlib import Path
from typing import List, Optional
from models.ingredient import Ingredient
from utils.search_utils import SearchEngine

class IngredientService:
    """Malzeme yönetim servisi"""

    def __init__(self):
        self.ingredients: List[Ingredient] = []
        self.search_engine = SearchEngine()
        self._load_ingredients()

    def _load_ingredients(self):
        """JSON dosyasından malzemeleri yükle"""
        json_path = Path(__file__).parent.parent / "data" / "ingredients.json"

        try:
            with open(json_path, 'r', encoding='utf-8') as f:
                data = json.load(f)
                self.ingredients = [Ingredient(**item) for item in data]
            print(f"✅ {len(self.ingredients)} malzeme yüklendi")
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
        print(f"\n🔎 IngredientService.search_ingredients() çağrıldı")
        print(f"   Query: '{query}'")
        print(f"   Limit: {limit}")
        print(f"   Toplam malzeme sayısı: {len(self.ingredients)}")

        if not query:
            print(f"   ⚠️  Query boş, ilk {limit} malzeme döndürülüyor")
            return self.ingredients[:limit]

        # Tüm malzeme isimlerini çıkar
        ingredient_names = [ing.name for ing in self.ingredients]
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
            ingredient = next((ing for ing in self.ingredients if ing.name == name), None)
            if ingredient:
                results.append(ingredient)

        print(f"   ✅ {len(results)} Ingredient objesi döndürülüyor")
        if results:
            print(f"   📋 İlk 3: {[r.name for r in results[:3]]}")

        return results

    def get_ingredient_by_name(self, name: str) -> Optional[Ingredient]:
        """İsme göre malzeme bul"""
        for ing in self.ingredients:
            if ing.name.lower() == name.lower():
                return ing
        return None

    def get_all_ingredients(self) -> List[Ingredient]:
        """Tüm malzemeleri getir"""
        return self.ingredients

    def get_ingredient_names(self) -> List[str]:
        """Tüm malzeme isimlerini getir"""
        return [ing.name for ing in self.ingredients]
