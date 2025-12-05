"""
Build embeddings and FAISS index for semantic search
"""
import sys
import os
import json
import pickle
import numpy as np
from pathlib import Path

# Add parent directory to path
sys.path.append(os.path.dirname(os.path.dirname(os.path.realpath(__file__))))

from sentence_transformers import SentenceTransformer
import faiss
from db.base import SessionLocal
from db.models import Ingredient, Recipe, Embedding

# Model for Turkish text embeddings
MODEL_NAME = "emrecan/bert-base-turkish-cased-mean-nli-stsb-tr"
EMBEDDING_DIM = 768  # BERT base dimension

def load_or_create_model():
    """Load the sentence transformer model"""
    print(f"Loading model: {MODEL_NAME}...")
    model = SentenceTransformer(MODEL_NAME)
    print(f"✅ Model loaded. Embedding dimension: {EMBEDDING_DIM}")
    return model

def build_ingredient_embeddings(session, model):
    """Generate embeddings for ingredients"""
    ingredients = session.query(Ingredient).all()
    print(f"Building embeddings for {len(ingredients)} ingredients...")

    # Prepare texts for embedding
    texts = []
    ids = []
    for ing in ingredients:
        # Create rich text representation
        text = f"{ing.name} - kalori: {ing.calories}, protein: {ing.protein_g}g, karbonhidrat: {ing.carbs_g}g"
        texts.append(text)
        ids.append(ing.id)

    # Generate embeddings
    embeddings = model.encode(texts, show_progress_bar=True, batch_size=32)

    # Store embeddings in embeddings table
    for i, ing in enumerate(ingredients):
        # Check if embedding exists
        existing = session.query(Embedding).filter_by(
            entity_type='ingredient',
            entity_id=ing.id
        ).first()

        if existing:
            existing.embedding_text = json.dumps(embeddings[i].tolist())
        else:
            embedding_record = Embedding(
                entity_type='ingredient',
                entity_id=ing.id,
                embedding_text=json.dumps(embeddings[i].tolist())
            )
            session.add(embedding_record)

    session.commit()
    print(f"✅ Generated and stored {len(embeddings)} ingredient embeddings")

    return embeddings, ids

def build_recipe_embeddings(session, model):
    """Generate embeddings for recipes (title + first 3 ingredients)"""
    recipes = session.query(Recipe).all()
    print(f"Building embeddings for {len(recipes)} recipes...")

    # Prepare texts for embedding
    texts = []
    ids = []
    for recipe in recipes:
        # Use title + first part of available_ingredients
        text = f"{recipe.title}"
        if recipe.available_ingredients:
            # Take first 3 ingredients
            ingredients = recipe.available_ingredients.split(',')[:3]
            text += f" - {', '.join(ingredients)}"
        texts.append(text)
        ids.append(recipe.id)

    if not texts:
        print("No recipes to embed")
        return [], []

    # Generate embeddings
    embeddings = model.encode(texts, show_progress_bar=True, batch_size=32)

    # Store embeddings in embeddings table
    for i, recipe in enumerate(recipes):
        # Check if embedding exists
        existing = session.query(Embedding).filter_by(
            entity_type='recipe',
            entity_id=recipe.id
        ).first()

        if existing:
            existing.embedding_text = json.dumps(embeddings[i].tolist())
        else:
            embedding_record = Embedding(
                entity_type='recipe',
                entity_id=recipe.id,
                embedding_text=json.dumps(embeddings[i].tolist())
            )
            session.add(embedding_record)

    session.commit()
    print(f"✅ Generated and stored {len(embeddings)} recipe embeddings")

    return embeddings, ids

def build_faiss_index(embeddings, ids, index_path):
    """Build and save FAISS index"""
    print(f"Building FAISS index with {len(embeddings)} vectors...")

    # Convert to numpy array
    embeddings_array = np.array(embeddings).astype('float32')

    # Create FAISS index (using L2 distance)
    index = faiss.IndexFlatL2(EMBEDDING_DIM)

    # Add vectors to index
    index.add(embeddings_array)

    # Save index and ID mapping
    faiss.write_index(index, str(index_path))

    # Save ID mapping
    id_map_path = index_path.with_suffix('.ids')
    with open(id_map_path, 'wb') as f:
        pickle.dump(ids, f)

    print(f"✅ FAISS index saved to {index_path}")
    print(f"✅ ID mapping saved to {id_map_path}")

    return index

def test_search(model, index, ids, query="domates"):
    """Test semantic search"""
    print(f"\n🔍 Testing semantic search with query: '{query}'")

    # Encode query
    query_embedding = model.encode([query])

    # Search in index
    k = 5  # Top 5 results
    distances, indices = index.search(query_embedding.astype('float32'), k)

    print(f"Top {k} results:")
    session = SessionLocal()
    for i, (dist, idx) in enumerate(zip(distances[0], indices[0])):
        ing_id = ids[idx]
        ingredient = session.query(Ingredient).filter_by(id=ing_id).first()
        if ingredient:
            print(f"  {i+1}. {ingredient.name} (distance: {dist:.3f})")
    session.close()

def main():
    """Main function"""
    print("Starting embedding generation...")

    # Create output directory
    output_dir = Path(__file__).parent.parent / "data" / "embeddings"
    output_dir.mkdir(parents=True, exist_ok=True)

    # Load model
    model = load_or_create_model()

    # Connect to database
    session = SessionLocal()
    try:
        # Check if database has data
        ingredient_count = session.query(Ingredient).count()
        if ingredient_count == 0:
            print("❌ No ingredients in database. Run seed_database.py first.")
            return

        # Build ingredient embeddings
        ing_embeddings, ing_ids = build_ingredient_embeddings(session, model)

        # Build ingredient FAISS index
        if ing_embeddings:
            ing_index_path = output_dir / "ingredients.index"
            ing_index = build_faiss_index(ing_embeddings, ing_ids, ing_index_path)

            # Test ingredient search
            test_search(model, ing_index, ing_ids)

        # Build recipe embeddings
        recipe_embeddings, recipe_ids = build_recipe_embeddings(session, model)

        # Build recipe FAISS index
        if recipe_embeddings:
            recipe_index_path = output_dir / "recipes.index"
            recipe_index = build_faiss_index(recipe_embeddings, recipe_ids, recipe_index_path)

        print("\n✅ Embedding generation complete!")
        print(f"   Ingredient index: {output_dir / 'ingredients.index'}")
        print(f"   Recipe index: {output_dir / 'recipes.index'}")

    except Exception as e:
        print(f"❌ Error: {e}")
        session.rollback()
    finally:
        session.close()

if __name__ == "__main__":
    main()