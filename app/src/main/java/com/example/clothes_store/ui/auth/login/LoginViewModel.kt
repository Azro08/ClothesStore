package com.example.clothes_store.ui.auth.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clothes_store.data.model.User
import com.example.clothes_store.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel(){

    private var _loggedIn = MutableStateFlow<User?>(null)
    val loggedIn = _loggedIn

    private var _token = MutableStateFlow<String?>(null)
    val token = _token

    fun login(email : String, password : String) = viewModelScope.launch {
        try {
            authRepository.login(email, password).let {
                Log.d("LoginViewModel", it.toString())
                Log.d("LoginViewModel", it.body().toString())
                Log.d("LoginViewModel", it.message().toString())
                if (it.isSuccessful){
                    _loggedIn.value = it.body()
                    authenticate(email, password)
                }
            }
        } catch (e : Exception){
            Log.d("LoginViewModel", e.message.toString())
        }
    }

    private fun authenticate(email : String, password : String) = viewModelScope.launch {
        try {
            authRepository.authenticate(email, password).let {
                if (it.isSuccessful){
                    _token.value = it.body()
                    Log.d("LoginViewModel", it.body().toString())
                    Log.d("LoginViewModel", it.message().toString())
                }
            }
        } catch (e : Exception){
            Log.d("LoginViewModel", e.message.toString())
        }
    }

}