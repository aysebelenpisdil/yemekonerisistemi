#!/usr/bin/env python3
"""
Build multilingual FAISS index for recipe search
Uses paraphrase-multilingual-MiniLM-L12-v2 model
Supports Turkish queries → English recipes
"""

import json
import pickle
import numpy as np
import faiss
from pathlib import Path
from sentence_transformers import SentenceTransformer

# Paths
PROJECT_ROOT = Path(__file__).parent.parent
RECIPES_PATH = PROJECT_ROOT / "backend" / "data" / "recipes_en_full.json"
OUTPUT_DIR = PROJECT_ROOT / "backend" / "data" / "embeddings"
INDEX_PATH = OUTPUT_DIR / "recipes_multilingual.index"
IDS_PATH = OUTPUT_DIR / "recipes_multilingual.ids"

# Model
MODEL_NAME = "paraphrase-multilingual-MiniLM-L12-v2"
BATCH_SIZE = 256
CHECKPOINT_INTERVAL = 5000


def create_recipe_text(recipe: dict) -> str:
    """Create rich text representation for embedding"""
    title = recipe.get('title', '')
    ingredients = recipe.get('ingredients', [])

    # Join ingredients, limit to first 300 chars
    if isinstance(ingredients, list):
        ingredients_text = ', '.join(ingredients)[:300]
    else:
        ingredients_text = str(ingredients)[:300]

    return f"{title}. {ingredients_text}"


def main():
    print("=" * 60)
    print("Building Multilingual Recipe Embeddings")
    print("=" * 60)

    # Load recipes
    print(f"\n📂 Loading recipes from {RECIPES_PATH}...")
    with open(RECIPES_PATH, 'r', encoding='utf-8') as f:
        recipes = json.load(f)
    print(f"✅ Loaded {len(recipes)} recipes")

    # Load model
    print(f"\n🤖 Loading model: {MODEL_NAME}...")
    model = SentenceTransformer(MODEL_NAME)
    embedding_dim = model.get_sentence_embedding_dimension()
    print(f"✅ Model loaded. Embedding dimension: {embedding_dim}")

    # Prepare texts
    print("\n📝 Preparing recipe texts...")
    texts = []
    ids = []
    for recipe in recipes:
        text = create_recipe_text(recipe)
        texts.append(text)
        ids.append(recipe['id'])

    print(f"✅ Prepared {len(texts)} texts")

    # Create embeddings in batches
    print(f"\n🔄 Creating embeddings (batch_size={BATCH_SIZE})...")
    all_embeddings = []

    for i in range(0, len(texts), BATCH_SIZE):
        batch = texts[i:i + BATCH_SIZE]
        embeddings = model.encode(batch, show_progress_bar=False)
        all_embeddings.append(embeddings)

        processed = min(i + BATCH_SIZE, len(texts))
        if processed % CHECKPOINT_INTERVAL == 0 or processed == len(texts):
            print(f"   Processed: {processed}/{len(texts)} ({100*processed/len(texts):.1f}%)")

    # Stack all embeddings
    embeddings_matrix = np.vstack(all_embeddings).astype('float32')
    print(f"✅ Created embeddings matrix: {embeddings_matrix.shape}")

    # Normalize for cosine similarity
    faiss.normalize_L2(embeddings_matrix)

    # Create FAISS index
    print("\n🏗️ Building FAISS index...")
    index = faiss.IndexFlatIP(embedding_dim)  # Inner product for cosine similarity
    index.add(embeddings_matrix)
    print(f"✅ Index contains {index.ntotal} vectors")

    # Save index and IDs
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

    print(f"\n💾 Saving index to {INDEX_PATH}...")
    faiss.write_index(index, str(INDEX_PATH))

    print(f"💾 Saving IDs to {IDS_PATH}...")
    with open(IDS_PATH, 'wb') as f:
        pickle.dump(ids, f)

    print("\n" + "=" * 60)
    print("✅ DONE! Multilingual embeddings created successfully.")
    print(f"   Index: {INDEX_PATH}")
    print(f"   IDs: {IDS_PATH}")
    print(f"   Total vectors: {index.ntotal}")
    print("=" * 60)

    # Quick test
    print("\n🧪 Quick test: searching for 'tavuk yemeği'...")
    test_query = "tavuk yemeği"
    query_embedding = model.encode([test_query])
    faiss.normalize_L2(query_embedding.reshape(1, -1).astype('float32'))

    D, I = index.search(query_embedding.astype('float32'), 5)

    print(f"Top 5 results for '{test_query}':")
    for i, (dist, idx) in enumerate(zip(D[0], I[0])):
        recipe = recipes[idx]
        print(f"   {i+1}. {recipe['title']} (score: {dist:.4f})")


if __name__ == "__main__":
    main()
