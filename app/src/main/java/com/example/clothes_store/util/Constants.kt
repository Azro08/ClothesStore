package com.example.clothes_store.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

object Constants {
    const val SHARED_PREF_NAME = "user_pref"
    const val USER_KEY = "user_key"
    const val USER_ID_KEY = "user_id_key"
    const val USER_ROLE_KEY = "user_role_key"
    const val PRODUCT_ID = "product_id"
    const val USER_ROLE = "user_role"
    const val ADMIN_ROLE = "admin_role"
    const val AUTH_TOKEN_KEY = "auth_token_key"

    const val BASE_URL = "http://192.168.100.38:8088/api/"
    const val BASE_IMAGE_URL = BASE_URL+"products/images/"

    fun generateRandomId(): String {
        val characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val random =
            Random(System.currentTimeMillis()) // Seed the random number generator with the current time

        val randomString = StringBuilder(28)

        for (i in 0 until 28) {
            val randomIndex = random.nextInt(characters.length)
            randomString.append(characters[randomIndex])
        }

        return randomString.toString()
    }

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

}