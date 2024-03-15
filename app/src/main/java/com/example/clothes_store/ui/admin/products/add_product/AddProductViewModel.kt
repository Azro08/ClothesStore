package com.example.clothes_store.ui.admin.products.add_product

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clothes_store.data.model.Category
import com.example.clothes_store.data.model.Product
import com.example.clothes_store.data.repository.ProductCategoryRepository
import com.example.clothes_store.data.repository.ProductRepository
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: ProductCategoryRepository
) : ViewModel() {

    private var _addProductState = MutableStateFlow<ScreenState<String>>(ScreenState.Loading())
    val addProductState get() = _addProductState

    private var _updateProductState = MutableStateFlow<ScreenState<String>>(ScreenState.Loading())
    val updateProductState get() = _updateProductState

    private val _productDetails = MutableStateFlow<ScreenState<Product?>>(ScreenState.Loading())
    val productsDetails = _productDetails

    private val _categoryList =
        MutableStateFlow<ScreenState<List<Category>?>>(ScreenState.Loading())
    val categoryList = _categoryList


    init {
        getCategoriesList()
    }

    fun getProductDetails(productId: Int) = viewModelScope.launch {
        try {
            productRepository.getProductDetailsById(productId).let {

                if (it.isSuccessful) {
                    _productDetails.value = ScreenState.Success(it.body())
                } else {
                    if (it.message().isNullOrEmpty()) _productDetails.value =
                        ScreenState.Error("Error")
                    else _productDetails.value = ScreenState.Error(it.message() ?: "Error")
                }

            }
        } catch (e: Exception) {
            _productDetails.value = ScreenState.Error(e.message ?: "Failed")
        }
    }

    fun updateProduct(id: Int, product: Product, imageFile: String) = viewModelScope.launch {
        try {
            productRepository.updateProduct(
                updatedProduct = product,
                id = id,
                imageFile = imageFile
            ).let {
                Log.d("UpdateProductViewModelS", it.body().toString())
                Log.d("UpdateProductViewModelS", it.message().toString())
                if (it.isSuccessful) {
                    Log.d("UpdateProductViewModelS", it.body().toString())
                    _updateProductState.value = ScreenState.Success(it.body()!!)
                } else {
                    Log.d("UpdateProductViewModelF", it.message().toString())
                    if (it.message().isNullOrEmpty()) _updateProductState.value =
                        ScreenState.Error("Error")
                    else _updateProductState.value = ScreenState.Error(it.message() ?: "Error")
                }
            }
        } catch (e: Exception) {
            Log.d("UpdateProductViewModelE", e.message.toString())
            _updateProductState.value = ScreenState.Error(e.message ?: "Failed")
        }
    }

    fun addProduct(product: Product, imageFile: File) = viewModelScope.launch {

        try {
            productRepository.addProduct(product, imageFile).let {
                Log.d("AddProductViewModel", it.toString())
                Log.d("AddProductViewModel", it.message().toString())
                if (it.isSuccessful) {
                    _addProductState.value = ScreenState.Success(it.body()!!)
                } else {
                    if (it.message().isNullOrEmpty()) _addProductState.value =
                        ScreenState.Error("Error")
                    else _addProductState.value = ScreenState.Error(it.message())
                }

            }
        } catch (e: Exception) {
            _addProductState.value = ScreenState.Error(e.message.toString())
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

}