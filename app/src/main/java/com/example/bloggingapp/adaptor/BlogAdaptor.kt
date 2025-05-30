package com.example.bloggingapp.adaptor

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bloggingapp.Model.BloggingModel
import com.example.bloggingapp.R
import com.example.bloggingapp.ReadMoreActivity
import com.example.bloggingapp.databinding.BlogItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BlogAdaptor(private val items: List<BloggingModel>) :
    RecyclerView.Adapter<BlogAdaptor.BlogViewHolder>() {

    private val databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance("https://blogging-app-71899-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference()
    private val currentUser = FirebaseAuth.getInstance().currentUser

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
        return BlogViewHolder(binding, databaseReference, currentUser, this)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        Log.d("BlogAdaptor", "Binding item at position $position: ${items[position].heading}")
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        Log.d("BlogAdaptor", "getItemCount: ${items.size}")
        return items.size
    }

    class BlogViewHolder(
        private val binding: BlogItemBinding,
        private val databaseReference: DatabaseReference,
        private val currentUser: FirebaseUser?,
        private val adapter: BlogAdaptor
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: BloggingModel) {
            val postId = model.postId
            Log.d("BlogViewHolder", "Binding: ${model.heading}")
            binding.heading.text = model.heading
            binding.username.text = model.username
            binding.date.text = model.date
            binding.post.text = model.post
            binding.likeCount.text = model.likeCount.toString()

            // Execute pending bindings immediately
            binding.executePendingBindings()

            binding.root.setOnClickListener {
                Log.d("BlogViewHolder", "Item clicked: ${model.heading}")
                val context = binding.root.context
                val intent = Intent(context, ReadMoreActivity::class.java)
                intent.putExtra("BlogItem", model)
                context.startActivity(intent)
            }

            val postLikeReference =
                databaseReference.child("blogs").child(postId.toString()).child("likes")
            val currentUserLiked = currentUser?.uid.let { uid ->
                postLikeReference.child(uid.toString())
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                binding.likebutton.setImageResource(R.drawable.like_2)
                            } else {
                                binding.likebutton.setImageResource(R.drawable.icon)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
            }

            binding.likebutton.setOnClickListener {
                if (currentUser != null) {
                    handleLikeButtonClick(postId, model)
                } else {
                    Toast.makeText(
                        this@BlogViewHolder.itemView.context,
                        "User not logged in",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        private fun handleLikeButtonClick(
            string: String?, model: BloggingModel
        ) {
            val userReference = databaseReference.child("users").child(currentUser!!.uid)
            val postLikeReference =
                databaseReference.child("Blogs").child(string.toString()).child("likes")

            postLikeReference.addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        userReference.child("likes").child(string.toString()).removeValue()
                            .addOnSuccessListener {
                                postLikeReference.child(currentUser.uid).removeValue()
                                model.likedBy?.remove(currentUser.uid)
                                updateLikeButtonImage(binding, false)

                                val newLikeCount = model.likeCount - 1;
                                model.likeCount = newLikeCount
                                databaseReference.child("Blogs").child(string.toString())
                                    .child("likeCount").setValue(newLikeCount)
                                adapter.notifyDataSetChanged()
                            }.addOnFailureListener { e ->
                                Log.e(
                                    "LikedClicked", "onDataChange: Failure to unlike the blog $e"
                                )
                            }

                    } else {
                        userReference.child("likes").child(string.toString()).setValue(true)
                            .addOnSuccessListener {
                                postLikeReference.child(currentUser.uid).setValue(true)
                                model.likedBy?.add(currentUser.uid)
                                updateLikeButtonImage(binding, true)

                                val newLikeCount = model.likeCount + 1;
                                model.likeCount = newLikeCount
                                databaseReference.child("Blogs").child(string.toString())
                                    .child("likeCount").setValue(newLikeCount)
                                adapter.notifyDataSetChanged()
                            }.addOnFailureListener { e ->
                                Log.e(
                                    "LikedClicked", "onDataChange: Failure to like the blog $e"
                                )
                            }
                    }
                }

                private fun updateLikeButtonImage(binding: BlogItemBinding, isLiked: Boolean) {
                    binding.likebutton.setImageResource(if (isLiked) R.drawable.icon else R.drawable.like_2)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }
}