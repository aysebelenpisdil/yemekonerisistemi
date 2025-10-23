package com.yemekonerisistemi.app.models

data class Recipe(
    val id: Int,
    val title: String,
    val cookingTime: Int, // dakika cinsinden
    val calories: Int,
    val recommendationReason: String,
    val availableIngredients: String,
    val imageUrl: String,
    val servings: Int = 4,
    val ingredients: List<Ingredient> = emptyList(),
    val instructions: List<String> = emptyList()
)

data class Ingredient(
    val id: Int,
    val name: String,
    val amount: String,
    val unit: String,
    val category: String,
    val isAvailable: Boolean = false
)

data class Category(
    val id: Int,
    val name: String,
    val icon: String,
    val ingredients: List<Ingredient>
)