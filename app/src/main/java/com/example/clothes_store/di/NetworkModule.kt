package com.example.clothes_store.di

import com.example.clothes_store.data.api.AuthService
import com.example.clothes_store.data.api.CartService
import com.example.clothes_store.data.api.CategoryService
import com.example.clothes_store.data.api.OrderService
import com.example.clothes_store.data.api.ProductsService
import com.example.clothes_store.util.Constants
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideRetrofit(): Retrofit {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    fun provideProductsApiService(retrofit: Retrofit): ProductsService =
        retrofit.create(ProductsService::class.java)

    @Provides
    fun provideOrderApiService(retrofit: Retrofit): OrderService =
        retrofit.create(OrderService::class.java)

    @Provides
    fun provideCategoryApiService(retrofit: Retrofit): CategoryService =
        retrofit.create(CategoryService::class.java)

    @Provides
    fun provideCartApiService(retrofit: Retrofit): CartService =
        retrofit.create(CartService::class.java)

    @Provides
    fun provideAuthApiService(retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

}