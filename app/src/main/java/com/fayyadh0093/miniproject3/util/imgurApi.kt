package com.fayyadh0093.miniproject3.util

import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

// Model response Imgur

// Retrofit interface
interface ImgurApi {
    @Multipart
    @POST("3/image")
    suspend fun uploadImage(
        @Header("Authorization") authHeader: String,
        @Part image: MultipartBody.Part
    ): ImgurResponse

    companion object {
        private const val BASE_URL = "https://api.imgur.com/"

        private val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
        }
    }
}
