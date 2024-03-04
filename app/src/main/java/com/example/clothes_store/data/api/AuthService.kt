package com.example.clothes_store.data.api

import com.example.clothes_store.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST


interface AuthService {

    @POST("auth/signin")
    suspend fun login(@Body user: User): Response<User>

    @POST("auth/signup")
    suspend fun signup(@Body user: User): Response<String>

    @POST("auth/authenticate")
    suspend fun authenticate(@Body user: User) : Response<String> //return JWT token
}
