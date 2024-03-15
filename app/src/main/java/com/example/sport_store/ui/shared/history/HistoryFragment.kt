package com.example.sport_store.ui.shared.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sport_store.data.model.Order
import com.example.sport_store.databinding.FragmentHistoryBinding
import com.example.sport_store.util.AuthManager
import com.example.sport_store.util.Constants
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HistoryViewModel by viewModels()
    private var rvOrderAdapter: OrdersRvAdapter? = null
    @Inject lateinit var authManager: AuthManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (authManager.getUserRole() == Constants.USER_ROLE) getUserOrdersList()
        else getAllOrdersList()
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun getUserOrdersList() {
        lifecycleScope.launch {
            viewModel.getOrders()
            viewModel.orders.collect { state ->
                Log.d("HistoryFragment", state.message.toString())
                Log.d("HistoryFragment", state.data.toString())
                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Error -> {
                        handleError(state.message!!)
                    }

                    is ScreenState.Success -> {
                        binding.textViewError.visibility = View.GONE
                        binding.rvOrders.visibility = View.VISIBLE
                        if (state.data.isNullOrEmpty()) {
                            handleError("No orders yet")
                        } else displayHistoryList(state.data)

                    }
                }
            }
        }
    }

    private fun getAllOrdersList() {
        lifecycleScope.launch {
            viewModel.getAllOrdersHistory()
            viewModel.orders.collect { state ->
                Log.d("HistoryFragment", state.message.toString())
                Log.d("HistoryFragment", state.data.toString())
                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Error -> {
                        handleError(state.message!!)
                    }

                    is ScreenState.Success -> {
                        binding.textViewError.visibility = View.GONE
                        binding.rvOrders.visibility = View.VISIBLE
                        if (state.data.isNullOrEmpty()) {
                            handleError("No orders yet")
                        } else displayHistoryList(state.data)

                    }
                }
            }
        }
    }

    private fun displayHistoryList(orders: List<Order>) {
        Log.d("HistoryFragment", orders.toString())
        rvOrderAdapter = OrdersRvAdapter(orders)
        binding.rvOrders.setHasFixedSize(true)
        binding.rvOrders.layoutManager = LinearLayoutManager(context)
        binding.rvOrders.adapter = rvOrderAdapter
    }

    private fun handleError(message: String) {
        binding.rvOrders.visibility = View.GONE
        binding.textViewError.visibility = View.VISIBLE
        binding.textViewError.text = message
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        rvOrderAdapter = null
    }

}
