package com.example.bloggingapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var blogAdapter: BlogAdaptor
    private lateinit var databaseReference: DatabaseReference
    private val blogItems = mutableListOf<BloggingModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Activity started")

        enableEdgeToEdge()

        // Initialize data binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        Log.d(TAG, "Data binding initialized successfully")

        binding.floatingActionButton2.setOnClickListener {
            Log.d(TAG, "Floating action button clicked - navigating to AddArticle")
            startActivity(Intent(this, AddArticle::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize database reference
        databaseReference = FirebaseDatabase.getInstance("https://blogging-app-71899-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("Blogs")
        Log.d(TAG, "Database reference initialized")

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        Log.d(TAG, "Setting up RecyclerView")

        blogAdapter = BlogAdaptor(blogItems)
        Log.d(TAG, "Blog adapter created with ${blogItems.size} initial items")

        binding.blogRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = blogAdapter
        }
        Log.d(TAG, "RecyclerView configured with LinearLayoutManager and adapter")

        // Add database listener
        Log.d(TAG, "Adding database value event listener")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "Database snapshot received")
                Log.d(TAG, "Snapshot exists: ${snapshot.exists()}")
                Log.d(TAG, "Snapshot children count: ${snapshot.childrenCount}")

                // Clear existing items to avoid duplicates
                val previousSize = blogItems.size
                blogItems.clear()
                Log.d(TAG, "Cleared ${previousSize} existing blog items")

                var addedCount = 0
                for (childSnapshot in snapshot.children) {
                    Log.d(TAG, "Processing child snapshot with key: ${childSnapshot.key}")

                    val blog = childSnapshot.getValue(BloggingModel::class.java)
                    if (blog != null) {
                        blogItems.add(blog)
                        addedCount++
                        Log.d(TAG, "Added blog")
                    } else {
                        Log.w(TAG, "Failed to parse blog from snapshot with key: ${childSnapshot.key}")
                        Log.w(TAG, "Snapshot value: ${childSnapshot.value}")
                    }
                }

                Log.d(TAG, "Added $addedCount blogs to list. Total items: ${blogItems.size}")

                blogAdapter.notifyDataSetChanged()
                Log.d(TAG, "Adapter notified of data changes")

                if (blogItems.isEmpty()) {
                    Log.w(TAG, "No blogs found in database")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Database error occurred: ${error.message}", error.toException())
                Log.e(TAG, "Error code: ${error.code}")
                Log.e(TAG, "Error details: ${error.details}")

                Toast.makeText(this@MainActivity, "Failed to load blogs: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        Log.d(TAG, "Database listener attached successfully")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: Activity started")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Activity resumed")
        Log.d(TAG, "Current blog items count: ${blogItems.size}")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: Activity paused")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: Activity destroyed")
    }
}
