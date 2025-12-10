"""
Tarif (Recipe) modeli
"""
from pydantic import BaseModel
from typing import Optional, List
from .user_context import UserContext


class Recipe(BaseModel):
    """Yemek tarifi modeli"""
    id: int
    title: str
    cooking_time: int  # dakika
    calories: int
    servings: int = 4
    recommendation_reason: Optional[str] = None
    available_ingredients: Optional[str] = None
    image_url: Optional[str] = ""
    instructions: List[str] = []


class RecipeRecommendationRequest(BaseModel):
    """Tarif önerisi istek modeli"""
    ingredients: List[str]
    dietary_preferences: Optional[List[str]] = None
    max_cooking_time: Optional[int] = None
    max_calories: Optional[int] = None
    limit: int = 20
    user_context: Optional[UserContext] = None


class RecipeRecommendationResponse(BaseModel):
    """Tarif önerisi cevap modeli"""
    recipes: List[Recipe]
    total: int
    matched_ingredients: List[str]
