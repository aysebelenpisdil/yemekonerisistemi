"""
Tarif (Recipe) API endpoints
"""
from fastapi import APIRouter, HTTPException, Path
from typing import Optional
from models.recipe import (
    Recipe,
    RecipeRecommendationRequest,
    RecipeRecommendationResponse
)
from services.recipe_service import RecipeService

router = APIRouter()
recipe_service = RecipeService()

@router.post("/recommend", response_model=RecipeRecommendationResponse)
async def get_recipe_recommendations(request: RecipeRecommendationRequest):
    """
    Malzemelere göre tarif önerileri getir
    - **ingredients**: Buzdolabındaki malzemeler
    - **dietary_preferences**: Diyet tercihleri (opsiyonel)
    - **max_cooking_time**: Maksimum pişirme süresi (dakika)
    - **max_calories**: Maksimum kalori
    - **limit**: Sonuç limiti
    """
    recipes, matched_ingredients = recipe_service.get_recipe_recommendations(
        ingredients=request.ingredients,
        dietary_preferences=request.dietary_preferences,
        max_cooking_time=request.max_cooking_time,
        max_calories=request.max_calories,
        limit=request.limit
    )
    
    return RecipeRecommendationResponse(
        recipes=recipes,
        total=len(recipes),
        matched_ingredients=matched_ingredients
    )

@router.get("/search", response_model=list[Recipe])
async def search_recipes(
    q: str,
    limit: int = 20
):
    """
    Tarif ara
    - **q**: Arama sorgusu
    - **limit**: Maksimum sonuç sayısı
    """
    recipes = recipe_service.search_recipes(q, limit)
    return recipes

@router.get("/{recipe_id}", response_model=Recipe)
async def get_recipe_by_id(
    recipe_id: int = Path(..., description="Tarif ID'si")
):
    """
    ID'ye göre tarif detayı getir
    """
    recipe = recipe_service.get_recipe_by_id(recipe_id)
    if not recipe:
        raise HTTPException(status_code=404, detail="Tarif bulunamadı")
    return recipe
