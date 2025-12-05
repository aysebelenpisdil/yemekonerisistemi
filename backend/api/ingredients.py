"""
Malzeme (Ingredient) API endpoints
"""
from fastapi import APIRouter, Query, Depends
from typing import Optional
from sqlalchemy.orm import Session
from models.ingredient import Ingredient, IngredientSearchResponse
from services.ingredient_service import IngredientService
from db.base import get_db

router = APIRouter()

def get_ingredient_service(db: Session = Depends(get_db)) -> IngredientService:
    """Get ingredient service with DB session"""
    return IngredientService(db=db)

@router.get("/", response_model=IngredientSearchResponse)
async def search_ingredients(
    q: Optional[str] = Query(None, description="Arama sorgusu"),
    limit: int = Query(50, ge=1, le=500, description="Sonuç limiti"),
    service: IngredientService = Depends(get_ingredient_service)
):
    """
    Malzeme ara
    - **q**: Arama sorgusu (opsiyonel)
    - **limit**: Maksimum sonuç sayısı
    """
    print("\n" + "="*80)
    print("🔍 INGREDIENTS API ÇAĞRILDI!")
    print("="*80)
    print(f"📝 Query: {q}")
    print(f"📊 Limit: {limit}")
    print(f"🌐 Endpoint: /api/ingredients/")

    results = service.search_ingredients(q, limit)

    print(f"✅ Sonuç sayısı: {len(results)}")
    if results:
        print(f"📋 İlk 3 sonuç: {[r.name for r in results[:3]]}")
    else:
        print("❌ Hiç sonuç bulunamadı!")
    print("="*80 + "\n")

    return IngredientSearchResponse(
        results=results,
        total=len(results),
        query=q
    )

@router.get("/all", response_model=list[Ingredient])
async def get_all_ingredients(service: IngredientService = Depends(get_ingredient_service)):
    """Tüm malzemeleri getir"""
    return service.get_all_ingredients()

@router.get("/names", response_model=list[str])
async def get_ingredient_names(service: IngredientService = Depends(get_ingredient_service)):
    """Tüm malzeme isimlerini getir (autocomplete için)"""
    return service.get_ingredient_names()

@router.get("/{name}", response_model=Ingredient)
async def get_ingredient_by_name(name: str, service: IngredientService = Depends(get_ingredient_service)):
    """İsme göre malzeme detayını getir"""
    ingredient = service.get_ingredient_by_name(name)
    if not ingredient:
        from fastapi import HTTPException
        raise HTTPException(status_code=404, detail="Malzeme bulunamadı")
    return ingredient
