"""
Malzeme (Ingredient) API endpoints
"""
from fastapi import APIRouter, Query
from typing import Optional
from models.ingredient import Ingredient, IngredientSearchResponse
from services.ingredient_service import IngredientService

router = APIRouter()
ingredient_service = IngredientService()

@router.get("/", response_model=IngredientSearchResponse)
async def search_ingredients(
    q: Optional[str] = Query(None, description="Arama sorgusu"),
    limit: int = Query(50, ge=1, le=500, description="Sonuç limiti")
):
    """
    Malzeme ara
    - **q**: Arama sorgusu (opsiyonel)
    - **limit**: Maksimum sonuç sayısı
    """
    results = ingredient_service.search_ingredients(q, limit)
    return IngredientSearchResponse(
        results=results,
        total=len(results),
        query=q
    )

@router.get("/all", response_model=list[Ingredient])
async def get_all_ingredients():
    """Tüm malzemeleri getir"""
    return ingredient_service.get_all_ingredients()

@router.get("/names", response_model=list[str])
async def get_ingredient_names():
    """Tüm malzeme isimlerini getir (autocomplete için)"""
    return ingredient_service.get_ingredient_names()

@router.get("/{name}", response_model=Ingredient)
async def get_ingredient_by_name(name: str):
    """İsme göre malzeme detayını getir"""
    ingredient = ingredient_service.get_ingredient_by_name(name)
    if not ingredient:
        from fastapi import HTTPException
        raise HTTPException(status_code=404, detail="Malzeme bulunamadı")
    return ingredient
