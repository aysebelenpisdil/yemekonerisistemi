#!/usr/bin/env python3
"""
Test script for RAG pipeline
Tests multilingual search, LLM, and cache functionality
"""

import sys
import time
sys.path.insert(0, '/Users/belenikov/yemekonerisistemi/backend')

from services.semantic_service import SemanticSearchService
from services.llm_service import OllamaLLMService
from services.rag_service import RAGService
from services.cache_service import get_cache, CacheService


def test_header(title: str):
    print("\n" + "=" * 60)
    print(f"🧪 {title}")
    print("=" * 60)


def test_multilingual_search():
    """Test Turkish query → English recipe matching"""
    test_header("TEST 1: Multilingual Semantic Search")

    service = SemanticSearchService()

    queries = [
        ("tavuklu makarna", "Should find chicken pasta recipes"),
        ("çikolatalı kek", "Should find chocolate cake recipes"),
        ("sebzeli çorba", "Should find vegetable soup recipes"),
        ("biftek", "Should find steak/beef recipes"),
    ]

    all_passed = True

    for query, description in queries:
        print(f"\n📝 Query: '{query}' ({description})")

        results = service.search_recipes(query, limit=5)

        if results:
            print(f"   ✅ Found {len(results)} results:")
            for i, r in enumerate(results[:3], 1):
                print(f"      {i}. {r['title']} (score: {r.get('similarity_score', 0):.4f})")
        else:
            print(f"   ❌ No results found!")
            all_passed = False

    return all_passed


def test_llm_service():
    """Test Ollama LLM connectivity and generation"""
    test_header("TEST 2: LLM Service (Ollama)")

    llm = OllamaLLMService()

    print(f"📡 Model: {llm.model}")
    print(f"📡 Available: {llm.is_available()}")

    if not llm.is_available():
        print("   ❌ LLM not available - skipping generation test")
        return False

    # Test simple generation
    prompt = "Merhaba, ben bir yemek tarifi asistanıyım. Bana tavuk yemeği hakkında bir cümle söyle."
    print(f"\n📝 Prompt: {prompt[:50]}...")

    result = llm.generate(prompt)

    if result.get("error"):
        print(f"   ❌ Error: {result['error']}")
        return False
    else:
        print(f"   ✅ Response: {result['response'][:100]}...")
        print(f"   ⏱️ Latency: {result['latency_ms']:.1f}ms")
        return True


def test_rag_pipeline():
    """Test full RAG pipeline"""
    test_header("TEST 3: RAG Pipeline")

    semantic = SemanticSearchService()
    llm = OllamaLLMService()
    rag = RAGService(semantic_service=semantic, llm_service=llm)

    # Test RAG query
    query = "Akşam yemeği için hızlı bir şey öner"
    print(f"\n📝 Query: '{query}'")

    result = rag.answer(query)

    print(f"\n📖 Answer: {result['answer'][:200]}...")
    print(f"\n📚 Sources ({len(result['sources'])}):")
    for s in result['sources'][:3]:
        print(f"   - {s['title']} (score: {s['similarity_score']:.4f})")
    print(f"\n📊 Confidence: {result['confidence']:.2f}")
    print(f"⏱️ Total Latency: {result['latency_ms']:.1f}ms")

    return len(result['sources']) > 0


def test_cache():
    """Test cache functionality"""
    test_header("TEST 4: Cache Service")

    # Create new cache instance for testing
    cache = CacheService()

    print(f"\n📊 Cache stats: {cache.stats()}")

    # Test basic cache operations
    test_key = "test:key"
    test_value = {"test": "value", "number": 42}

    cache.set(test_key, test_value, ttl=60)
    retrieved = cache.get(test_key)

    if retrieved == test_value:
        print("   ✅ Basic cache set/get works")
    else:
        print("   ❌ Cache set/get failed")
        return False

    # Test translation cache
    title = "Chicken Pasta with Vegetables"
    cached_title = cache.get_translated_title(title)

    if cached_title == title:
        print(f"   ✅ Translation cache returns original (no translator): {cached_title}")
    else:
        print(f"   ⚠️ Unexpected result: {cached_title}")

    # Test with mock translator
    def mock_translator(text):
        return f"TR_{text}"

    translated = cache.get_translated_title("Test Recipe", translator=mock_translator)
    print(f"   ✅ Translation with mock: {translated}")

    # Second call should hit cache
    start = time.time()
    cached = cache.get_translated_title("Test Recipe", translator=mock_translator)
    cache_time = (time.time() - start) * 1000
    print(f"   ✅ Cache hit: {cached} (latency: {cache_time:.2f}ms)")

    return True


def main():
    print("\n" + "🚀" * 30)
    print("   RAG PIPELINE TEST SUITE")
    print("🚀" * 30)

    results = {}

    # Run tests
    results["Multilingual Search"] = test_multilingual_search()
    results["LLM Service"] = test_llm_service()
    results["RAG Pipeline"] = test_rag_pipeline()
    results["Cache Service"] = test_cache()

    # Summary
    test_header("TEST SUMMARY")

    passed = 0
    failed = 0

    for test, result in results.items():
        status = "✅ PASSED" if result else "❌ FAILED"
        print(f"   {test}: {status}")
        if result:
            passed += 1
        else:
            failed += 1

    print(f"\n📊 Results: {passed} passed, {failed} failed")

    if failed == 0:
        print("\n🎉 All tests passed! RAG pipeline is ready.")
    else:
        print("\n⚠️ Some tests failed. Check the output above.")

    return failed == 0


if __name__ == "__main__":
    success = main()
    sys.exit(0 if success else 1)
