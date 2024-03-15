package com.example.sport_store.ui.shared.home.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sport_store.R
import com.example.sport_store.data.model.Product
import com.example.sport_store.databinding.ProductItemBinding
import com.example.sport_store.util.Constants
import kotlin.io.encoding.ExperimentalEncodingApi

class ProductsRvAdapter(
    private var productList: List<Product>,
    private val userRole: String,
    private val cartListener: (product: Product) -> Unit,
    private val itemListener: (product: Product) -> Unit,
    private val deleteItemListener: (product: Product) -> Unit
) : RecyclerView.Adapter<ProductsRvAdapter.ProductViewHolder>() {

    @OptIn(ExperimentalEncodingApi::class)
    class ProductViewHolder(
        cartListener: (product: Product) -> Unit,
        itemListener: (product: Product) -> Unit,
        deleteItemListener: (product: Product) -> Unit,
        private val userRole: String,
        private val binding: ProductItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private var product: Product? = null
        fun bind(curProduct: Product) {
            if (userRole == Constants.USER_ROLE) binding.buttonAddToCart.visibility = View.VISIBLE
            else if (userRole == Constants.ADMIN_ROLE) binding.buttonDeleteProd.visibility =
                View.VISIBLE
            Log.d("prodItem", curProduct.toString())
            binding.textViewOrderProductName.text = curProduct.name
            val priceString = String.format("%.2f", curProduct.price)
            val priceTag = "$priceString$"
            binding.textViewOrderProductPrice.text = priceTag
            val imageUrl = Constants.BASE_IMAGE_URL + curProduct.image
            Log.d("imageUrl", imageUrl)
            Glide.with(binding.root)
                .load(Uri.parse(imageUrl))
                .error(R.drawable.app_logo)
                .into(binding.imageViewOrderProduct)
            product = curProduct
        }


        init {
            binding.buttonAddToCart.setOnClickListener { cartListener(product!!) }
            binding.root.setOnClickListener { itemListener(product!!) }
            binding.buttonDeleteProd.setOnClickListener { deleteItemListener(product!!) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            cartListener,
            itemListener,
            deleteItemListener,
            userRole,
            ProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateProductList(newProductList: List<Product>) {
        productList = newProductList.toMutableList()
        notifyDataSetChanged()
    }


}