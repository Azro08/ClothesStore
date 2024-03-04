package com.example.clothes_store.data.model

data class Order (
    val id : Int = 0,
    val userId : Int = 0,
    val productNames : String = "",
    val totalPrice : Double = 0.0,
    val date : String = "",
    val paymentMethod : String = "",
    val address : String = "",
    val phoneNum : String = "",
)