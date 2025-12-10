package com.yemekonerisistemi.app.api

import com.google.gson.annotations.SerializedName
import com.yemekonerisistemi.app.models.Ingredient
import com.yemekonerisistemi.app.models.Recipe
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Backend API Service
 * Base URL: http://localhost:8000 (geliştirme)
 */
interface ApiService {

    // ========== INGREDIENTS ==========

    /**
     * Malzeme ara
     * GET /api/ingredients/?q=search&limit=50
     */
    @GET("/api/ingredients/")
    suspend fun searchIngredients(
        @Query("q") query: String? = null,
        @Query("limit") limit: Int = 50
    ): Response<IngredientSearchResponse>

    /**
     * Tüm malzeme isimlerini getir (autocomplete için)
     * GET /api/ingredients/names
     */
    @GET("/api/ingredients/names")
    suspend fun getIngredientNames(): Response<List<String>>

    // ========== RECIPES ==========

    /**
     * Tarif listesi getir (öneriler)
     * POST /api/recipes/recommend
     */
    @POST("/api/recipes/recommend")
    suspend fun getRecipeRecommendations(
        @Body request: RecipeRecommendationRequest
    ): Response<RecipeRecommendationResponse>

    /**
     * Tarif ara
     * GET /api/recipes/search?q=keyword&limit=20
     */
    @GET("/api/recipes/search")
    suspend fun searchRecipes(
        @Query("q") query: String,
        @Query("limit") limit: Int = 20
    ): Response<List<Recipe>>

    /**
     * Tarif detayı getir
     * GET /api/recipes/{id}
     */
    @GET("/api/recipes/{id}")
    suspend fun getRecipeById(
        @Path("id") recipeId: Int
    ): Response<Recipe>

    // ========== SEMANTIC SEARCH ==========

    /**
     * Doğal dil ile semantik arama
     * POST /api/semantic/search
     */
    @POST("/api/semantic/search")
    suspend fun semanticSearch(
        @Body request: SemanticSearchRequest
    ): Response<SemanticSearchResponse>

    /**
     * Semantik malzeme önerisi
     * GET /api/semantic/suggest?q=query
     */
    @GET("/api/semantic/suggest")
    suspend fun semanticSuggest(
        @Query("q") query: String,
        @Query("limit") limit: Int = 10
    ): Response<List<String>>
}

// ========== REQUEST/RESPONSE MODELS ==========

data class IngredientSearchResponse(
    @SerializedName("results")
    val results: List<IngredientDTO>,

    @SerializedName("total")
    val total: Int,

    @SerializedName("query")
    val query: String?
)

data class IngredientDTO(
    @SerializedName("name")
    val name: String,

    @SerializedName("portion_g")
    val portionG: Double = 0.0,

    @SerializedName("calories")
    val calories: Double = 0.0,

    @SerializedName("fat_g")
    val fatG: Double = 0.0,

    @SerializedName("carbs_g")
    val carbsG: Double = 0.0,

    @SerializedName("protein_g")
    val proteinG: Double = 0.0,

    @SerializedName("sugar_g")
    val sugarG: Double = 0.0,

    @SerializedName("fiber_g")
    val fiberG: Double = 0.0
)

data class RecipeRecommendationRequest(
    @SerializedName("ingredients")
    val ingredients: List<String>,

    @SerializedName("dietary_preferences")
    val dietaryPreferences: List<String>? = null,

    @SerializedName("max_cooking_time")
    val maxCookingTime: Int? = null,

    @SerializedName("max_calories")
    val maxCalories: Int? = null,

    @SerializedName("limit")
    val limit: Int = 20
)

data class RecipeRecommendationResponse(
    @SerializedName("recipes")
    val recipes: List<Recipe>,

    @SerializedName("total")
    val total: Int,

    @SerializedName("matched_ingredients")
    val matchedIngredients: List<String>
)

data class SemanticSearchRequest(
    @SerializedName("query")
    val query: String,

    @SerializedName("search_type")
    val searchType: String = "recipes",

    @SerializedName("limit")
    val limit: Int = 10
)

data class SemanticSearchResponse(
    @SerializedName("results")
    val results: List<Recipe>,

    @SerializedName("query")
    val query: String,

    @SerializedName("search_type")
    val searchType: String,

    @SerializedName("total")
    val total: Int
)
