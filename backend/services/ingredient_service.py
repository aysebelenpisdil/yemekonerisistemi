"""
Malzeme (Ingredient) servis katmanı
"""
import json
from pathlib import Path
from typing import List, Optional
from models.ingredient import Ingredient

class IngredientService:
    """Malzeme yönetim servisi"""

    def __init__(self):
        self.ingredients: List[Ingredient] = []
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
        """Malzeme ara"""
        if not query:
            return self.ingredients[:limit]

        query_lower = query.lower()
        results = [
            ing for ing in self.ingredients
            if query_lower in ing.name.lower()
        ]
        return results[:limit]

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
