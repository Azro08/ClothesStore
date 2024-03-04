package com.example.clothes_store.ui.user.product_details

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.clothes_store.R
import com.example.clothes_store.data.model.Product
import com.example.clothes_store.databinding.FragmentProductDetailsBinding
import com.example.clothes_store.util.AuthManager
import com.example.clothes_store.util.Constants
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProductDetailsFragment : DialogFragment() {
    private var _binding: FragmentProductDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductDetailsViewModel by viewModels()

    @Inject
    lateinit var authManager: AuthManager
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentProductDetailsBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this.activity)
        builder.run { setView(binding.root) }
        val productId = arguments?.getInt(Constants.PRODUCT_ID) ?: 0
        Log.d("prodId", productId.toString())
        if (authManager.getUserRole() == Constants.ADMIN_ROLE) {
            binding.buttonAddToCart.visibility = View.GONE
            binding.buttonEditProd.visibility = View.VISIBLE
            binding.buttonEditProd.setOnClickListener {
                val bundle = bundleOf(Pair(Constants.PRODUCT_ID, productId))
                findNavController().navigate(R.id.nav_details_edit, bundle)
            }
        }
        getDetails(productId)
        return builder.create()
    }

    private fun addToCart(productId: Int) {
        lifecycleScope.launch {
            viewModel.addToCart(productId)
            viewModel.addedToCart.collect {
                if (it == "Done") Toast.makeText(
                    requireContext(),
                    "Added to cart",
                    Toast.LENGTH_SHORT
                ).show()
                else Toast.makeText(requireContext(), it ?: "Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getDetails(productId: Int) {
        lifecycleScope.launch {
            viewModel.getProductDetails(productId)
            viewModel.productsDetails.collect { state ->
                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Error -> Toast.makeText(
                        context,
                        state.message ?: "Failed",
                        Toast.LENGTH_SHORT
                    ).show()

                    is ScreenState.Success -> {
                        if (state.data != null) {
                            displayDetails(state.data)
                            binding.buttonAddToCart.setOnClickListener { addToCart(state.data.id) }
                        }
                    }

                }
            }
        }
    }

    private fun displayDetails(product: Product) = with(binding) {
        Log.d("photoLink", product.image)
        Glide.with(binding.root)
            .load(product.image)
            .error(R.drawable.app_logo)
            .into(binding.imageviewDetailsImage)
        textViewDetailsName.text = product.name
        textViewDetailsDescription.text = product.description
        val priceString = String.format("%.2f", product.price)
        val priceTag = "$$priceString"
        textViewDetailsPrice.text = priceTag
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}