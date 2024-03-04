package com.example.clothes_store.data.repository

import android.net.Uri
import com.example.clothes_store.data.api.CategoryService
import com.example.clothes_store.data.model.Category
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class ProductCategoryRepository @Inject constructor(
    private val categoryService: CategoryService
) {
    suspend fun getAllCategories(): Response<List<Category>> {
        return categoryService.getAllCategories()
    }

}