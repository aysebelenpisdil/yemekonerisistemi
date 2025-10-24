#!/usr/bin/env python3
"""
Fuzzy Search Test Script
467 malzeme ile fuzzy search algoritmasını test eder
"""

import sys
from pathlib import Path

# Backend dizinini Python path'e ekle
backend_dir = Path(__file__).parent
sys.path.insert(0, str(backend_dir))

from services.ingredient_service import IngredientService
from utils.search_utils import SearchEngine

def test_fuzzy_search():
    """Fuzzy search algoritmasını test et"""

    print("=" * 80)
    print("🔍 FUZZY SEARCH TESTİ")
    print("=" * 80)

    # IngredientService'i başlat
    service = IngredientService()

    print(f"\n✅ Toplam {len(service.ingredients)} malzeme yüklendi\n")

    # Test sorguları (farklı senaryolar)
    test_queries = [
        # Exact match
        ("domates", "Exact match testi"),

        # Typo (hatalı yazım)
        ("domtes", "Typo tolerance testi"),
        ("domatas", "Typo tolerance testi 2"),

        # Turkish characters
        ("çilek", "Turkish character testi"),
        ("cilek", "Turkish normalized testi"),

        # Partial match
        ("dom", "Partial match testi"),
        ("yum", "Partial match testi 2"),

        # Contains
        ("erik", "Contains testi"),

        # Case insensitive
        ("DOMATES", "Case insensitive testi"),
        ("BiBeR", "Case insensitive testi 2"),

        # Complex queries
        ("kırmızı", "Multi-word testi"),
        ("tavuk göğüs", "Multi-word testi 2"),

        # Edge cases
        ("xyz123", "Bulunamaz testi"),
        ("a", "Tek harf testi"),
    ]

    for query, description in test_queries:
        print(f"\n{'='*80}")
        print(f"📝 {description}: '{query}'")
        print(f"{'='*80}")

        # Arama yap
        results = service.search_ingredients(query, limit=5)

        if results:
            print(f"✅ {len(results)} sonuç bulundu:\n")
            for i, ing in enumerate(results, 1):
                # Relevance score'u hesapla (debug için)
                score = SearchEngine.calculate_relevance(query, ing.name)
                print(f"  {i}. {ing.name:30} (score: {score:.1f})")
        else:
            print("❌ Sonuç bulunamadı")

    print("\n" + "="*80)
    print("✅ TÜM TESTLER TAMAMLANDI!")
    print("="*80)

    # İstatistikler
    print(f"\n📊 İSTATİSTİKLER:")
    print(f"  - Toplam malzeme: {len(service.ingredients)}")
    print(f"  - Veri kaynağı: backend/data/ingredients.json")
    print(f"  - Fuzzy search algoritması: Levenshtein + Similarity + Turkish normalization")
    print(f"  - Threshold: 30.0 (minimum %30 match)")

def test_specific_ingredients():
    """Belirli malzemeleri test et"""

    print("\n" + "="*80)
    print("🎯 BELİRLİ MALZEME TESTLERİ")
    print("="*80)

    service = IngredientService()

    # Kullanıcının yaygın aradığı malzemeler
    common_ingredients = [
        "yumurta", "süt", "un", "şeker", "tuz",
        "domates", "biber", "soğan", "sarımsak",
        "tavuk", "et", "balık", "peynir", "yoğurt"
    ]

    print("\n📋 Yaygın malzemeler:")
    for ingredient in common_ingredients:
        results = service.search_ingredients(ingredient, limit=1)
        status = "✅" if results else "❌"
        print(f"  {status} {ingredient:15} -> {results[0].name if results else 'BULUNAMADI'}")

if __name__ == "__main__":
    test_fuzzy_search()
    test_specific_ingredients()
