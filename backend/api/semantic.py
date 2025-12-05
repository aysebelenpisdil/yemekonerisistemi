"""
Semantic search API endpoints
"""
from fastapi import APIRouter, Query, Depends
from typing import Optional, List
from pydantic import BaseModel
from sqlalchemy.orm import Session
from db.base import get_db
from services.semantic_service import SemanticSearchService
from models.ingredient import Ingredient

router = APIRouter()

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

def get_semantic_service(db: Session = Depends(get_db)) -> SemanticSearchService:
    """Get semantic search service with DB session"""
    return SemanticSearchService(db=db)

@router.get("/search", response_model=SemanticSearchResponse)
async def semantic_search(
    q: str = Query(..., description="Arama sorgusu"),
    limit: int = Query(10, ge=1, le=50, description="Sonuç limiti"),
    service: SemanticSearchService = Depends(get_semantic_service)
):
    """
    Semantic search for ingredients using embeddings

    - **q**: Search query (required)
    - **limit**: Maximum number of results

    This endpoint uses BERT embeddings and FAISS for semantic similarity search.
    """
    print("\n" + "="*80)
    print("🧠 SEMANTIC SEARCH API CALLED!")
    print("="*80)
    print(f"📝 Query: {q}")
    print(f"📊 Limit: {limit}")

    # Perform semantic search
    search_results = service.search(q, limit)

    # Convert to response format
    results = []
    for i, (ingredient, similarity) in enumerate(search_results):
        results.append(SemanticSearchResult(
            ingredient=ingredient,
            similarity=round(similarity, 4),
            rank=i + 1
        ))

    # Generate explanation
    explanation = None
    if results:
        ingredients_list = [r.ingredient for r in results]
        explanation = service.explain_results(q, ingredients_list)

    print(f"✅ Returning {len(results)} semantic results")
    if results:
        print(f"📋 Top 3: {[r.ingredient.name for r in results[:3]]}")
    print("="*80 + "\n")

    return SemanticSearchResponse(
        results=results,
        total=len(results),
        query=q,
        explanation=explanation
    )

@router.get("/status")
async def semantic_status(service: SemanticSearchService = Depends(get_semantic_service)):
    """Check semantic search service status"""
    status = {
        "model_loaded": service.model is not None,
        "index_loaded": service.index is not None,
        "index_size": service.index.ntotal if service.index else 0,
        "available": service.model is not None and service.index is not None
    }
    return status