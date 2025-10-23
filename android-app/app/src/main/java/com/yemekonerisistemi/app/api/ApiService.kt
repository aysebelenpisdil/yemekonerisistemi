package com.yemekonerisistemi.app.api

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

    /**
     * İsme göre malzeme detayı getir
     * GET /api/ingredients/{name}
     */
    @GET("/api/ingredients/{name}")
    suspend fun getIngredientByName(
        @Query("name") name: String
    ): Response<Ingredient>

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
}

/**
 * API Response Models
 */
data class IngredientSearchResponse(
    val results: List<IngredientDTO>,
    val total: Int,
    val query: String?
)

/**
 * Backend'den gelen malzeme DTO
 */
data class IngredientDTO(
    val name: String,
    val portion_g: Double,
    val calories: Double,
    val fat_g: Double,
    val carbs_g: Double,
    val protein_g: Double,
    val sugar_g: Double,
    val fiber_g: Double
)

/**
 * Recipe Recommendation Request
 */
data class RecipeRecommendationRequest(
    val ingredients: List<String>,
    val dietary_preferences: List<String>? = null,
    val max_cooking_time: Int? = null,
    val max_calories: Int? = null,
    val limit: Int = 20
)

/**
 * Recipe Recommendation Response
 */
data class RecipeRecommendationResponse(
    val recipes: List<Recipe>,
    val total: Int,
    val matched_ingredients: List<String>
)
