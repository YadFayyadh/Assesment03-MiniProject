package com.fayyadh0093.miniproject3.network

import com.fayyadh0093.miniproject3.model.Resep
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ResepApiService {
    @GET("/Resep")
    suspend fun getResep(@Query("userId") userId: String): List<Resep>

    @GET("/Resep")
    suspend fun getResepAll(): List<Resep>


    @DELETE("/Resep/{id}")
    suspend fun deleteResep(
        @Path("id") id: String
    )

    @FormUrlEncoded
    @PUT("/Resep/{id}")
    suspend fun updateResep(
        @Path("id") id: String,
        @Field("name") name: String,
        @Field("bahan") bahan: String,
        @Field("langkah") langkah: String,
        @Field("userId") userId: String,
        @Field("imageUrl") imageUrl: String
    ): Resep


    @FormUrlEncoded
    @POST("/Resep")
    suspend fun postResep(
        @Field("name") name: String,
        @Field("bahan") bahan: String,
        @Field("langkah") langkah: String,
        @Field("userId") userId: String,
        @Field("imageUrl") imageUrl: String
    ): Resep

}

    object ResepApi {
        private const val BASE_URL = "https://6841688ed48516d1d35b74c4.mockapi.io"

        private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service: ResepApiService = retrofit.create(ResepApiService::class.java)
    }


    enum class ApiStatus { LOADING, SUCCES, FAILED }
