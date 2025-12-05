"""
CSV'den Recipe JSON dosyasi olusturma scripti.
Food Ingredients and Recipe Dataset'i isle ve recipes_en.json olustur.
"""
import pandas as pd
import json
import re
from pathlib import Path


def split_instructions_to_sentences(text: str) -> list:
    """
    Uzun instruction metnini cumlelere bol.
    Nokta, unlem veya soru isareti ile biten cumleleri ayir.
    """
    if not text or not isinstance(text, str):
        return []

    # Cumleleri ayir (., !, ? ile biten)
    sentences = re.split(r'(?<=[.!?])\s+', text.strip())

    # Bos cumleleri filtrele ve temizle
    cleaned = [s.strip() for s in sentences if s.strip()]

    return cleaned


def process_csv_to_recipes(csv_path: Path, output_path: Path, limit: int = 50) -> int:
    """
    CSV dosyasini oku ve Recipe formatinda JSON'a donustur.

    Args:
        csv_path: Kaynak CSV dosyasi
        output_path: Hedef JSON dosyasi
        limit: Islenecek maksimum satir sayisi

    Returns:
        Islenen tarif sayisi
    """
    # CSV'yi oku
    df = pd.read_csv(csv_path, nrows=limit)

    recipes = []

    for idx, row in df.iterrows():
        # Image URL olustur
        image_name = row.get('Image_Name', '')
        if pd.isna(image_name) or not image_name:
            image_url = ""
        else:
            image_url = f"http://10.0.2.2:8000/static/images/{image_name}.jpg"

        # Instructions'i cumlelere bol
        instructions_text = row.get('Instructions', '')
        if pd.isna(instructions_text):
            instructions_text = ""
        instructions = split_instructions_to_sentences(str(instructions_text))

        # Cleaned_Ingredients'i al
        cleaned_ingredients = row.get('Cleaned_Ingredients', '')
        if pd.isna(cleaned_ingredients):
            cleaned_ingredients = ""

        # Title'i al
        title = row.get('Title', '')
        if pd.isna(title):
            title = f"Recipe {idx + 1}"

        # Recipe dict olustur
        recipe = {
            "id": idx + 1,
            "title": str(title),
            "cooking_time": 30,  # Varsayilan deger
            "calories": 300,     # Varsayilan deger
            "servings": 4,       # Varsayilan deger
            "recommendation_reason": None,
            "available_ingredients": str(cleaned_ingredients),
            "image_url": image_url,
            "instructions": instructions
        }

        recipes.append(recipe)

    # JSON'a kaydet
    output_path.parent.mkdir(parents=True, exist_ok=True)
    with open(output_path, 'w', encoding='utf-8') as f:
        json.dump(recipes, f, ensure_ascii=False, indent=2)

    return len(recipes)


def main():
    # Dosya yollarini belirle
    script_dir = Path(__file__).parent
    project_root = script_dir.parent.parent

    csv_path = project_root / "data" / "Food Ingredients and Recipe Dataset with Image Name Mapping.csv"
    output_path = script_dir.parent / "data" / "recipes_en.json"

    # CSV dosyasinin varligini kontrol et
    if not csv_path.exists():
        print(f"HATA: CSV dosyasi bulunamadi: {csv_path}")
        return

    # Tarifleri isle
    count = process_csv_to_recipes(csv_path, output_path, limit=50)

    print(f"✅ {count} tarif islendi ve kaydedildi")
    print(f"   Cikti: {output_path}")


if __name__ == "__main__":
    main()
