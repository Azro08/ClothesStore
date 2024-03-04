package com.example.clothes_store.ui.auth.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clothes_store.data.model.User
import com.example.clothes_store.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _signedUp = MutableStateFlow<String?>(null)
    val signedUp = _signedUp

    fun signUp(user: User) = viewModelScope.launch {
        try {
            authRepository.signup(user).let {
                if (it.isSuccessful) {
                    _signedUp.value = "User signed up successfully"
                } else {
                    _signedUp.value = it.message().toString()
                }
            }
        } catch (e: Exception) {
            Log.e("SignupViewModel", "signUp: ${e.message}")
            _signedUp.value = "Error signing up"
        }
    }


}