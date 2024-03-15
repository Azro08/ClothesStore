package com.example.clothes_store.ui.admin.products.add_product

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.clothes_store.R
import com.example.clothes_store.data.model.Product
import com.example.clothes_store.data.model.toCategoryStringList
import com.example.clothes_store.databinding.FragmentAddProductBinding
import com.example.clothes_store.util.Constants
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class AddProductFragment : Fragment() {
    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddProductViewModel by viewModels()
    private lateinit var pickMedia: ActivityResultLauncher<String>
    private var imageUri = Uri.parse("")
    private var productId: Int? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setMediaPicker()
    }

    private fun setMediaPicker() {
        pickMedia = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                Glide.with(binding.root).load(uri).error(R.drawable.add_img_icon)
                    .into(binding.imageViewAddProdImage)
                imageUri = uri
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddProductBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getProductsCategories()
        productId = arguments?.getInt(Constants.PRODUCT_ID)
        productId?.let {
            getDetails(it)
        }
        binding.buttonSaveProduct.setOnClickListener {
            if (!areAllFieldsFilled()) Toast.makeText(
                context, "Please fill all fields", Toast.LENGTH_SHORT
            ).show()
            else saveProduct()
        }
        binding.imageViewAddProdImage.setOnClickListener {
            setProfileImage()
        }
    }

    private fun getProductsCategories() {
        lifecycleScope.launch {
            viewModel.categoryList.collect { state ->
                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Error -> {}

                    is ScreenState.Success -> {
                        displayCategories(state.data!!.toCategoryStringList())
                    }
                }
            }
        }
    }

    private fun displayCategories(categoryList: List<String>) {
        val categoryAdapter = ArrayAdapter(
            requireContext(),
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            categoryList
        )
        binding.spinnerCategory.adapter = categoryAdapter
    }

    private fun getFilePathFromUri(uri: Uri): String? {
        val contentResolver = requireActivity().contentResolver
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        return cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            it.getString(columnIndex)
        }
    }

    private fun getDetails(id: Int) {
        lifecycleScope.launch {
            lifecycleScope.launch {
                viewModel.getProductDetails(id)
                viewModel.productsDetails.collect { state ->
                    when (state) {
                        is ScreenState.Loading -> {}
                        is ScreenState.Error -> Toast.makeText(
                            context, state.message ?: "Failed", Toast.LENGTH_SHORT
                        ).show()

                        is ScreenState.Success -> {
                            if (state.data != null) {
                                displayDetails(state.data)
                            }
                        }

                    }
                }
            }
        }
    }

    private fun displayDetails(product: Product) {
        val imageUrl = Constants.BASE_IMAGE_URL + product.image
        imageUri = Uri.parse(product.image)
        binding.editTextName.setText(product.name)
        binding.editTextDescription.setText(product.description)
        binding.editTextPrice.setText(product.price.toString())
        Glide.with(binding.root).load(Uri.parse(imageUrl)).error(R.drawable.app_logo)
            .into(binding.imageViewAddProdImage)
    }

    private fun setProfileImage() {
        pickMedia.launch("image/*")
    }


    private fun saveProduct() {
        val productName = binding.editTextName.text.toString()
        val productCategory = binding.spinnerCategory.selectedItem.toString()
        val productDescription = binding.editTextDescription.text.toString()
        val productPrice = binding.editTextPrice.text.toString().toDouble()
        val filePath = getFilePathFromUri(imageUri)
        val file = File(filePath!!)
        Log.d("AddProductFragment", "File path: $filePath")
        val newProduct = Product(
            name = productName,
            category = productCategory,
            description = productDescription,
            price = productPrice,
        )

        if (productId != null) {
            updateProd(newProduct)
        } else createNewProd(newProduct, file)
    }

    private fun updateProd(newProduct: Product) {
        lifecycleScope.launch {
            val imageFile = imageUri.toString()
            viewModel.updateProduct(productId!!, newProduct, imageFile)
            viewModel.updateProductState.collect { state ->
                when (state) {
                    is ScreenState.Loading -> {
                        binding.buttonSaveProduct.visibility = View.GONE
                    }

                    is ScreenState.Error -> {
                        binding.buttonSaveProduct.visibility = View.VISIBLE
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    }

                    is ScreenState.Success -> {
                        Toast.makeText(context, "Product saved successfully", Toast.LENGTH_SHORT)
                            .show()
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun createNewProd(newProduct: Product, imageFile: File) {
        lifecycleScope.launch {
            viewModel.addProduct(newProduct, imageFile)
            viewModel.addProductState.collect { state ->
                when (state) {
                    is ScreenState.Loading -> {
                        binding.buttonSaveProduct.visibility = View.GONE
                    }

                    is ScreenState.Error -> {
                        binding.buttonSaveProduct.visibility = View.VISIBLE
                        Toast.makeText(context, state.message ?: "Failed", Toast.LENGTH_SHORT)
                            .show()
                    }

                    is ScreenState.Success -> {
                        Toast.makeText(context, "Product added successfully", Toast.LENGTH_SHORT)
                            .show()
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun areAllFieldsFilled(): Boolean {
        val productName = binding.editTextName.text.toString()
        val productCategory = binding.spinnerCategory.selectedItem.toString()
        val productDescription = binding.editTextDescription.text.toString()
        val productPrice = binding.editTextPrice.text.toString()

        return productName.isNotEmpty() && productCategory.isNotEmpty() && productDescription.isNotEmpty() && productPrice.isNotEmpty() && imageUri.toString()
            .isNotEmpty()
    }


}