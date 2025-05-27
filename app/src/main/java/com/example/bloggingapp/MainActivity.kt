package com.example.bloggingapp

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bloggingapp.Model.BloggingModel
import com.example.bloggingapp.adaptor.BlogAdaptor
import com.example.bloggingapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var blogAdapter: BlogAdaptor

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
        val blogList = createSampleData()
        blogAdapter = BlogAdaptor(blogList)

        binding.blogRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = blogAdapter
        }
    }

    private fun createSampleData(): List<BloggingModel> {
        return listOf(
            BloggingModel(
                heading = "Please Start Writing Better Git Commits",
                username = "New Blogger",
                date = "July 29, 2022",
                post = "I recently read a helpful article on Hashnode by Simon Egersand titled \"Write Git Commit Messages Your Colleagues Will Love,\" and it inspired me to dive a little deeper into understanding what makes a Git commit good or bad.",
                likeCount = 20
            ),
            BloggingModel(
                heading = "Understanding Android Architecture",
                username = "Tech Writer",
                date = "August 15, 2022",
                post = "Android architecture components help you structure your app in a way that is robust, testable, and maintainable with less boilerplate code.",
                likeCount = 45
            ),
            BloggingModel(
                heading = "Kotlin Tips and Tricks",
                username = "Kotlin Expert",
                date = "September 10, 2022",
                post = "Here are some advanced Kotlin features that can make your code more concise and readable.",
                likeCount = 32
            )
        )
    }
}