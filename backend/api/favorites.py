# Bu dosya oluşturulabilir - Favoriler API

"""
Favoriler API - Gelecek implementasyon için şablon
"""
from fastapi import APIRouter, Depends
from pydantic import BaseModel
from typing import List

router = APIRouter()

class FavoriteRequest(BaseModel):
    recipe_id: int
    user_id: str = "anonymous"  # Auth yokken

class FavoriteResponse(BaseModel):
    success: bool
    message: str
    favorites_count: int

# In-memory storage (POC için)
_favorites: dict = {}  # user_id -> List[recipe_id]

@router.post("/")
async def add_favorite(request: FavoriteRequest) -> FavoriteResponse:
    """Favorilere tarif ekle"""
    user_favorites = _favorites.get(request.user_id, [])
    
    if request.recipe_id not in user_favorites:
        user_favorites.append(request.recipe_id)
        _favorites[request.user_id] = user_favorites
        return FavoriteResponse(
            success=True,
            message="Favorilere eklendi",
            favorites_count=len(user_favorites)
        )
    
    return FavoriteResponse(
        success=False,
        message="Zaten favorilerde",
        favorites_count=len(user_favorites)
    )

@router.delete("/{recipe_id}")
async def remove_favorite(recipe_id: int, user_id: str = "anonymous") -> FavoriteResponse:
    """Favorilerden tarif çıkar"""
    user_favorites = _favorites.get(user_id, [])
    
    if recipe_id in user_favorites:
        user_favorites.remove(recipe_id)
        _favorites[user_id] = user_favorites
        return FavoriteResponse(
            success=True,
            message="Favorilerden çıkarıldı",
            favorites_count=len(user_favorites)
        )
    
    return FavoriteResponse(
        success=False,
        message="Favorilerde bulunamadı",
        favorites_count=len(user_favorites)
    )

@router.get("/")
async def get_favorites(user_id: str = "anonymous") -> List[int]:
    """Kullanıcının favorilerini getir"""
    return _favorites.get(user_id, [])
