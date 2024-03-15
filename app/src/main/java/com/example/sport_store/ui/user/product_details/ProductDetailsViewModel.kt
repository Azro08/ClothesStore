package com.example.sport_store.ui.user.product_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sport_store.data.model.Product
import com.example.sport_store.data.repository.CartRepository
import com.example.sport_store.data.repository.ProductRepository
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _productDetails = MutableStateFlow<ScreenState<Product?>>(ScreenState.Loading())
    val productsDetails = _productDetails

    private val _addedToCart = MutableStateFlow<String?>(null)
    val addedToCart = _addedToCart

    fun getProductDetails(productId: Int) = viewModelScope.launch {
        try {
            productRepository.getProductDetailsById(productId).let {

                if (it.isSuccessful) {
                    _productDetails.value = ScreenState.Success(it.body())
                } else {
                    if (it.message().isNullOrEmpty()) _productDetails.value = ScreenState.Error("Error")
                    else _productDetails.value = ScreenState.Error(it.message() ?: "Error")
                }

            }
        } catch (e: Exception) {
            _productDetails.value = ScreenState.Error(e.message ?: "Failed")
        }
    }

    fun addToCart(productId: Int) = viewModelScope.launch {
        try {
            cartRepository.addToCart(productId).let {
                if (it.isSuccessful) {
                    _addedToCart.value = "Added to cart"
                } else {
                    _addedToCart.value = it.message()
                }
            }
        } catch (e: Exception) {
            _addedToCart.value = e.message
        }
    }

}