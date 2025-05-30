package com.example.bloggingapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.bloggingapp.Model.BloggingModel
import com.example.bloggingapp.databinding.ActivityReadMoreBinding

class ReadMoreActivity : AppCompatActivity() {

    private lateinit var binding : ActivityReadMoreBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_read_more)

        binding.backButton.setOnClickListener {
            finish()
        }

        val blogs = intent.getParcelableExtra<BloggingModel>("BlogItem")

        if (blogs != null){
            binding.titleText.text = blogs.heading
            binding.username.text = blogs.username
            binding.date.text = blogs.date
            binding.blogDescriptionTextView.text = blogs.post
        } else {
            Toast.makeText(this, "Failed To Load Blogs", Toast.LENGTH_SHORT).show()
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}