package com.example.sport_store.data.repository

import com.example.sport_store.data.api.CartService
import com.example.sport_store.data.model.Cart
import com.example.sport_store.util.AuthManager
import retrofit2.Response
import javax.inject.Inject

class CartRepository @Inject constructor(private val cartService: CartService, authManager: AuthManager) {

    private val token = authManager.getToken()

    private val userId = authManager.getUserId()
    suspend fun addToCart(productId: Int): Response<String> {
        return cartService.addToCart(token, userId, productId)
    }

    suspend fun getCartItems(): Response<List<Cart>> {
        return cartService.getCartItems(token, userId)
    }

    suspend fun removeItemFromCart(cartId: Int): Response<String> {
        return cartService.removeItemFromCart(cartId)
    }

    suspend fun removeAllItemsFromCart() {
        cartService.removeAllItemsFromCart(token, userId)
    }
}
