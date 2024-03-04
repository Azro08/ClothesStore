package com.example.clothes_store.data.api

import com.example.clothes_store.data.model.Category
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface CategoryService {
    @GET("/api/categories/all")
    suspend fun getAllCategories(): Response<List<Category>>
}
