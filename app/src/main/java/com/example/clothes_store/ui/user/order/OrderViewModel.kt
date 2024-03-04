package com.example.clothes_store.ui.user.order

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clothes_store.data.model.Cart
import com.example.clothes_store.data.model.Order
import com.example.clothes_store.data.repository.CartRepository
import com.example.clothes_store.data.repository.OrderRepository
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _cartItems = MutableStateFlow<ScreenState<List<Cart>?>>(ScreenState.Loading())
    val cartItems = _cartItems

    private val _order = MutableStateFlow<ScreenState<String>?>(null)
    val order = _order

    init {
        getCartItems()
    }

    private fun getCartItems() = viewModelScope.launch {
        try {
            cartRepository.getCartItems().let {
                if (it.isSuccessful) {
                    _cartItems.value = ScreenState.Success(it.body())
                } else _cartItems.value = ScreenState.Error(it.message())
            }
        } catch (e: Exception) {
            _cartItems.value = ScreenState.Error(e.message.toString())
        }
    }

    fun order(order: Order) = viewModelScope.launch {
        try {
            orderRepository.addOrder(order).let {
                if (it.isSuccessful) {
                    Log.e("OrderViewModel", it.body().toString())
                    _order.value = ScreenState.Success("Order placed successfully")
                    clearCart()
                } else {
                    _order.value = ScreenState.Error(it.message())
                    Log.e("OrderViewModel", it.message())
                }
            }
        } catch (e: Exception) {
            _order.value = ScreenState.Error(e.message.toString())
            Log.e("OrderViewModel", e.message.toString())
        }
    }

    private fun clearCart() = viewModelScope.launch {
        try {
            cartRepository.removeAllItemsFromCart()
        } catch (e: Exception) {
            _order.value = ScreenState.Error(e.message.toString())
        }
    }

}