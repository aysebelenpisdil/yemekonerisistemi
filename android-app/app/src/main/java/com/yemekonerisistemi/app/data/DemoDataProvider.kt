package com.yemekonerisistemi.app.data

import com.yemekonerisistemi.app.models.Ingredient
import com.yemekonerisistemi.app.models.InventoryItem
import com.yemekonerisistemi.app.models.Recipe

/**
 * Merkezi Demo Data Provider
 * Tüm demo/fallback verileri tek bir yerden yönetir
 * DRY prensibine uygun
 */
object DemoDataProvider {

    // Varsayılan görseller (Backend'den gelmezse kullanılır)
    private val DEFAULT_IMAGES = listOf(
        "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400",
        "https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=400",
        "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400",
        "https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=400",
        "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=400"
    )

    fun getRandomImageUrl(): String = DEFAULT_IMAGES.random()

    // ========== DEMO RECIPES ==========

    fun getDemoRecipes(): List<Recipe> = listOf(
        Recipe(
            id = 1,
            title = "Tavuk Sote",
            cookingTime = 30,
            calories = 280,
            servings = 4,
            recommendationReason = "Envanterindeki tavuk ve sebzelerle mükemmel uyum!",
            availableIngredients = "Tavuk, Domates, Biber",
            imageUrl = DEFAULT_IMAGES[0]
        ),
        Recipe(
            id = 2,
            title = "Menemen",
            cookingTime = 15,
            calories = 220,
            servings = 2,
            recommendationReason = "Yumurta ve domateslerinle hızlı kahvaltı!",
            availableIngredients = "Yumurta, Domates, Biber",
            imageUrl = DEFAULT_IMAGES[1]
        ),
        Recipe(
            id = 3,
            title = "Sebze Çorbası",
            cookingTime = 25,
            calories = 150,
            servings = 4,
            recommendationReason = "Sebzelerini değerlendirmek için ideal",
            availableIngredients = "Domates, Soğan, Biber",
            imageUrl = DEFAULT_IMAGES[2]
        ),
        Recipe(
            id = 4,
            title = "Kremalı Makarna",
            cookingTime = 20,
            calories = 420,
            servings = 2,
            recommendationReason = "Hızlı yemek tercihine uygun!",
            availableIngredients = "Makarna, Yoğurt",
            imageUrl = DEFAULT_IMAGES[3]
        ),
        Recipe(
            id = 5,
            title = "Kuru Fasulye",
            cookingTime = 90,
            calories = 350,
            servings = 6,
            recommendationReason = "Bu hafta en çok yapılan tarif!",
            availableIngredients = "Fasulye, Soğan",
            imageUrl = DEFAULT_IMAGES[4]
        ),
        Recipe(
            id = 6,
            title = "Mercimek Çorbası",
            cookingTime = 35,
            calories = 180,
            servings = 4,
            recommendationReason = "Klasik lezzet, her zaman favoride",
            availableIngredients = "Mercimek, Soğan",
            imageUrl = DEFAULT_IMAGES[0]
        )
    )

    fun getRecommendedRecipes(): List<Recipe> = getDemoRecipes().take(3)

    fun getTrendingRecipes(): List<Recipe> = getDemoRecipes().drop(3)

    // ========== DEMO RECIPE DETAIL ==========

    fun getDemoRecipeDetail(): Recipe = Recipe(
        id = 1,
        title = "Tavuk Sote",
        cookingTime = 30,
        calories = 280,
        servings = 4,
        recommendationReason = "Bu tarif envanterinizdeki tavuk, domates ve biber ile mükemmel uyum sağlıyor. " +
                "Ayrıca günlük kalori hedefinize uygun ve protein değeri yüksek.",
        availableIngredients = "Tavuk, Domates, Biber",
        imageUrl = DEFAULT_IMAGES[0],
        instructions = listOf(
            "Tavuk göğüslerini küp şeklinde doğrayın ve tuzlayın.",
            "Domatesleri ve biberleri küp şeklinde doğrayın.",
            "Soğanı ince ince doğrayın.",
            "Tavada sıvı yağı kızdırın ve tavukları ekleyin.",
            "Tavuklar renk alana kadar kavurun (yaklaşık 5-7 dakika).",
            "Soğanları ekleyip pembeleşene kadar kavurun.",
            "Domatesleri ve biberleri ekleyin.",
            "Kapağını kapatıp kısık ateşte 15-20 dakika pişirin.",
            "Tuz ve karabiberle tatlandırın.",
            "Sıcak servis yapın. Afiyet olsun!"
        )
    )

    fun getDemoIngredients(): List<Ingredient> = listOf(
        Ingredient(1, "Tavuk Göğsü", "500", "gram", "Et", isAvailable = true),
        Ingredient(2, "Domates", "3", "adet", "Sebze", isAvailable = true),
        Ingredient(3, "Yeşil Biber", "2", "adet", "Sebze", isAvailable = true),
        Ingredient(4, "Soğan", "1", "adet", "Sebze", isAvailable = true),
        Ingredient(5, "Sıvı Yağ", "2", "yemek kaşığı", "Yağ", isAvailable = false),
        Ingredient(6, "Tuz", "1", "çay kaşığı", "Baharat", isAvailable = true),
        Ingredient(7, "Karabiber", "1", "çay kaşığı", "Baharat", isAvailable = false)
    )

    // ========== DEMO INVENTORY ==========

    fun getDemoInventoryItems(): List<InventoryItem> = listOf(
        InventoryItem(name = "Yumurta", quantity = 6, unit = "adet"),
        InventoryItem(name = "Domates", quantity = 3, unit = "adet"),
        InventoryItem(name = "Tavuk Göğsü", quantity = 2, unit = "adet"),
        InventoryItem(name = "Biber", quantity = 4, unit = "adet"),
        InventoryItem(name = "Soğan", quantity = 2, unit = "adet")
    )

    fun getDemoInventoryCount(): Int = 12
}
