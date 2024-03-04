package com.example.clothes_store.data.model

data class Category(
    val id: Int = 0,
    val category: String = "",
)

fun List<Category>.toCategoryStringList(): List<String> {
    return map { it.category }
}
