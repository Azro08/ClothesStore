package com.example.sport_store.ui.shared.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sport_store.data.model.Order
import com.example.sport_store.data.repository.OrderRepository
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _orders = MutableStateFlow<ScreenState<List<Order>?>>(ScreenState.Loading())
    val orders = _orders



    fun getOrders() = viewModelScope.launch {
        try {
            orderRepository.getOrdersByUserId().let {
                if (it.isSuccessful) {
                    _orders.value = ScreenState.Success(it.body())
                } else {
                    _orders.value = ScreenState.Error(it.message())
                }
            }
        } catch (e: Exception) {
            _orders.value = ScreenState.Error(e.message.toString())
        }
    }

    fun getAllOrdersHistory() = viewModelScope.launch {
        try {
            orderRepository.getAllOrders().let {
                if (it.isSuccessful) {
                    _orders.value = ScreenState.Success(it.body())
                } else {
                    _orders.value = ScreenState.Error(it.message())
                }
            }
        } catch (e: Exception) {
            _orders.value = ScreenState.Error(e.message.toString())
        }
    }

    fun refresh() {
        getOrders()
    }


}