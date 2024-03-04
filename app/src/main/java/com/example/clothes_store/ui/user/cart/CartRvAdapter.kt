package com.example.clothes_store.ui.user.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clothes_store.R
import com.example.clothes_store.data.model.Cart
import com.example.clothes_store.databinding.CartItemBinding

class CartRvAdapter(
    private val cartList: List<Cart>,
    private val removeListener: (cartItem: Cart) -> Unit
) : RecyclerView.Adapter<CartRvAdapter.CartItemViewHolder>() {

    class CartItemViewHolder(
        removeListener: (cartItem: Cart) -> Unit,
        private val binding: CartItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private var cartItem: Cart? = null
        fun bind(curCartItem: Cart) {
            binding.textViewCartProductName.text = curCartItem.product.name
            val priceString = String.format("%.2f", curCartItem.product.price)
            val priceTag = "$priceString$"
            binding.textViewCartProductPrice.text = priceTag
            Glide.with(binding.root).load(curCartItem.product.image)
                .error(R.drawable.app_logo)
                .into(binding.imageViewCarProductImg)
            cartItem = curCartItem
        }


        init {
            binding.buttonDeleteFromCart.setOnClickListener { removeListener(cartItem!!) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        return CartItemViewHolder(
            removeListener,
            CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        holder.bind(cartList[position])
    }

}