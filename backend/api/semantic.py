"""
Semantic search and RAG API endpoints
"""
from fastapi import APIRouter, Query, Depends, HTTPException
from typing import Optional, List, Dict, Any
from pydantic import BaseModel
from sqlalchemy.orm import Session
from db.base import get_db
from services.semantic_service import SemanticSearchService
from services.rag_service import RAGService
from services.llm_service import OllamaLLMService
from services.cache_service import get_cache
from models.ingredient import Ingredient

router = APIRouter()


# === Request/Response Models ===

class SemanticSearchResult(BaseModel):
    """Semantic search result with similarity score"""
    ingredient: Ingredient
    similarity: float
    rank: int


class SemanticSearchResponse(BaseModel):
    """Semantic search response"""
    results: List[SemanticSearchResult]
    total: int
    query: str
    explanation: Optional[str] = None


class RecipeSearchResult(BaseModel):
    """Recipe search result"""
    id: int
    title: str
    ingredients: List[str]
    similarity_score: float
    popularity_score: float


class RecipeSearchResponse(BaseModel):
    """Recipe search response"""
    recipes: List[Dict[str, Any]]
    total: int
    query: str
    latency_ms: float


class RAGRequest(BaseModel):
    """RAG ask request"""
    query: str
    context: Optional[Dict[str, Any]] = None


class RAGResponse(BaseModel):
    """RAG ask response"""
    answer: str
    sources: List[Dict[str, Any]]
    confidence: float
    latency_ms: float


class PopularRecipe(BaseModel):
    """Popular recipe response"""
    id: int
    title: str
    popularity_score: float
    view_count: int
    favorite_count: int


# === Service Dependencies ===

_semantic_service = None
_rag_service = None
_llm_service = None


def get_semantic_service(db: Session = Depends(get_db)) -> SemanticSearchService:
    """Get semantic search service"""
    global _semantic_service
    if _semantic_service is None:
        _semantic_service = SemanticSearchService(db=db)
    else:
        _semantic_service.db = db
    return _semantic_service


def get_llm_service() -> OllamaLLMService:
    """Get LLM service"""
    global _llm_service
    if _llm_service is None:
        _llm_service = OllamaLLMService()
    return _llm_service


def get_rag_service(db: Session = Depends(get_db)) -> RAGService:
    """Get RAG service"""
    global _rag_service
    semantic = get_semantic_service(db)
    llm = get_llm_service()
    if _rag_service is None:
        _rag_service = RAGService(semantic_service=semantic, llm_service=llm)
    return _rag_service


# === Ingredient Semantic Search (Legacy) ===

@router.get("/search", response_model=SemanticSearchResponse)
async def semantic_search(
    q: str = Query(..., description="Arama sorgusu"),
    limit: int = Query(10, ge=1, le=50, description="Sonuç limiti"),
    service: SemanticSearchService = Depends(get_semantic_service)
):
    """
    Semantic search for ingredients using embeddings
    """
    print(f"\n🧠 Semantic Ingredient Search: '{q}'")

    search_results = service.search(q, limit)

    results = []
    for i, (ingredient, similarity) in enumerate(search_results):
        results.append(SemanticSearchResult(
            ingredient=ingredient,
            similarity=round(similarity, 4),
            rank=i + 1
        ))

    explanation = None
    if results:
        ingredients_list = [r.ingredient for r in results]
        explanation = service.explain_results(q, ingredients_list)

    return SemanticSearchResponse(
        results=results,
        total=len(results),
        query=q,
        explanation=explanation
    )


# === Recipe Semantic Search ===

