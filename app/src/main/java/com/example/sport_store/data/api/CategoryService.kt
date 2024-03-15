package com.example.sport_store.data.api

import com.example.sport_store.data.model.Category
import retrofit2.Response
import retrofit2.http.*

interface CategoryService {
    @GET("/api/categories/all")
    suspend fun getAllCategories(): Response<List<Category>>
}
