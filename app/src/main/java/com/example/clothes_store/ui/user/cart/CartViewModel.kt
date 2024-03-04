package com.example.clothes_store.ui.user.cart

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clothes_store.data.model.Cart
import com.example.clothes_store.data.repository.CartRepository
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _cartItems = MutableStateFlow<ScreenState<List<Cart>?>>(ScreenState.Loading())
    val cartItems = _cartItems

    private val _deleteItem = MutableStateFlow("")
    val deleteItem = _deleteItem

    init {
        getCartItems()
    }

    fun refresh() {
        getCartItems()
    }

    private fun getCartItems() = viewModelScope.launch {
        try {
            cartRepository.getCartItems().let {
                if (it.isSuccessful){
                    _cartItems.value = ScreenState.Success(it.body())
                } else{
                    _cartItems.value = ScreenState.Error(it.message())
                    Log.d("CartFragment", it.message())
                }
            }
        }catch (e: Exception){
            _cartItems.value = ScreenState.Error(e.message.toString())
            Log.d("CartFragment", e.message.toString())
        }
    }


    fun deleteItem(cartId: Int) = viewModelScope.launch {
        try {
            cartRepository.removeItemFromCart(cartId).let {
                if (it.isSuccessful){
                    _deleteItem.value = it.body().toString()
                } else {
                    _deleteItem.value = it.message().toString()
                    Log.d("CartFragment", it.message().toString())
                }
            }
        } catch (e: Exception){
            _deleteItem.value = e.message.toString()
            Log.d("CartFragment", e.message.toString())
        }

    }

}