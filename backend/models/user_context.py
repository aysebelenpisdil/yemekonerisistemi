"""
User Context Model
Contains user preferences and constraints for recipe recommendations
"""
from pydantic import BaseModel, Field
from typing import Optional, List


class UserContext(BaseModel):
    """
    User context for personalized recipe recommendations

    This model combines:
    - Diet preferences (vegetarian, vegan, gluten-free, keto, paleo, low-carb, halal, pescetarian)
    - Allergen restrictions (hard filter - recipes with allergens are excluded)
    - Cuisine preferences (Turkish, Italian, Asian, Mediterranean, Mexican, Indian, Japanese, French, Middle Eastern)
    - Available ingredients from user's inventory
    - Cooking time and calorie constraints (chip-based preferences)
    - Skill levels (beginner, intermediate, advanced)
    - Spice preferences (none, mild, medium, hot)
    - Serving sizes (1-2, 3-4, 5+)
    - Meal types (breakfast, lunch, dinner, snack)
    """

    diet_types: List[str] = Field(
        default=[],
        description="Diet types: Vejetaryen, Vegan, Glutensiz, Keto, Paleo, Düşük Karbonhidrat, Helal, Pescetaryen"
    )

    allergens: List[str] = Field(
        default=[],
        description="Allergen categories: Fındık/Fıstık, Süt, Yumurta, Deniz Ürünleri, Gluten, Soya, Susam, Kereviz, Hardal"
    )

    cuisines: List[str] = Field(
        default=[],
        description="Preferred cuisines: Türk, İtalyan, Asya, Akdeniz, Meksika, Hint, Japon, Fransız, Ortadoğu"
    )

    available_ingredients: List[str] = Field(
        default=[],
        description="Ingredients available in user's inventory"
    )

    max_cooking_time: Optional[int] = Field(
        default=None,
        description="Maximum cooking time in minutes (null = no limit) - legacy field"
    )

    max_calories: Optional[int] = Field(
        default=None,
        description="Maximum calories per serving (null = no limit) - legacy field"
    )

    cooking_time_preferences: List[str] = Field(
        default=[],
        description="Cooking time chip preferences: 15dk, 30dk, 45dk, 60dk, 90+dk"
    )

    calorie_preferences: List[str] = Field(
        default=[],
        description="Calorie chip preferences: 300kcal, 500kcal, 750kcal, 1000kcal, 1500+kcal"
    )

    skill_levels: List[str] = Field(
        default=[],
        description="Skill levels: Başlangıç, Orta, İleri"
    )

    spice_preferences: List[str] = Field(
        default=[],
        description="Spice preferences: Acısız, Az Acılı, Orta, Çok Acılı"
    )

    serving_sizes: List[str] = Field(
        default=[],
        description="Serving sizes: 1-2 kişi, 3-4 kişi, 5+ kişi"
    )

    meal_types: List[str] = Field(
        default=[],
        description="Meal types: Kahvaltı, Öğle, Akşam, Atıştırmalık"
    )

    class Config:
        json_schema_extra = {
            "example": {
                "diet_types": ["Vejetaryen"],
                "allergens": ["Fındık/Fıstık", "Süt"],
                "cuisines": ["Türk", "Akdeniz"],
                "available_ingredients": ["domates", "soğan", "zeytinyağı", "pirinç"],
                "max_cooking_time": 30,
                "max_calories": 500,
                "cooking_time_preferences": ["30dk", "45dk"],
                "calorie_preferences": ["500kcal"],
                "skill_levels": ["Başlangıç", "Orta"],
                "spice_preferences": ["Az Acılı"],
                "serving_sizes": ["3-4 kişi"],
                "meal_types": ["Akşam"]
            }
        }

    def has_restrictions(self) -> bool:
        """Check if user has any dietary restrictions"""
        return bool(self.diet_types or self.allergens)

    def has_preferences(self) -> bool:
        """Check if user has any preferences set"""
        return bool(
            self.diet_types or
            self.allergens or
            self.cuisines or
            self.max_cooking_time or
            self.max_calories or
            self.cooking_time_preferences or
            self.calorie_preferences or
            self.skill_levels or
            self.spice_preferences or
            self.serving_sizes or
            self.meal_types
        )

    def get_max_cooking_time_from_prefs(self) -> Optional[int]:
        """
        Extract maximum cooking time from chip preferences
        Returns the highest selected value, or None if no preferences
        """
        if not self.cooking_time_preferences:
            return self.max_cooking_time  # Fall back to legacy field

        time_map = {
            "15dk": 15,
            "30dk": 30,
            "45dk": 45,
            "60dk": 60,
            "90+dk": 9999  # No limit for 90+
        }

        max_time = 0
        for pref in self.cooking_time_preferences:
            if pref in time_map:
                max_time = max(max_time, time_map[pref])

        if max_time == 0:
            return self.max_cooking_time
        if max_time == 9999:
            return None  # No limit

        return max_time

    def get_max_calories_from_prefs(self) -> Optional[int]:
        """
        Extract maximum calories from chip preferences
        Returns the highest selected value, or None if no preferences
        """
        if not self.calorie_preferences:
            return self.max_calories  # Fall back to legacy field

        cal_map = {
            "300kcal": 300,
            "500kcal": 500,
            "750kcal": 750,
            "1000kcal": 1000,
            "1500+kcal": 9999  # No limit for 1500+
        }

        max_cal = 0
        for pref in self.calorie_preferences:
            if pref in cal_map:
                max_cal = max(max_cal, cal_map[pref])

        if max_cal == 0:
            return self.max_calories
        if max_cal == 9999:
            return None  # No limit

        return max_cal

    def to_system_prompt(self) -> str:
        """
        Generate system prompt section for LLM/RAG queries

        Returns:
            Formatted string describing user's dietary context
        """
        parts = []

        if self.diet_types:
            diets = ", ".join(self.diet_types)
            parts.append(f"Kullanıcının diyet tercihleri: {diets}")

        if self.allergens:
            allergens = ", ".join(self.allergens)
            parts.append(
                f"⚠️ UYARI: Kullanıcının şu alerjileri var: {allergens}. "
                f"Bu malzemeleri ve türevlerini KESİNLİKLE içeren tarifleri önerme!"
            )

        if self.cuisines:
            cuisines = ", ".join(self.cuisines)
            parts.append(f"Tercih edilen mutfaklar: {cuisines}")

        if self.available_ingredients:
            ingredients = ", ".join(self.available_ingredients[:10])
            parts.append(f"Kullanıcının mevcut malzemeleri: {ingredients}")

        # Use chip-based preferences if available, otherwise legacy fields
        max_time = self.get_max_cooking_time_from_prefs()
        if max_time:
            parts.append(f"Maksimum pişirme süresi: {max_time} dakika")

        max_cal = self.get_max_calories_from_prefs()
        if max_cal:
            parts.append(f"Maksimum kalori limiti: {max_cal} kcal")

        if self.skill_levels:
            levels = ", ".join(self.skill_levels)
            parts.append(f"Zorluk seviyesi tercihi: {levels}")

        if self.spice_preferences:
            spice = ", ".join(self.spice_preferences)
            parts.append(f"Baharatlılık tercihi: {spice}")

        if self.serving_sizes:
            servings = ", ".join(self.serving_sizes)
            parts.append(f"Porsiyon tercihi: {servings}")

        if self.meal_types:
            meals = ", ".join(self.meal_types)
            parts.append(f"Öğün tipi: {meals}")

        if not parts:
            return ""

        return "\n".join(parts)
