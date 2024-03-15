package com.example.clothes_store.data.model

import com.google.gson.annotations.SerializedName

data class Product(
    val id : Int = 0,
    val category : String = "",
    val description : String = "",
    val name : String = "",
    val price : Double = 0.0,
    val image : String = "",
)