#!/usr/bin/env python3
"""
Tam tarif veri setini işle ve popularity_score hesapla
"""

import pandas as pd
import json
import re
import ast
from pathlib import Path

# Dosya yolları
PROJECT_ROOT = Path(__file__).parent.parent
CSV_PATH = PROJECT_ROOT / "data" / "Food Ingredients and Recipe Dataset with Image Name Mapping.csv"
OUTPUT_PATH = PROJECT_ROOT / "backend" / "data" / "recipes_en_full.json"

# Yaygın malzemeler (popularity hesabında kullanılacak)
COMMON_INGREDIENTS = {
    'chicken', 'tomato', 'tomatoes', 'onion', 'onions', 'garlic', 'egg', 'eggs',
    'butter', 'salt', 'pepper', 'olive oil', 'cheese', 'milk', 'flour',
    'sugar', 'rice', 'pasta', 'bread', 'beef', 'pork', 'fish', 'shrimp',
    'carrot', 'carrots', 'potato', 'potatoes', 'lemon', 'lime', 'cream',
    'parsley', 'basil', 'oregano', 'thyme', 'rosemary', 'cilantro',
    'soy sauce', 'vinegar', 'honey', 'ginger', 'celery', 'bell pepper',
    'mushroom', 'mushrooms', 'spinach', 'broccoli', 'corn', 'beans'
}

# Kategori boost kelimeleri
CATEGORY_BOOST_KEYWORDS = {
    'easy': 0.1, 'quick': 0.1, 'simple': 0.1,
    'classic': 0.05, 'traditional': 0.05, 'homemade': 0.05,
    'healthy': 0.08, 'light': 0.05,
    'comfort': 0.05, 'family': 0.05,
}


def parse_ingredients(ingredients_str):
    """Malzeme string'ini listeye çevir"""
    if pd.isna(ingredients_str):
        return []
    try:
        # Python list literal olarak parse et
        return ast.literal_eval(ingredients_str)
    except (ValueError, SyntaxError):
        # Başarısız olursa virgülle ayır
        return [i.strip() for i in ingredients_str.split(',')]


def calculate_common_ingredient_ratio(ingredients):
    """Yaygın malzeme oranını hesapla"""
    if not ingredients:
        return 0.0

    count = 0
    ingredients_lower = ' '.join([i.lower() for i in ingredients])

    for common in COMMON_INGREDIENTS:
        if common in ingredients_lower:
            count += 1

    return min(1.0, count / 5)  # 5+ yaygın malzeme = 1.0


def calculate_simplicity_score(ingredients, instructions):
    """Basitlik skorunu hesapla"""
    if not ingredients:
        return 0.5

    ingredient_count = len(ingredients)
    instruction_length = len(str(instructions)) if instructions and not pd.isna(instructions) else 0

    # Az malzeme = daha basit
    ingredient_score = min(1.0, 10 / max(ingredient_count, 1))

    # Kısa talimat = daha basit
    instruction_score = min(1.0, 500 / max(instruction_length, 100))

    return ingredient_score * 0.5 + instruction_score * 0.5


def calculate_category_boost(title):
    """Başlıktaki anahtar kelimelere göre boost hesapla"""
    if not title or pd.isna(title):
        return 0.0

    title_lower = str(title).lower()
    boost = 0.0

    for keyword, value in CATEGORY_BOOST_KEYWORDS.items():
        if keyword in title_lower:
            boost += value

    return min(boost, 0.2)  # Maksimum 0.2 boost


def calculate_popularity_score(title, ingredients, instructions):
    """Toplam popularity skorunu hesapla"""
    common_ratio = calculate_common_ingredient_ratio(ingredients)
    simplicity = calculate_simplicity_score(ingredients, instructions)
    category_boost = calculate_category_boost(title)

    # Ağırlıklı ortalama
    score = (common_ratio * 0.4) + (simplicity * 0.3) + (category_boost * 0.3)

    # 0-1 arasında normalize et
    return round(min(1.0, max(0.0, score)), 4)


def process_recipes():
    """Ana işleme fonksiyonu"""
    print(f"CSV dosyası okunuyor: {CSV_PATH}")

    df = pd.read_csv(CSV_PATH)
    print(f"Toplam {len(df)} tarif bulundu")

    recipes = []

    for idx, row in df.iterrows():
        if idx % 10000 == 0:
            print(f"İşleniyor: {idx}/{len(df)}")

        title = row.get('Title', '')
        ingredients_raw = row.get('Cleaned_Ingredients', row.get('Ingredients', ''))
        instructions = row.get('Instructions', '')
        image_name = row.get('Image_Name', '')

        # Malzemeleri parse et
        ingredients = parse_ingredients(ingredients_raw)

        # Popularity skorunu hesapla
        popularity_score = calculate_popularity_score(title, ingredients, instructions)

        recipe = {
            'id': idx + 1,
            'title': title,
            'ingredients': ingredients,
            'instructions': instructions,
            'image_name': image_name,
            'popularity_score': popularity_score,
            'view_count': 0,
            'favorite_count': 0
        }

        recipes.append(recipe)

    # Output klasörünü oluştur
    OUTPUT_PATH.parent.mkdir(parents=True, exist_ok=True)

    # JSON olarak kaydet
    print(f"JSON dosyası yazılıyor: {OUTPUT_PATH}")
    with open(OUTPUT_PATH, 'w', encoding='utf-8') as f:
        json.dump(recipes, f, ensure_ascii=False, indent=2)

    print(f"İşlem tamamlandı! {len(recipes)} tarif kaydedildi.")

    # İstatistikler
    scores = [r['popularity_score'] for r in recipes]
    print(f"\nPopularity Score İstatistikleri:")
    print(f"  Min: {min(scores):.4f}")
    print(f"  Max: {max(scores):.4f}")
    print(f"  Ortalama: {sum(scores)/len(scores):.4f}")

    # En popüler 5 tarif
    top_5 = sorted(recipes, key=lambda x: x['popularity_score'], reverse=True)[:5]
    print(f"\nEn Yüksek Skorlu 5 Tarif:")
    for r in top_5:
        print(f"  - {r['title']}: {r['popularity_score']}")


if __name__ == "__main__":
    process_recipes()
