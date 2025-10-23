"""
Malzeme (Ingredient) modeli
"""
from pydantic import BaseModel
from typing import Optional

class Ingredient(BaseModel):
    """Besin malzemesi modeli"""
    name: str
    portion_g: float
    calories: float
    fat_g: float
    carbs_g: float
    protein_g: float
    sugar_g: float
    fiber_g: float

class IngredientSearchResponse(BaseModel):
    """Malzeme arama cevabı"""
    results: list[Ingredient]
    total: int
    query: Optional[str] = None
