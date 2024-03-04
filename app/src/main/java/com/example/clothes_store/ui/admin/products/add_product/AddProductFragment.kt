package com.example.clothes_store.ui.admin.products.add_product

import android.content.Context
import android.net.Uri
import android.os.Bundle
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
                Glide.with(binding.root).load(uri)
                    .error(R.drawable.add_img_icon)
                    .into(binding.imageViewAddProdImage)
                imageUri = uri
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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
                context,
                "Please fill all fields",
                Toast.LENGTH_SHORT
            ).show()
            else saveProduct()
        }
        binding.imageViewAddProdImage.setOnClickListener {
            setProfileImage()
        }
    }

    private fun getProductsCategories() {
        Log.d("AddProductFragment", "getProductsCategories")
        lifecycleScope.launch {
            viewModel.categoryList.collect { state ->
                when (state) {
                    is ScreenState.Loading -> {}
                    is ScreenState.Error -> {
                        Log.d("AddProductFragment", "error: ${state.message}")
                    }

                    is ScreenState.Success -> {
                        Log.d("AddProductFragment", "success: ${state.data}")
                        displayCategories(state.data!!.toCategoryStringList())
                    }
                }
            }
        }
    }

    private fun displayCategories(categoryList: List<String>) {
        Log.d("AddProductFragment", "displayCategories: $categoryList")
        val categoryAdapter = ArrayAdapter(
            requireContext(),
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            categoryList
        )
        binding.spinnerCategory.adapter = categoryAdapter
    }

    private fun getDetails(id: Int) {
        lifecycleScope.launch {
            lifecycleScope.launch {
                viewModel.getProductDetails(id)
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
                            }
                        }

                    }
                }
            }
        }
    }

    private fun displayDetails(product: Product) {
        imageUri = Uri.parse(product.image)
        binding.editTextName.setText(product.name)
        binding.editTextDescription.setText(product.description)
        binding.editTextPrice.setText(product.price.toString())
        Glide.with(binding.root).load(imageUri).error(R.drawable.app_logo)
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
        val imageFile = imageUri.toString()
        Log.d("imageFIleUri", imageUri.toString())
        val newProduct = Product(
            name = productName,
            category = productCategory,
            description = productDescription,
            price = productPrice,
            image = imageFile
        )

        if (productId != null) {
            updateProd(newProduct)
        } else createNewProd(newProduct, imageFile)
    }

    private fun updateProd(newProduct: Product) {
        Log.d("AddProductFragment", "updateProd: ")
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
                        Log.d("AddProductFragment", state.message.toString())
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

    private fun createNewProd(newProduct: Product, imageFile: String) {
        Log.d("AddProductFragment", "createNewProd: ")
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
                        Log.d("AddProductFragment", state.message.toString())
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

//    private fun getRealPathFromURI(uri: Uri): String {
//        val cursor: Cursor? = requireActivity().contentResolver.query(uri, null, null, null, null)
//        return if (cursor == null) { // Source is Dropbox or other similar local file path
//            uri.path.toString()
//        } else {
//            cursor.moveToFirst()
//            val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
//            val path: String = cursor.getString(idx)
//            cursor.close()
//            path
//        }
//    }


    private fun areAllFieldsFilled(): Boolean {
        val productName = binding.editTextName.text.toString()
        val productCategory = binding.spinnerCategory.selectedItem.toString()
        val productDescription = binding.editTextDescription.text.toString()
        val productPrice = binding.editTextPrice.text.toString()

        return productName.isNotEmpty() && productCategory.isNotEmpty() &&
                productDescription.isNotEmpty() && productPrice.isNotEmpty() && imageUri.toString()
            .isNotEmpty()
    }


}