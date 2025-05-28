package com.example.bloggingapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bloggingapp.Model.BloggingModel
import com.example.bloggingapp.adaptor.BlogAdaptor
import com.example.bloggingapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var blogAdapter: BlogAdaptor
    private lateinit var databaseReference : DatabaseReference
    private val blogItems = mutableListOf<BloggingModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize data binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.floatingActionButton2.setOnClickListener {
            startActivity(Intent(this, AddArticle::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        blogAdapter = BlogAdaptor(blogItems)

        binding.blogRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = blogAdapter
        }

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snapshot in snapshot.children) {
                   val blog = snapshot.getValue(BloggingModel::class.java)
                    if (blog != null){
                        blogItems.add(blog)
                    }
                }
                blogAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Failed to Add Blogs", Toast.LENGTH_SHORT).show()
            }

        })
    }
}