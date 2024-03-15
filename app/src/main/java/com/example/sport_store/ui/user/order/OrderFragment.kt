package com.example.sport_store.ui.user.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.sport_store.data.model.Cart
import com.example.sport_store.data.model.Order
import com.example.sport_store.databinding.FragmentOrderBinding
import com.example.sport_store.util.AuthManager
import com.example.sport_store.util.Constants
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OrderFragment : Fragment() {
    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OrderViewModel by viewModels()

    @Inject
    lateinit var authManager: AuthManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderBinding.inflate(layoutInflater)
        return binding.root
    }

    private fun allFieldsAreFilled(): Boolean {
        return !(binding.radioButtonCreditCard.isChecked &&
                (binding.editTextCardNumber.text.isNotEmpty() &&
                        binding.editTextExpiryDate.text.isNotEmpty() &&
                        binding.editTextCVV.text.isNotEmpty()) || binding.editTextOrderAddress.text.toString()
            .isEmpty() ||
                binding.editTextOrderPhoneNum.text.toString().isEmpty())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getOrderDetails()
        binding.buttonFinish.setOnClickListener {
            if (allFieldsAreFilled()) submitOrder()
            else Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getOrderDetails() {
        lifecycleScope.launch {
            viewModel.cartItems.collect { state ->
                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Error -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    }

                    is ScreenState.Success -> {

                        if (state.data.isNullOrEmpty()) {
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT)
                                .show()
                        } else displayOrderDetails(state.data)

                    }
                }
            }
        }
    }

    private fun displayOrderDetails(cartList: List<Cart>) {
        val productNames = cartList.joinToString { it.product.name }
        val totalPrice = cartList.sumOf { it.product.price }
        val priceString = String.format("%.2f", totalPrice)
        val priceTag = "$priceString$"
        binding.textViewOrderDetails.text = productNames
        binding.textViewOrderTotalPrice.text = priceTag
        binding.textViewOrderDate.text = Constants.getCurrentDate()
    }

    private fun submitOrder() {
        val products = binding.textViewOrderDetails.text.toString()
        val totalPrice = binding.textViewOrderTotalPrice.text.toString().replace("$", "").toDouble()
        val orderDate = binding.textViewOrderDate.text.toString()
        val address = binding.editTextOrderAddress.text.toString()
        val paymentMethod = if (binding.radioButtonCreditCard.isChecked) "Credit Card" else "Cash"
        val phoneNum = binding.editTextOrderPhoneNum.text.toString()
        val userId = authManager.getUserId()
        val order = Order(
            id = 0,
            totalPrice = totalPrice,
            userId = userId,
            productNames = products,
            date = orderDate,
            address = address,
            paymentMethod = paymentMethod,
            phoneNum = phoneNum,
        )

        lifecycleScope.launch {
            viewModel.order(order)
            viewModel.order.collect { state ->
                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Error -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    }

                    is ScreenState.Success -> {
                        Toast.makeText(context, state.data, Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }

                    else -> {}
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}