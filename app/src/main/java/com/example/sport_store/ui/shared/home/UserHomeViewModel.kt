package com.example.sport_store.ui.shared.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sport_store.data.model.Product
import com.example.sport_store.data.model.Category
import com.example.sport_store.data.repository.CartRepository
import com.example.sport_store.data.repository.ProductCategoryRepository
import com.example.sport_store.data.repository.ProductRepository
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserHomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: ProductCategoryRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _productsList = MutableStateFlow<ScreenState<List<Product>?>>(ScreenState.Loading())
    val productsList = _productsList

    private val _categoryList =
        MutableStateFlow<ScreenState<List<Category>?>>(ScreenState.Loading())
    val categoryList = _categoryList

    private val _addedToCart = MutableStateFlow<String?>(null)
    val addedToCart = _addedToCart

    private val _productDeleted = MutableStateFlow<String?>(null)
    val productDeleted = _productDeleted

    init {
        getCategoriesList()
    }

    fun refreshProductsList(category: String) {
        getProductList(category)
    }

    fun filterProductsList(query: String): List<Product> {
        return when (val currentState = productsList.value) {
            is ScreenState.Success -> {
                currentState.data?.filter { product ->
                    product.name.contains(query, ignoreCase = true)
                } ?: emptyList()
            }

            else -> emptyList()
        }
    }


    private fun getCategoriesList() = viewModelScope.launch {
        try {
            categoryRepository.getAllCategories().let {
                if (it.isSuccessful) {
                    Log.d("CatList", it.body().toString())
                    _categoryList.value = ScreenState.Success(it.body())
                } else {
                    _categoryList.value = ScreenState.Error(it.message().toString())
                    Log.d("CatList", it.message().toString())
                }
            }
        } catch (e: Exception) {
            Log.d("CatList", e.message.toString())
            _categoryList.value = ScreenState.Error(e.message.toString())
        }
    }

    fun getProductList(category: String = "all") = viewModelScope.launch {
        try {
            productRepository.getProducts(category).let {
                if (it.isSuccessful) {
                    Log.d("ProdListS", it.body().toString())
                    _productsList.value = ScreenState.Success(it.body())
                }
                else {
                    Log.d("ProdListF", it.message().toString())
                    _productsList.value = ScreenState.Error(it.message().toString())
                }
            }
        } catch (e: Exception) {
            Log.d("ProdListE", e.message.toString())
            _productsList.value = ScreenState.Error(e.message.toString())
        }
    }

    fun addToCart(productId: Int) = viewModelScope.launch {
        try {
            cartRepository.addToCart(productId).let {
                if (it.isSuccessful) {
                    _addedToCart.value = "Done"
                } else {
                    _addedToCart.value = it.message().toString()
                }
            }
        } catch (e: Exception) {
            _addedToCart.value = e.message.toString()
        }
    }

    fun deleteProduct(productId: Int) = viewModelScope.launch {
        try {
            productRepository.deleteProduct(productId).let {
                if (it.isSuccessful) {
                    Log.d("ProdList", "Product deleted")
                    _productDeleted.value = "Done"
                } else{
                    Log.d("ProdList", it.message().toString())
                    _productDeleted.value = it.message().toString()
                }
            }
        } catch (e: Exception) {
            Log.d("ProdList", e.message.toString())
            _productDeleted.value = e.message.toString()
        }
    }

}