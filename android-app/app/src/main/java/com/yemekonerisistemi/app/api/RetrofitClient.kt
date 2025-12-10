package com.yemekonerisistemi.app.api

import com.yemekonerisistemi.app.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit Client Singleton
 * Backend API bağlantısı için merkezi yapılandırma
 */
object RetrofitClient {

    // BuildConfig'den URL al - debug/release/flavor'a göre değişir
    private val BASE_URL: String = BuildConfig.API_BASE_URL

    // Logging interceptor (sadece debug modda)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    // OkHttp Client
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("X-Client-Version", BuildConfig.VERSION_NAME)
                .method(original.method, original.body)
                .build()
            chain.proceed(request)
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    // Retrofit Instance
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // API Service
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    /**
     * Debug bilgisi
     */
    fun getBaseUrl(): String = BASE_URL
    fun isDebug(): Boolean = BuildConfig.DEBUG
}
