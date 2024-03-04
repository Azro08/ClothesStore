package com.example.clothes_store.data.api

import com.example.clothes_store.data.model.Product
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductsService {
    @GET("products/all")
    suspend fun getAllProducts() : Response<List<Product>>

    @Multipart
    @POST("products/add")
    suspend fun addProduct(
        @Header("Authorization") token: String,
        @Part imageFile: MultipartBody.Part,
        @Part("name") name: RequestBody,
        @Part("price") price: RequestBody,
        @Part("description") description: RequestBody,
        @Part("category") category: RequestBody,
    ): Response<String>


    @GET("products/getByCategory")
    suspend fun getProductsByCategory(
        @Query("category") category : String) : Response<List<Product>>

    @GET("products/details/{id}")
    suspend fun getProductDetailsById(
        @Path("id") id: Int) : Response<Product>


    @Multipart
    @PUT("products/update/{productId}")
    suspend fun updateProduct(
        @Path("productId") id : Int,
        @Query("Authorization") token: String,
        @Part("name") name: RequestBody,
        @Part("price") price: RequestBody,
        @Part("description") description: RequestBody,
        @Part("category") category: RequestBody,
        @Part imageFile: MultipartBody.Part,
        ): Response<String>


    @DELETE("products/delete/{id}")
    @Headers(
        "Content-Type: application/json"
    )
    suspend fun deleteProduct(
        @Header("Authorization") token: String,
        @Path("id") id: Int) : Response<String>
}