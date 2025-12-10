"""
Enhanced Seed Script
Daha fazla örnek tarif ve malzeme ile database'i doldur
"""
import sys
import os
import json
import random
from pathlib import Path

sys.path.append(os.path.dirname(os.path.dirname(os.path.realpath(__file__))))

# Varsayılan görseller
DEFAULT_IMAGES = [
    "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400",
    "https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=400",
    "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400",
    "https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=400",
    "https://images.unsplash.com/photo-1565958011703-44f9829ba187?w=400",
    "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=400",
    "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=400",
]

# Türkçe tarifler
TURKISH_RECIPES = [
    {
        "title": "Karnıyarık",
        "cooking_time": 60,
        "calories": 380,
        "servings": 4,
        "available_ingredients": "Patlıcan, Kıyma, Domates, Biber, Soğan",
        "recommendation_reason": "Geleneksel Türk mutfağından lezzetli bir ana yemek",
        "instructions": [
            "Patlıcanları boylamasına soyun ve tuzlu suda bekletin",
            "Kıymayı soğan ve baharatlarla kavurun",
            "Patlıcanları kızartın",
            "Kıymalı harcı patlıcanların içine doldurun",
            "Fırında 30 dakika pişirin"
        ]
    },
    {
        "title": "İmam Bayıldı",
        "cooking_time": 50,
        "calories": 220,
        "servings": 4,
        "available_ingredients": "Patlıcan, Domates, Biber, Soğan, Sarımsak",
        "recommendation_reason": "Hafif ve sağlıklı zeytinyağlı yemek",
        "instructions": [
            "Patlıcanları alacalı soyun",
            "Soğan ve domatesi kavurun",
            "Patlıcanların içini açın ve harcı doldurun",
            "Zeytinyağı ekleyip fırında pişirin"
        ]
    },
    {
        "title": "Mantı",
        "cooking_time": 90,
        "calories": 450,
        "servings": 6,
        "available_ingredients": "Un, Kıyma, Yoğurt, Sarımsak, Tereyağı",
        "recommendation_reason": "Özel günler için muhteşem bir seçim",
        "instructions": [
            "Hamuru yoğurun ve dinlendirin",
            "Kıymalı iç hazırlayın",
            "Hamuru açın ve küçük kareler kesin",
            "Mantıları şekillendirin",
            "Kaynar suda haşlayın",
            "Sarımsaklı yoğurt ve tereyağlı sos ile servis edin"
        ]
    },
    {
        "title": "Lahmacun",
        "cooking_time": 40,
        "calories": 280,
        "servings": 4,
        "available_ingredients": "Un, Kıyma, Domates, Biber, Maydanoz",
        "recommendation_reason": "Hızlı ve lezzetli bir atıştırmalık",
        "instructions": [
            "Hamuru hazırlayın",
            "Kıymalı harç hazırlayın",
            "Hamuru ince açın",
            "Harcı sürün",
            "Yüksek ısıda pişirin"
        ]
    },
    {
        "title": "Döner Kebap",
        "cooking_time": 120,
        "calories": 520,
        "servings": 6,
        "available_ingredients": "Kuzu Eti, Soğan, Domates, Baharatlar",
        "recommendation_reason": "Klasik Türk lezzeti",
        "instructions": [
            "Eti marine edin",
            "Şişe dizin",
            "Yavaş yavaş pişirin",
            "İnce dilimler halinde kesin",
            "Lavaş veya pide ile servis edin"
        ]
    },
    {
        "title": "Şakşuka",
        "cooking_time": 35,
        "calories": 180,
        "servings": 4,
        "available_ingredients": "Patlıcan, Biber, Domates, Sarımsak, Yoğurt",
        "recommendation_reason": "Hafif ve besleyici meze",
        "instructions": [
            "Sebzeleri küp doğrayın",
            "Zeytinyağında kavurun",
            "Domatesleri ekleyin",
            "Yoğurt ile servis edin"
        ]
    },
    {
        "title": "Çiğ Köfte",
        "cooking_time": 45,
        "calories": 290,
        "servings": 6,
        "available_ingredients": "Bulgur, Domates Salçası, Soğan, Maydanoz, Baharatlar",
        "recommendation_reason": "Vejetaryen dostudur ve lezzetlidir",
        "instructions": [
            "Bulguru ıslatın",
            "Salça ve baharatları ekleyin",
            "İyice yoğurun",
            "Soğan ve maydanozu karıştırın",
            "Marul ile servis edin"
        ]
    },
    {
        "title": "Mercimek Köftesi",
        "cooking_time": 40,
        "calories": 210,
        "servings": 6,
        "available_ingredients": "Mercimek, Bulgur, Soğan, Maydanoz, Yeşil Soğan",
        "recommendation_reason": "Sağlıklı ve ekonomik bir seçim",
        "instructions": [
            "Mercimeği haşlayın",
            "Bulguru ekleyin",
            "Soğanları kavurun",
            "Hepsini karıştırın",
            "Şekil verin"
        ]
    }
]

def generate_recipes_json():
    """Tarifleri JSON olarak kaydet"""
    recipes = []
    
    for idx, recipe in enumerate(TURKISH_RECIPES, start=1):
        recipe["id"] = idx
        recipe["image_url"] = random.choice(DEFAULT_IMAGES)
        recipes.append(recipe)
    
    output_path = Path(__file__).parent.parent / "data" / "recipes.json"
    output_path.parent.mkdir(parents=True, exist_ok=True)
    
    with open(output_path, 'w', encoding='utf-8') as f:
        json.dump(recipes, f, ensure_ascii=False, indent=2)
    
    print(f"✅ {len(recipes)} tarif kaydedildi: {output_path}")
    return recipes

if __name__ == "__main__":
    generate_recipes_json()
