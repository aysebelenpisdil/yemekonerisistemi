"""
Semantic search service using embeddings and FAISS
"""
import json
import pickle
import numpy as np
import time
from pathlib import Path
from typing import List, Optional, Tuple
from sentence_transformers import SentenceTransformer
import faiss
from sqlalchemy.orm import Session
from db import models as db_models
from models.ingredient import Ingredient

class SemanticSearchService:
    """Service for semantic search using embeddings"""

    def __init__(self, db: Session = None):
        self.db = db
        self.model = None
        self.index = None
        self.ids = None
        self._load_resources()

    def _load_resources(self):
        """Load model, index and ID mappings"""
        try:
            # Load model
            model_name = "emrecan/bert-base-turkish-cased-mean-nli-stsb-tr"
            print(f"Loading semantic model: {model_name}...")
            self.model = SentenceTransformer(model_name)

            # Load FAISS index
            index_path = Path(__file__).parent.parent / "data" / "embeddings" / "ingredients.index"
            if index_path.exists():
                print(f"Loading FAISS index from {index_path}...")
                self.index = faiss.read_index(str(index_path))

                # Load ID mapping
                id_map_path = index_path.with_suffix('.ids')
                if id_map_path.exists():
                    with open(id_map_path, 'rb') as f:
                        self.ids = pickle.load(f)
                    print(f"✅ Loaded index with {self.index.ntotal} vectors")
                else:
                    print("⚠️ ID mapping not found")
            else:
                print("⚠️ FAISS index not found. Run build_embeddings.py first.")

        except Exception as e:
            print(f"❌ Error loading semantic search resources: {e}")

    def search(self, query: str, limit: int = 10) -> List[Tuple[Ingredient, float]]:
        """
        Perform semantic search for ingredients

        Args:
            query: Search query
            limit: Maximum number of results

        Returns:
            List of (Ingredient, similarity_score) tuples
        """
        start_time = time.time()

        if not query:
            print(f"[SemanticService.search] Empty query, returning [], latency=0.1ms")
            return []

        if not self.model or not self.index or not self.ids:
            print("⚠️ Semantic search not available - resources not loaded")
            return []

        if not self.db:
            print("⚠️ Database session required for semantic search")
            return []

        try:
            # Encode query
            print(f"🔍 Semantic search for: '{query}'")
            query_embedding = self.model.encode([query])

            # Search in index
            distances, indices = self.index.search(
                query_embedding.astype('float32'),
                min(limit, self.index.ntotal)
            )

            # Convert to ingredients
            results = []
            for dist, idx in zip(distances[0], indices[0]):
                if idx < len(self.ids):
                    ing_id = self.ids[idx]
                    db_ing = self.db.query(db_models.Ingredient).filter_by(id=ing_id).first()
                    if db_ing:
                        ingredient = Ingredient(
                            name=db_ing.name,
                            portion_g=db_ing.portion_g,
                            calories=db_ing.calories,
                            fat_g=db_ing.fat_g,
                            carbs_g=db_ing.carbs_g,
                            protein_g=db_ing.protein_g,
                            sugar_g=db_ing.sugar_g,
                            fiber_g=db_ing.fiber_g
                        )
                        # Convert distance to similarity (inverse)
                        similarity = 1.0 / (1.0 + dist)
                        results.append((ingredient, similarity))

            # Log latency
            latency_ms = (time.time() - start_time) * 1000
            print(f"✅ Found {len(results)} semantic matches, latency={latency_ms:.1f}ms")
            return results

        except Exception as e:
            latency_ms = (time.time() - start_time) * 1000
            print(f"❌ Semantic search error: {e}, latency={latency_ms:.1f}ms")
            return []

    def explain_results(self, query: str, ingredients: List[Ingredient]) -> str:
        """
        Generate explanation for why ingredients were returned

        Args:
            query: Original search query
            ingredients: List of matched ingredients

        Returns:
            Explanation text
        """
        if not ingredients:
            return "Aramanıza uygun malzeme bulunamadı."

        # Simple explanation for POC
        names = [ing.name for ing in ingredients[:3]]
        explanation = f"'{query}' araması için önerilen malzemeler: {', '.join(names)}. "
        explanation += "Bu malzemeler, besin değerleri ve kullanım alanları açısından benzerlik göstermektedir."

        return explanation