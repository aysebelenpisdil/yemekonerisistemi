"""
CSV'den JSON'a dönüştürme yardımcı scripti
"""
import csv
import json
from pathlib import Path

def convert_csv_to_json():
    """Yemek veri tabanı CSV'sini JSON'a çevirir"""

    csv_path = Path(__file__).parent.parent.parent / "data" / "Yemek_Veri_Tabani.csv"
    json_path = Path(__file__).parent.parent / "data" / "ingredients.json"

    ingredients = []

    with open(csv_path, 'r', encoding='utf-8') as csv_file:
        csv_reader = csv.DictReader(csv_file)

        for row in csv_reader:
            ingredient = {
                "name": row["Malzeme Adı"],
                "portion_g": float(row["Porsiyon (g)"]),
                "calories": float(row["Enerji (kcal)"]),
                "fat_g": float(row["Yağ (g)"]),
                "carbs_g": float(row["Karbonhidrat (g)"]),
                "protein_g": float(row["Protein (g)"]),
                "sugar_g": float(row["Şeker (g)"]),
                "fiber_g": float(row["Fiber (g)"])
            }
            ingredients.append(ingredient)

    # JSON dosyasını oluştur
    json_path.parent.mkdir(exist_ok=True)
    with open(json_path, 'w', encoding='utf-8') as json_file:
        json.dump(ingredients, json_file, ensure_ascii=False, indent=2)

    print(f"✅ {len(ingredients)} malzeme JSON'a dönüştürüldü")
    print(f"📁 Dosya konumu: {json_path}")

    return ingredients

if __name__ == "__main__":
    convert_csv_to_json()
