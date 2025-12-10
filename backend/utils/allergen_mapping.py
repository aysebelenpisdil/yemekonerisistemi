"""
Allergen Mapping Utility
Maps allergen categories to their derivative ingredients (Turkish and English)
Used for hard filtering recipes that contain allergens
"""
from typing import List, Set


# Allergen category to derivative ingredients mapping
# Each category includes both Turkish and English terms
ALLERGEN_DERIVATIVES = {
    "Süt": [
        # Turkish
        "süt", "krema", "peynir", "yoğurt", "tereyağı", "kaymak", "labne",
        "lor", "çökelek", "ayran", "kefir", "beyaz peynir", "kaşar",
        "tulum peyniri", "hellim", "mascarpone", "ricotta", "mozzarella",
        "parmesan", "cheddar", "brie", "camembert", "feta",
        # English
        "milk", "cream", "cheese", "yogurt", "butter", "dairy", "whey",
        "casein", "lactose", "ghee", "sour cream", "cottage cheese",
        "cream cheese", "buttermilk", "condensed milk", "evaporated milk",
    ],

    "Yumurta": [
        # Turkish
        "yumurta", "yumurta sarısı", "yumurta akı", "mayonez",
        # English
        "egg", "eggs", "egg yolk", "egg white", "mayonnaise", "mayo",
        "meringue", "albumin", "globulin", "lysozyme", "lecithin",
    ],

    "Fındık/Fıstık": [
        # Turkish
        "fındık", "fıstık", "ceviz", "badem", "antep fıstığı", "kaju",
        "yer fıstığı", "çam fıstığı", "pekan", "hindistan cevizi",
        "macadamia", "fındık ezmesi", "badem ezmesi", "fıstık ezmesi",
        # English
        "nut", "nuts", "peanut", "peanuts", "hazelnut", "walnut", "almond",
        "pistachio", "cashew", "pine nut", "pecan", "macadamia", "chestnut",
        "coconut", "nut butter", "almond butter", "peanut butter",
        "nutella", "praline", "marzipan",
    ],

    "Deniz Ürünleri": [
        # Turkish
        "balık", "karides", "midye", "istiridye", "ıstakoz", "yengeç",
        "kalamar", "ahtapot", "levrek", "çipura", "somon", "ton balığı",
        "hamsi", "sardalya", "uskumru", "palamut", "lüfer", "mezgit",
        "barbunya", "trança", "dil balığı", "kalkan", "deniz tarağı",
        "kabuklular", "yumuşakçalar", "balık sosu", "balık yağı",
        # English
        "fish", "seafood", "shrimp", "prawn", "lobster", "crab", "mussel",
        "oyster", "clam", "squid", "octopus", "salmon", "tuna", "cod",
        "anchovy", "sardine", "mackerel", "bass", "trout", "halibut",
        "scallop", "shellfish", "crustacean", "fish sauce", "fish oil",
        "caviar", "roe",
    ],

    "Gluten": [
        # Turkish
        "buğday", "un", "makarna", "ekmek", "simit", "poğaça", "börek",
        "pide", "lahmacun", "gözleme", "yufka", "kadayıf", "baklava",
        "erişte", "bulgur", "irmik", "arpa", "çavdar", "yulaf",
        "galeta unu", "galeta tozu", "kraker", "bisküvi", "pasta",
        "kek", "kurabiye", "mantı", "ravioli",
        # English
        "wheat", "flour", "bread", "pasta", "noodle", "spaghetti",
        "macaroni", "lasagna", "couscous", "bulgur", "semolina",
        "barley", "rye", "oat", "oats", "breadcrumbs", "crouton",
        "cracker", "biscuit", "cookie", "cake", "pastry", "pie crust",
        "soy sauce", "seitan", "beer", "malt",
    ],

    "Soya": [
        # Turkish
        "soya", "soya sosu", "tofu", "edamame", "soya fasulyesi",
        "soya sütü", "miso", "tempeh", "soya unu", "soya yağı",
        # English
        "soy", "soya", "soybean", "soy sauce", "tofu", "edamame",
        "miso", "tempeh", "soy milk", "soy protein", "soy lecithin",
        "soy flour", "soy oil", "textured vegetable protein", "tvp",
    ],

    "Kereviz": [
        # Turkish
        "kereviz", "kereviz sapı", "kereviz kökü", "celeriac",
        # English
        "celery", "celeriac", "celery salt", "celery seed",
    ],

    "Hardal": [
        # Turkish
        "hardal", "hardal tohumu", "hardal sosu",
        # English
        "mustard", "mustard seed", "dijon", "mustard powder",
    ],

    "Susam": [
        # Turkish
        "susam", "tahin", "susam yağı",
        # English
        "sesame", "sesame seed", "tahini", "sesame oil", "halvah",
    ],
}


def get_allergen_derivatives(allergen: str) -> List[str]:
    """
    Get all derivative ingredients for a given allergen category

    Args:
        allergen: Allergen category name (e.g., "Süt", "Yumurta")

    Returns:
        List of derivative ingredient names (lowercase)
    """
    derivatives = ALLERGEN_DERIVATIVES.get(allergen, [])
    return [d.lower() for d in derivatives]


def get_all_allergen_derivatives(allergens: List[str]) -> Set[str]:
    """
    Get all derivative ingredients for a list of allergen categories

    Args:
        allergens: List of allergen category names

    Returns:
        Set of all derivative ingredient names (lowercase)
    """
    all_derivatives = set()
    for allergen in allergens:
        all_derivatives.update(get_allergen_derivatives(allergen))
    return all_derivatives


def contains_allergen(text: str, allergens: List[str]) -> bool:
    """
    Check if text contains any allergen derivatives

    Args:
        text: Text to check (e.g., recipe ingredients or title)
        allergens: List of allergen category names

    Returns:
        True if any allergen derivative is found in text
    """
    if not text or not allergens:
        return False

    text_lower = text.lower()
    derivatives = get_all_allergen_derivatives(allergens)

    for derivative in derivatives:
        if derivative in text_lower:
            return True

    return False


def filter_recipes_by_allergens(recipes: list, allergens: List[str]) -> list:
    """
    Filter out recipes that contain allergen derivatives

    Args:
        recipes: List of recipe dictionaries
        allergens: List of allergen category names

    Returns:
        List of recipes that don't contain any allergens
    """
    if not allergens:
        return recipes

    derivatives = get_all_allergen_derivatives(allergens)
    safe_recipes = []

    for recipe in recipes:
        # Check ingredients field
        ingredients_text = ""
        if isinstance(recipe, dict):
            ingredients_text = str(recipe.get("available_ingredients", "")) + " " + str(recipe.get("ingredients", ""))
        elif hasattr(recipe, "available_ingredients"):
            ingredients_text = str(getattr(recipe, "available_ingredients", ""))

        ingredients_lower = ingredients_text.lower()

        # Check if any allergen derivative is in ingredients
        has_allergen = False
        for derivative in derivatives:
            if derivative in ingredients_lower:
                has_allergen = True
                break

        if not has_allergen:
            safe_recipes.append(recipe)

    return safe_recipes
