"""
Test script for database and semantic search implementation
"""
import sys
import os
import time
import requests

# Add parent directory to path
sys.path.append(os.path.dirname(os.path.dirname(os.path.realpath(__file__))))

from db.base import SessionLocal
from db.models import Ingredient, Recipe

def test_database():
    """Test database connectivity and data"""
    print("\n=== Testing Database ===")
    session = SessionLocal()
    try:
        # Count ingredients
        ingredient_count = session.query(Ingredient).count()
        recipe_count = session.query(Recipe).count()

        print(f"✅ Database connected")
        print(f"   Ingredients: {ingredient_count}")
        print(f"   Recipes: {recipe_count}")

        if ingredient_count == 0:
            print("⚠️ No ingredients found. Run seed_database.py first.")
            return False

        # Sample query
        sample = session.query(Ingredient).first()
        if sample:
            print(f"✅ Sample ingredient: {sample.name}")

        return True
    except Exception as e:
        print(f"❌ Database error: {e}")
        return False
    finally:
        session.close()

def test_fuzzy_search(base_url="http://localhost:8000"):
    """Test fuzzy search endpoint"""
    print("\n=== Testing Fuzzy Search ===")
    try:
        # Test with typo
        start = time.time()
        response = requests.get(f"{base_url}/api/ingredients?q=domtes&limit=5")
        latency = (time.time() - start) * 1000

        if response.status_code == 200:
            data = response.json()
            print(f"✅ Fuzzy search working")
            print(f"   Query: 'domtes' (typo)")
            print(f"   Results: {data['total']}")
            print(f"   Latency: {latency:.1f}ms")
            if data['results']:
                print(f"   Top match: {data['results'][0]['name']}")
            return True, latency
        else:
            print(f"❌ Fuzzy search failed: {response.status_code}")
            return False, 0
    except Exception as e:
        print(f"❌ Fuzzy search error: {e}")
        return False, 0

def test_semantic_search(base_url="http://localhost:8000"):
    """Test semantic search endpoint"""
    print("\n=== Testing Semantic Search ===")
    try:
        # Check status first
        status_response = requests.get(f"{base_url}/api/semantic/status")
        if status_response.status_code == 200:
            status = status_response.json()
            print(f"   Model loaded: {status['model_loaded']}")
            print(f"   Index loaded: {status['index_loaded']}")
            print(f"   Index size: {status['index_size']}")

            if not status['available']:
                print("⚠️ Semantic search not available. Run build_embeddings.py first.")
                return False, 0

        # Test search
        start = time.time()
        response = requests.get(f"{base_url}/api/semantic/search?q=domates&limit=5")
        latency = (time.time() - start) * 1000

        if response.status_code == 200:
            data = response.json()
            print(f"✅ Semantic search working")
            print(f"   Query: 'domates'")
            print(f"   Results: {data['total']}")
            print(f"   Latency: {latency:.1f}ms")
            if data['results']:
                print(f"   Top match: {data['results'][0]['ingredient']['name']} (similarity: {data['results'][0]['similarity']})")
            if data.get('explanation'):
                print(f"   Explanation: {data['explanation'][:100]}...")
            return True, latency
        else:
            print(f"❌ Semantic search failed: {response.status_code}")
            return False, 0
    except Exception as e:
        print(f"❌ Semantic search error: {e}")
        return False, 0

def main():
    """Run all tests"""
    print("🧪 Starting Implementation Tests")
    print("=" * 50)

    # Test database
    db_ok = test_database()

    # Test API (assuming server is running)
    print("\n⚠️ Testing API endpoints...")

    # Test fuzzy search
    fuzzy_ok, fuzzy_latency = test_fuzzy_search()

    # Test semantic search
    semantic_ok, semantic_latency = test_semantic_search()

    # Summary
    print("\n" + "=" * 50)
    print("📊 Test Summary:")
    print(f"   Database: {'✅ PASS' if db_ok else '❌ FAIL'}")
    print(f"   Fuzzy Search: {'✅ PASS' if fuzzy_ok else '❌ FAIL'}")
    if fuzzy_ok:
        print(f"      Latency: {fuzzy_latency:.1f}ms")
    print(f"   Semantic Search: {'✅ PASS' if semantic_ok else '❌ FAIL'}")
    if semantic_ok:
        print(f"      Latency: {semantic_latency:.1f}ms {'✅' if semantic_latency < 600 else '⚠️ >600ms'}")

    # Performance check
    if fuzzy_ok and semantic_ok:
        print("\n📈 Performance Metrics:")
        print(f"   Fuzzy search latency: {fuzzy_latency:.1f}ms")
        print(f"   Semantic search latency: {semantic_latency:.1f}ms")
        if semantic_latency < 600:
            print("   ✅ Semantic search meets <600ms target")
        else:
            print("   ⚠️ Semantic search exceeds 600ms target")

if __name__ == "__main__":
    main()