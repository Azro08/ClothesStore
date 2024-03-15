package com.example.sport_store.data.api

import com.example.sport_store.data.model.Cart
import retrofit2.Response
import retrofit2.http.*

interface CartService {
    @POST("cart/add")
    @Headers(
        "Content-Type: application/json"
    )
    suspend fun addToCart(
        @Header("Authorization") token: String,
        @Query("userId") userId: Int,
        @Query("productId") productId: Int
    ): Response<String>

    @GET("cart/get")
    @Headers(
        "Content-Type: application/json"
    )
    suspend fun getCartItems(
        @Header("Authorization") token: String,
        @Query("userId") userId: Int
    ): Response<List<Cart>>

    @DELETE("cart/removeCartItem")
    suspend fun removeItemFromCart(
        @Query("cartId") cartId: Int
    ): Response<String>

    @DELETE("cart/removeAllCartItems")
    @Headers(
        "Content-Type: application/json"
    )
    suspend fun removeAllItemsFromCart(
        @Header("Authorization") token: String,
        @Query("userId") userId: Int
    ): Response<String>
}
