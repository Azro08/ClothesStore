package com.example.sport_store.ui.shared.home.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sport_store.R
import com.example.sport_store.data.model.Category
import com.example.sport_store.databinding.CategoryItemBinding

class CategoryRvAdapter(
    private val categoryList: List<Category>,
    private val listener: (category: Category) -> Unit
) : RecyclerView.Adapter<CategoryRvAdapter.CategoryViewHolder>() {

    private var lastClickedIndex: Int = -1

    class CategoryViewHolder(
        private var adapter: CategoryRvAdapter,
        listener: (category: Category) -> Unit,
        private val binding: CategoryItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        private var category: Category? = null
        fun bind(curCategory: Category, position: Int) {
            binding.textViewProductCategory.text = curCategory.category
            Log.d("CategoryName", curCategory.category)
            category = curCategory

            if (adapter.lastClickedIndex == position) {
                // Set background for the last clicked item
                binding.root.setBackgroundColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.dark_gray2
                    )
                )
                binding.textViewProductCategory.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.fluorescent_yellow
                    )
                )
            } else {
                // Set the default background for other items
                binding.root.setBackgroundColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.dark_gray
                    )
                )
                binding.textViewProductCategory.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.fluorescent_yellow
                    )
                )
            }
        }

        init {
            binding.root.setOnClickListener {
                listener(category!!)
                // Update the background for the last clicked item
                if (adapter.lastClickedIndex != -1) {
                    adapter.notifyItemChanged(adapter.lastClickedIndex)
                }

                // Update the background for the currently clicked item
                adapter.lastClickedIndex = adapterPosition
                adapter.notifyItemChanged(adapterPosition)

                // Notify the listener
                adapter.listener(category!!)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            this,
            listener,
            CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categoryList[position], position)
    }

}