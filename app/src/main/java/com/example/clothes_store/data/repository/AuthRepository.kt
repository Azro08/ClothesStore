package com.example.clothes_store.data.repository

import com.example.clothes_store.data.api.AuthService
import com.example.clothes_store.data.model.User
import retrofit2.Response
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authService: AuthService
) {


    suspend fun login(email: String, password: String): Response<User> {
        val user = User(
            email = email,
            password = password
        )
        return authService.login(user)
    }

    suspend fun authenticate(email: String, password: String) : Response<String>{
        val user = User(
            email = email,
            password = password
        )
        return authService.authenticate(user)
    }

    suspend fun signup(user: User): Response<String> {
        return authService.signup(user)
    }

}