@router.get("/recipes/search", response_model=RecipeSearchResponse)
async def search_recipes(
    q: str = Query(..., description="Arama sorgusu (Türkçe veya İngilizce)"),
    limit: int = Query(10, ge=1, le=50, description="Sonuç limiti"),
    service: SemanticSearchService = Depends(get_semantic_service)
):
    """
    Multilingual semantic search for recipes

    Supports Turkish queries to find English recipes.
    Example: "tavuklu makarna" → finds Chicken Pasta recipes
    """
    print(f"\n🔍 Recipe Semantic Search: '{q}'")

    import time
    start_time = time.time()

    recipes = service.search_recipes(q, limit=limit)
    latency_ms = (time.time() - start_time) * 1000

    return RecipeSearchResponse(
        recipes=recipes,
        total=len(recipes),
        query=q,
        latency_ms=round(latency_ms, 1)
    )


# === RAG Endpoint ===

@router.post("/rag/ask", response_model=RAGResponse)
async def rag_ask(
    request: RAGRequest,
    rag_service: RAGService = Depends(get_rag_service)
):
    """
    RAG-based recipe assistant

    Ask questions in Turkish about recipes and get intelligent answers.

    Example queries:
    - "Akşam yemeği için hızlı bir şey öner"
    - "Tavuklu bir tarif istiyorum"
    - "Tatlı yapmak istiyorum, ne önerirsin?"
    """
    print(f"\n🤖 RAG Ask: '{request.query}'")

    result = rag_service.answer(
        query=request.query,
        user_context=request.context
    )

    return RAGResponse(
        answer=result["answer"],
        sources=result["sources"],
        confidence=result["confidence"],
        latency_ms=result["latency_ms"]
    )


# === Popular Recipes ===

@router.get("/recipes/popular")
async def get_popular_recipes(
    limit: int = Query(20, ge=1, le=100, description="Sonuç limiti"),
    service: SemanticSearchService = Depends(get_semantic_service)
):
    """
    Get most popular recipes by popularity score

    Returns recipes sorted by popularity_score (combination of
    common ingredients, simplicity, and category boost)
    """
    if not service.recipes_data:
        raise HTTPException(status_code=503, detail="Recipe data not loaded")

    # Sort by popularity_score
    recipes = list(service.recipes_data.values())
    sorted_recipes = sorted(
        recipes,
        key=lambda x: x.get('popularity_score', 0),
        reverse=True
    )[:limit]

    return {
        "recipes": sorted_recipes,
        "total": len(sorted_recipes)
    }


# === View Tracking ===

@router.post("/recipes/{recipe_id}/view")
async def track_recipe_view(
    recipe_id: int,
    service: SemanticSearchService = Depends(get_semantic_service)
):
    """
    Track recipe view and update popularity

    Increments view_count and recalculates popularity_score
    """
    if not service.recipes_data or recipe_id not in service.recipes_data:
        raise HTTPException(status_code=404, detail="Recipe not found")

    recipe = service.recipes_data[recipe_id]
    recipe['view_count'] = recipe.get('view_count', 0) + 1

    # Recalculate popularity with view boost
    base_score = recipe.get('popularity_score', 0.5)
    view_boost = min(0.1, recipe['view_count'] / 1000)  # Max 0.1 boost
    recipe['popularity_score'] = min(1.0, base_score + view_boost)

    return {
        "recipe_id": recipe_id,
        "view_count": recipe['view_count'],
        "popularity_score": recipe['popularity_score']
    }


# === Service Status ===

@router.get("/status")
async def semantic_status(
    service: SemanticSearchService = Depends(get_semantic_service),
    llm: OllamaLLMService = Depends(get_llm_service)
):
    """Check all semantic and RAG service status"""
    cache = get_cache()

    return {
        "semantic": {
            "model_loaded": service.model is not None,
            "ingredient_index": service.ingredient_index.ntotal if service.ingredient_index else 0,
            "recipe_index": service.recipe_index.ntotal if service.recipe_index else 0,
            "recipes_loaded": len(service.recipes_data) if service.recipes_data else 0
        },
        "llm": {
            "available": llm.is_available(),
            "model": llm.model
        },
        "cache": cache.stats()
    }
