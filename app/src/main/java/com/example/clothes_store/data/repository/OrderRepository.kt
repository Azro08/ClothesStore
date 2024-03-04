package com.example.clothes_store.data.repository

import com.example.clothes_store.data.api.OrderService
import com.example.clothes_store.data.model.Order
import com.example.clothes_store.util.AuthManager
import retrofit2.Response
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val orderService: OrderService,
    private val authManager: AuthManager
) {

    val token = authManager.getToken()

    suspend fun addOrder(order: Order): Response<String> {
        return orderService.addOrder(token, order)
    }

    suspend fun getAllOrders(): Response<List<Order>> {
        return orderService.getAllOrders()
    }

    suspend fun getOrderById(id: Int): Response<Order> {
        return orderService.getOrderById(id)
    }

    suspend fun getOrdersByUserId(): Response<List<Order>> {
        val userId = authManager.getUserId()
        return orderService.getOrdersByUserId(userId)
    }

    suspend fun deleteOrder(id: Int): Response<String> {
        return orderService.deleteOrder(id)
    }

}