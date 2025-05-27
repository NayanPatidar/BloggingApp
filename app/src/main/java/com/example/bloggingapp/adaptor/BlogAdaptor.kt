package com.example.bloggingapp.adaptor

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bloggingapp.Model.BloggingModel
import com.example.bloggingapp.R
import com.example.bloggingapp.databinding.BlogItemBinding

class BlogAdaptor(private val items: List<BloggingModel>) :
    RecyclerView.Adapter<BlogAdaptor.BlogViewHolder>() {

    init {
        Log.d("BlogAdaptor", "Adapter created with ${items.size} items")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        Log.d("BlogAdaptor", "Creating ViewHolder")
        val binding: BlogItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.blog_item,
            parent,
            false
        )
        return BlogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        Log.d("BlogAdaptor", "Binding item at position $position: ${items[position].heading}")
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        Log.d("BlogAdaptor", "getItemCount: ${items.size}")
        return items.size
    }

    class BlogViewHolder(private val binding: BlogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: BloggingModel) {
            Log.d("BlogViewHolder", "Binding: ${model.heading}")
            binding.heading.text = model.heading
            binding.username.text = model.username
            binding.date.text = model.date
            binding.post.text = model.post
            binding.likeCount.text = model.likeCount.toString()

            // Execute pending bindings immediately
            binding.executePendingBindings()
        }
    }
}