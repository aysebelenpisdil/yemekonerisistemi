"""
Semantic search service using embeddings and FAISS
Multilingual support for Turkish queries → English recipes
"""
import json
import pickle
import numpy as np
import time
from pathlib import Path
from typing import List, Optional, Tuple, Dict, Any
from sentence_transformers import SentenceTransformer
import faiss
from sqlalchemy.orm import Session
from db import models as db_models
from models.ingredient import Ingredient


class SemanticSearchService:
    """Service for semantic search using multilingual embeddings"""

    # Multilingual model - supports Turkish and English
    MULTILINGUAL_MODEL = "paraphrase-multilingual-MiniLM-L12-v2"
    # Fallback to Turkish model for ingredients
    TURKISH_MODEL = "emrecan/bert-base-turkish-cased-mean-nli-stsb-tr"

    def __init__(self, db: Session = None):
        self.db = db
        self.model = None
        self.ingredient_index = None
        self.ingredient_ids = None
        self.recipe_index = None
        self.recipe_ids = None
        self.recipes_data = None
        self._load_resources()

    def _load_resources(self):
        """Load model, indices and ID mappings"""
        try:
            # Load multilingual model
            print(f"Loading multilingual semantic model: {self.MULTILINGUAL_MODEL}...")
            self.model = SentenceTransformer(self.MULTILINGUAL_MODEL)
            print("✅ Multilingual model loaded")

            # Load ingredient index (old)
            self._load_ingredient_index()

            # Load recipe index (new multilingual)
            self._load_recipe_index()

        except Exception as e:
            print(f"❌ Error loading semantic search resources: {e}")

    def _load_ingredient_index(self):
        """Load ingredient FAISS index"""
        index_path = Path(__file__).parent.parent / "data" / "embeddings" / "ingredients.index"
        if index_path.exists():
            print(f"Loading ingredient FAISS index...")
            self.ingredient_index = faiss.read_index(str(index_path))

            id_map_path = index_path.with_suffix('.ids')
            if id_map_path.exists():
                with open(id_map_path, 'rb') as f:
                    self.ingredient_ids = pickle.load(f)
                print(f"✅ Ingredient index: {self.ingredient_index.ntotal} vectors")
        else:
            print("⚠️ Ingredient index not found")

    def _load_recipe_index(self):
        """Load recipe FAISS index"""
        index_path = Path(__file__).parent.parent / "data" / "embeddings" / "recipes_multilingual.index"
        if index_path.exists():
            print(f"Loading recipe FAISS index...")
            self.recipe_index = faiss.read_index(str(index_path))

            id_map_path = index_path.with_suffix('.ids')
            if id_map_path.exists():
                with open(id_map_path, 'rb') as f:
                    self.recipe_ids = pickle.load(f)
                print(f"✅ Recipe index: {self.recipe_index.ntotal} vectors")

            # Load recipes data for context
            recipes_path = Path(__file__).parent.parent / "data" / "recipes_en_full.json"
            if recipes_path.exists():
                with open(recipes_path, 'r', encoding='utf-8') as f:
                    recipes_list = json.load(f)
                    self.recipes_data = {r['id']: r for r in recipes_list}
                print(f"✅ Loaded {len(self.recipes_data)} recipes data")
        else:
            print("⚠️ Recipe index not found. Run build_multilingual_embeddings.py first.")

    def search_recipes(self, query: str, limit: int = 10) -> List[Dict[str, Any]]:
        """
        Search recipes using multilingual semantic search
        Supports Turkish queries → English recipes

        Args:
            query: Search query (Turkish or English)
            limit: Maximum number of results

        Returns:
            List of recipe dictionaries with similarity scores
        """
        start_time = time.time()

        if not query:
            return []

        if not self.model or not self.recipe_index or not self.recipe_ids:
            print("⚠️ Recipe semantic search not available")
            return []

        try:
            print(f"🔍 Multilingual recipe search: '{query}'")

            # Encode query
            query_embedding = self.model.encode([query])

            # Search in FAISS index
            distances, indices = self.recipe_index.search(
                query_embedding.astype('float32'),
                min(limit, self.recipe_index.ntotal)
            )

            # Build results
            results = []
            for dist, idx in zip(distances[0], indices[0]):
                if idx < len(self.recipe_ids):
                    recipe_id = self.recipe_ids[idx]
                    if self.recipes_data and recipe_id in self.recipes_data:
                        recipe = self.recipes_data[recipe_id].copy()
                        # Convert distance to similarity (cosine similarity)
                        # For normalized vectors: similarity = 1 - distance/2
                        similarity = float(1.0 / (1.0 + dist))
                        recipe['similarity_score'] = round(similarity, 4)
                        results.append(recipe)

            latency_ms = (time.time() - start_time) * 1000
            print(f"✅ Found {len(results)} recipes, latency={latency_ms:.1f}ms")
            return results

        except Exception as e:
            latency_ms = (time.time() - start_time) * 1000
            print(f"❌ Recipe search error: {e}, latency={latency_ms:.1f}ms")
            return []

    def search(self, query: str, limit: int = 10) -> List[Tuple[Ingredient, float]]:
        """
        Perform semantic search for ingredients (legacy method)

        Args:
            query: Search query
            limit: Maximum number of results

        Returns:
            List of (Ingredient, similarity_score) tuples
        """
        start_time = time.time()

        if not query:
            return []

        if not self.model or not self.ingredient_index or not self.ingredient_ids:
            print("⚠️ Ingredient semantic search not available")
            return []

        if not self.db:
            print("⚠️ Database session required for ingredient search")
            return []

        try:
            print(f"🔍 Semantic ingredient search: '{query}'")
            query_embedding = self.model.encode([query])

            distances, indices = self.ingredient_index.search(
                query_embedding.astype('float32'),
                min(limit, self.ingredient_index.ntotal)
            )

            results = []
            for dist, idx in zip(distances[0], indices[0]):
                if idx < len(self.ingredient_ids):
                    ing_id = self.ingredient_ids[idx]
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
                        similarity = 1.0 / (1.0 + dist)
                        results.append((ingredient, similarity))

            latency_ms = (time.time() - start_time) * 1000
            print(f"✅ Found {len(results)} ingredient matches, latency={latency_ms:.1f}ms")
            return results

        except Exception as e:
            latency_ms = (time.time() - start_time) * 1000
            print(f"❌ Ingredient search error: {e}, latency={latency_ms:.1f}ms")
            return []

    def get_recipe_context(self, recipe_ids: List[int]) -> List[Dict[str, Any]]:
        """
        Get recipe details for RAG context

        Args:
            recipe_ids: List of recipe IDs

        Returns:
            List of recipe dictionaries
        """
        if not self.recipes_data:
            return []

        return [
            self.recipes_data[rid]
            for rid in recipe_ids
            if rid in self.recipes_data
        ]

    def explain_results(self, query: str, ingredients: List[Ingredient]) -> str:
        """Generate explanation for ingredient results"""
        if not ingredients:
            return "Aramanıza uygun malzeme bulunamadı."

        names = [ing.name for ing in ingredients[:3]]
        explanation = f"'{query}' araması için önerilen malzemeler: {', '.join(names)}. "
        explanation += "Bu malzemeler, besin değerleri ve kullanım alanları açısından benzerlik göstermektedir."
        return explanation
