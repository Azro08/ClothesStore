package com.example.clothes_store.data.repository

import android.annotation.SuppressLint
import android.util.Log
import com.example.clothes_store.data.api.ProductsService
import com.example.clothes_store.data.model.Product
import com.example.clothes_store.util.AuthManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val productsService: ProductsService,
    val authManager: AuthManager
) {

    private val token = authManager.getToken()

    suspend fun getProducts(category: String = "all"): Response<List<Product>> {
        return if (category == "all") {
            Log.d("ProdList", "all")
            productsService.getAllProducts()
        } else productsService.getProductsByCategory(category)
    }

    @SuppressLint("Recycle")
    suspend fun addProduct(product: Product, imageFile: String): Response<String> {

        val nameRequestBody = product.name.toRequestBody("text/plain".toMediaTypeOrNull())
        val priceRequestBody =
            product.price.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val descriptionRequestBody =
            product.description.toRequestBody("text/plain".toMediaTypeOrNull())
        val categoryRequestBody = product.category.toRequestBody("text/plain".toMediaTypeOrNull())

        Log.d("imageFile", imageFile)

        val imageFilePart = MultipartBody.Part.createFormData(
            "imageFile",
            imageFile,
            imageFile.toRequestBody("image/*".toMediaTypeOrNull())
        )

        Log.d("imageFilePart", imageFilePart.toString())

        return productsService.addProduct(
            token = token,
            name = nameRequestBody,
            price = priceRequestBody,
            description = descriptionRequestBody,
            category = categoryRequestBody,
            imageFile = imageFilePart,
        )

    }
    suspend fun getProductDetailsById(id: Int): Response<Product> {
        return productsService.getProductDetailsById(id)
    }

    suspend fun updateProduct(
        id: Int,
        updatedProduct: Product,
        imageFile: String
    ): Response<String> {

        val nameRequestBody = updatedProduct.name.toRequestBody("text/plain".toMediaTypeOrNull())
        val priceRequestBody =
            updatedProduct.price.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val descriptionRequestBody =
            updatedProduct.description.toRequestBody("text/plain".toMediaTypeOrNull())
        val categoryRequestBody =
            updatedProduct.category.toRequestBody("text/plain".toMediaTypeOrNull())
        val idRequestBody =  id.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val requestFile = imageFile.toRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("imageFile", imageFile, requestFile)

        Log.d("imageFilePart", imagePart.toString())
        Log.d("PUT_REPOS", productsService.updateProduct(
            id = id,
            token = token,
            name = nameRequestBody,
            price = priceRequestBody,
            description = descriptionRequestBody,
            category = categoryRequestBody,
            imageFile = imagePart,
        ).body().toString())
        Log.d("PUT_REPOS", productsService.updateProduct(
            id = id,
            token = token,
            name = nameRequestBody,
            price = priceRequestBody,
            description = descriptionRequestBody,
            category = categoryRequestBody,
            imageFile = imagePart,
        ).message().toString())
        return productsService.updateProduct(
            id = id,
            token = token,
            name = nameRequestBody,
            price = priceRequestBody,
            description = descriptionRequestBody,
            category = categoryRequestBody,
            imageFile = imagePart,
        )
    }

    suspend fun deleteProduct(id: Int): Response<String> {
        return productsService.deleteProduct(token, id)
    }

}