package com.example.sport_store.data.api

import com.example.sport_store.data.model.Order
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface OrderService {
    @POST("orders/add")
    @Headers(
        "Content-Type: application/json"
    )
    suspend fun addOrder(
        @Header("Authorization") token: String,
        @Body orderDto: Order
    ): Response<String>

    @GET("orders/all")
    suspend fun getAllOrders(): Response<List<Order>>

    @GET("orders/getOrder/{id}")
    suspend fun getOrderById(@Path("id") id: Int): Response<Order>

    @GET("orders/getUserOrders/{userId}")
    suspend fun getOrdersByUserId(@Path("userId") userId: Int): Response<List<Order>>

    @DELETE("orders/delete/{id}")
    suspend fun deleteOrder(@Path("id") id: Int): Response<String>

}