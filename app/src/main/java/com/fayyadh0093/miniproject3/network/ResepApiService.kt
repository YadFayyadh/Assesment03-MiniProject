package com.fayyadh0093.miniproject3.network

import com.fayyadh0093.miniproject3.model.OpStatus
import com.fayyadh0093.miniproject3.model.Resep
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

private const val BASE_URL = "https://6841688ed48516d1d35b74c4.mockapi.io"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface ResepApiService {
    @GET("/Resep")
    suspend fun getResep(
        @Header("Authorization") userId: String
    ): List<Resep>

    @DELETE("/Resep")
    suspend fun deleteResep(
        @Header("Authorization") userId: String,
        @Query("id") id: String
    ): OpStatus

    @Multipart
    @POST("/Resep")
    suspend fun postResep(
        @Header("Authorization") userId: String,
        @Part("name") name: RequestBody,
        @Part("bahan") bahan : RequestBody,
        @Part("langkah") langkah : RequestBody,
    ): OpStatus
}

object ResepApi {
    val service: ResepApiService by lazy {
        retrofit.create(ResepApiService::class.java)
    }

    fun getResepUrl(imageId: String): String{
        return "${BASE_URL}image.php?id=$imageId"
    }
}

enum class ApiStatus { LOADING, SUCCES, FAILED}