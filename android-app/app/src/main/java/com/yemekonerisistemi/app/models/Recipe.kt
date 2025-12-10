package com.yemekonerisistemi.app.models

import com.google.gson.annotations.SerializedName

data class Recipe(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("cooking_time")
    val cookingTime: Int, // dakika cinsinden

    @SerializedName("calories")
    val calories: Int,

    @SerializedName("recommendation_reason")
    val recommendationReason: String = "",

    @SerializedName("available_ingredients")
    val availableIngredients: String = "",

    @SerializedName("image_url")
    val imageUrl: String = "",

    @SerializedName("servings")
    val servings: Int = 4,

    @SerializedName("ingredients")
    val ingredients: List<Ingredient> = emptyList(),

    @SerializedName("instructions")
    val instructions: List<String> = emptyList()
)

data class Ingredient(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("amount")
    val amount: String,

    @SerializedName("unit")
    val unit: String,

    @SerializedName("category")
    val category: String = "",

    @SerializedName("is_available")
    val isAvailable: Boolean = false
)

data class Category(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("icon")
    val icon: String,

    @SerializedName("ingredients")
    val ingredients: List<Ingredient> = emptyList()
)