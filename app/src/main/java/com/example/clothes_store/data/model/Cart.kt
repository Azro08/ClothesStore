package com.example.clothes_store.data.model

data class Cart(
    val id : Int = 0,
    val user : User,
    val product: Product
)