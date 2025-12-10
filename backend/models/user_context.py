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
    - Diet preferences (vegetarian, vegan, gluten-free, keto)
    - Allergen restrictions (hard filter - recipes with allergens are excluded)
    - Cuisine preferences (Turkish, Italian, Asian, Mediterranean)
    - Available ingredients from user's inventory
    - Cooking time and calorie constraints
    """

    diet_types: List[str] = Field(
        default=[],
        description="Diet types: Vejetaryen, Vegan, Glutensiz, Keto"
    )

    allergens: List[str] = Field(
        default=[],
        description="Allergen categories: Fındık/Fıstık, Süt, Yumurta, Deniz Ürünleri, Gluten"
    )

    cuisines: List[str] = Field(
        default=[],
        description="Preferred cuisines: Türk, İtalyan, Asya, Akdeniz"
    )

    available_ingredients: List[str] = Field(
        default=[],
        description="Ingredients available in user's inventory"
    )

    max_cooking_time: Optional[int] = Field(
        default=None,
        description="Maximum cooking time in minutes (null = no limit)"
    )

    max_calories: Optional[int] = Field(
        default=None,
        description="Maximum calories per serving (null = no limit)"
    )

    class Config:
        json_schema_extra = {
            "example": {
                "diet_types": ["Vejetaryen"],
                "allergens": ["Fındık/Fıstık", "Süt"],
                "cuisines": ["Türk", "Akdeniz"],
                "available_ingredients": ["domates", "soğan", "zeytinyağı", "pirinç"],
                "max_cooking_time": 30,
                "max_calories": 500
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
            self.max_calories
        )

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

        if self.max_cooking_time:
            parts.append(f"Maksimum pişirme süresi: {self.max_cooking_time} dakika")

        if self.max_calories:
            parts.append(f"Maksimum kalori limiti: {self.max_calories} kcal")

        if not parts:
            return ""

        return "\n".join(parts)
