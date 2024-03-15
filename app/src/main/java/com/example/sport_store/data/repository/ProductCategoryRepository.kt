package com.example.sport_store.data.repository

import com.example.sport_store.data.api.CategoryService
import com.example.sport_store.data.model.Category
import retrofit2.Response
import javax.inject.Inject

class ProductCategoryRepository @Inject constructor(
    private val categoryService: CategoryService
) {
    suspend fun getAllCategories(): Response<List<Category>> {
        return categoryService.getAllCategories()
    }

}