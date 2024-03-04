package com.example.clothes_store.ui.shared.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clothes_store.R
import com.example.clothes_store.data.model.Category
import com.example.clothes_store.data.model.Product
import com.example.clothes_store.databinding.FragmentUserHomeBinding
import com.example.clothes_store.ui.shared.home.adapter.CategoryRvAdapter
import com.example.clothes_store.ui.shared.home.adapter.ProductsRvAdapter
import com.example.clothes_store.util.AuthManager
import com.example.clothes_store.util.Constants
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UserHomeFragment : Fragment() {
    private var _binding: FragmentUserHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UserHomeViewModel by viewModels()
    private var categoryRvAdapter: CategoryRvAdapter? = null
    private var productsRvAdapter: ProductsRvAdapter? = null
    private var category = "all"

    @Inject
    lateinit var authManager: AuthManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setMenu()
        getProductsCategories()
        getProductsList()
        search()
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshProductsList(category)
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.admin_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.itemAddProduct -> {
                        findNavController().navigate(R.id.nav_home_add_prod)
                    }

                    R.id.itemShowOrderHistory -> {
                        findNavController().navigate(R.id.nav_home_history)
                    }

                    R.id.itemProfile -> {
                        findNavController().navigate(R.id.nav_home_profile)
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun search() {
        binding.editTextSearchProducts.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val searchText = s.toString().trim()
                performSearch(searchText)
            }
        })
    }

    private fun performSearch(query: String) {
        val filteredList = viewModel.filterProductsList(query)
        productsRvAdapter?.updateProductList(filteredList)
    }


    private fun getProductsCategories() {
        lifecycleScope.launch {
            viewModel.categoryList.collect { state ->
                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Error -> {}
                    is ScreenState.Success -> {
                        if (!state.data.isNullOrEmpty()) displayCategories(state.data)
                    }
                }
            }
        }
    }

    private fun displayCategories(categories: List<Category>) {
        categoryRvAdapter = CategoryRvAdapter(categories) {
            category = it.category
            getProductsList()
        }
        binding.rvCategory.setHasFixedSize(true)
        binding.rvCategory.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.rvCategory.adapter = categoryRvAdapter
    }

    private fun getProductsList() {
        lifecycleScope.launch {
            viewModel.getProductList(category)
            viewModel.productsList.collect { state ->
                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Error -> handleError(state.message ?: "No products found")
                    is ScreenState.Success -> {
                        if (!state.data.isNullOrEmpty()) displayProductList(state.data)
                        else binding.rvProducts.visibility  = View.GONE
                    }
                }
            }
        }
    }

    private fun displayProductList(productList: List<Product>) {
        Log.d("rodLis", productList.size.toString())
        if (productList.isEmpty()) {
            binding.rvProducts.visibility = View.GONE
            // Show a message indicating that no products are available

        } else {
            binding.rvProducts.visibility = View.VISIBLE
            productsRvAdapter = ProductsRvAdapter(
                productList,
                authManager.getUserRole(),
                { addItemToCart(it.id) },
                { navToDetails(it.id) },
                { deleteProduct(it.id) })
            binding.rvProducts.setHasFixedSize(true)
            binding.rvProducts.layoutManager = GridLayoutManager(requireContext(), 2)
            binding.rvProducts.adapter = productsRvAdapter
        }
    }


    private fun deleteProduct(id: Int) {
        lifecycleScope.launch {
            viewModel.deleteProduct(id)
            viewModel.productDeleted.collect {
                if (it == "Done") {
                    Toast.makeText(requireContext(), "Product deleted", Toast.LENGTH_SHORT).show()
                    viewModel.refreshProductsList(category)
                } else if (it != null) {
                    Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun navToDetails(productId: Int) {
        findNavController().navigate(
            R.id.nav_prodlist_to_details,
            bundleOf(Pair(Constants.PRODUCT_ID, productId))
        )
    }

    private fun addItemToCart(productId: Int) {
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

    private fun handleError(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        categoryRvAdapter = null
        productsRvAdapter = null
    }
}