package com.yemekonerisistemi.app.api

import com.yemekonerisistemi.app.models.Ingredient
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Backend API Service
 * Base URL: http://localhost:8000 (geliştirme)
 */
interface ApiService {

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
