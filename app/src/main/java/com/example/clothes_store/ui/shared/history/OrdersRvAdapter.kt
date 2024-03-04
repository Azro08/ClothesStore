package com.example.clothes_store.ui.shared.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clothes_store.data.model.Order
import com.example.clothes_store.databinding.OrderItemBinding

class OrdersRvAdapter(private val ordersList: List<Order>) :
    RecyclerView.Adapter<OrdersRvAdapter.OrderViewHolder>() {

    class OrderViewHolder(private val binding: OrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var order: Order? = null
        fun bind(curOrder: Order) {
            val priceString = String.format("%.2f", curOrder.totalPrice)
            val priceTag = "$priceString$"
            binding.textViwOrderItemDetails.text = curOrder.productNames
            binding.textViwOrderItemPrice.text = priceTag
            binding.textViwOrderItemDate.text = curOrder.date
            order = curOrder
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        return OrderViewHolder(
            OrderItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return ordersList.size
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(ordersList[position])
    }
}