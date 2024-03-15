package com.example.sport_store.util

import android.content.Context

class AuthManager(private val context: Context) {

    fun isLoggedIn(): Boolean {
        val authToken = getUser()
        return authToken.isNotEmpty()
    }

    //returns Email
    private fun getUser(): String {
        val sharedPreferences =
            context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(Constants.USER_KEY, "") ?: ""
    }

    fun getUserRole(): String {
        val sharedPreferences =
            context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(Constants.USER_ROLE_KEY, "") ?: ""
    }

    fun getUserId(): Int {
        val sharedPreferences =
            context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(Constants.USER_ID_KEY, 0)
    }

    fun saveToken(token: String) {
        val sharedPreferences =
            context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(Constants.AUTH_TOKEN_KEY, token).apply()
    }

    fun getToken(): String {
        val sharedPreferences =
            context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(Constants.AUTH_TOKEN_KEY, "") ?: ""
    }

    fun removeToken(){
        val sharedPreferences =
            context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(Constants.AUTH_TOKEN_KEY).apply()
    }

    fun saveUer(email: String, id : Int, role : String) {
        val sharedPreferences =
            context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(Constants.USER_KEY, email).apply()
        sharedPreferences.edit().putString(Constants.USER_ROLE_KEY, role).apply()
        sharedPreferences.edit().putInt(Constants.USER_ID_KEY, id).apply()
    }

    fun removeUser() {
        val sharedPreferences =
            context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(Constants.USER_KEY).apply()
        sharedPreferences.edit().remove(Constants.USER_ID_KEY).apply()
    }
}
