package com.example.mypocketguide.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object NetworkModule {

    @Volatile
    private var currentBaseUrl: String = ""

    @Volatile
    private var api: ExhibitApi? = null

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        isLenient = true
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    /** Устанавливает URL и пересоздаёт API, если адрес изменился. */
    fun setBaseUrl(url: String) {
        synchronized(this) {
            if (currentBaseUrl != url) {
                currentBaseUrl = url
                api = null
            }
        }
    }

    fun getApi(): ExhibitApi {
        return api ?: synchronized(this) {
            api ?: createApi().also { api = it }
        }
    }

    /**
     * Проверка соединения: HEAD-запрос к базовому URL.
     * Возвращает true, если сервер ответил хоть чем-то (не сетевой ошибкой).
     */
    fun ping(url: String): Boolean {
        return try {
            val client = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build()
            val request = okhttp3.Request.Builder()
                .url(url)
                .head()
                .build()
            client.newCall(request).execute().use { it.isSuccessful || it.code in 400..599 }
        } catch (e: Exception) {
            false
        }
    }

    private fun createApi(): ExhibitApi {
        check(currentBaseUrl.isNotBlank()) {
            "NetworkModule.setBaseUrl() must be called before using the API"
        }
        return Retrofit.Builder()
            .baseUrl(currentBaseUrl)
            .client(okHttp)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ExhibitApi::class.java)
    }
}