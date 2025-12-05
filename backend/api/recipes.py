"""
Tarif (Recipe) API endpoints
"""
from fastapi import APIRouter, HTTPException, Path, Depends, Query
from typing import Optional, List
from sqlalchemy.orm import Session
from models.recipe import (
    Recipe,
    RecipeRecommendationRequest,
    RecipeRecommendationResponse
)
from services.recipe_service import RecipeService
from db.base import get_db

router = APIRouter()

def get_recipe_service(db: Session = Depends(get_db)) -> RecipeService:
    """Get recipe service with DB session"""
    return RecipeService(db=db)

@router.get("/filter", response_model=List[Recipe])
async def filter_recipes(
    q: Optional[str] = Query(None, description="Arama sorgusu"),
    max_time: Optional[int] = Query(None, description="Maksimum pişirme süresi (dakika)"),
    limit: int = Query(20, ge=1, le=100, description="Sonuç limiti"),
    service: RecipeService = Depends(get_recipe_service)
):
    """
    Filter recipes by query and max cooking time
    - **q**: Search query (optional)
    - **max_time**: Maximum cooking time in minutes (optional)
    - **limit**: Maximum results
    Returns empty list [] for empty query
    """
    if not q and not max_time:
        # Return empty list for completely empty filter
        return []

    results = service.filter_recipes(q=q, max_time=max_time, limit=limit)
    return results

@router.post("/recommend", response_model=RecipeRecommendationResponse)
async def get_recipe_recommendations(
    request: RecipeRecommendationRequest,
    service: RecipeService = Depends(get_recipe_service)
):
    """
    Malzemelere göre tarif önerileri getir
    - **ingredients**: Buzdolabındaki malzemeler
    - **dietary_preferences**: Diyet tercihleri (opsiyonel)
    - **max_cooking_time**: Maksimum pişirme süresi (dakika)
    - **max_calories**: Maksimum kalori
    - **limit**: Sonuç limiti
    """
    recipes, matched_ingredients = service.get_recipe_recommendations(
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
    limit: int = 20,
    service: RecipeService = Depends(get_recipe_service)
):
    """
    Tarif ara
    - **q**: Arama sorgusu
    - **limit**: Maksimum sonuç sayısı
    """
    recipes = service.search_recipes(q, limit)
    return recipes

@router.get("/{recipe_id}", response_model=Recipe)
async def get_recipe_by_id(
    recipe_id: int = Path(..., description="Tarif ID'si"),
    service: RecipeService = Depends(get_recipe_service)
):
    """
    ID'ye göre tarif detayı getir
    """
    recipe = service.get_recipe_by_id(recipe_id)
    if not recipe:
        raise HTTPException(status_code=404, detail="Tarif bulunamadı")
    return recipe
