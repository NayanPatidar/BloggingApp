package com.example.bloggingapp.Model;

data class BloggingModel(
    val heading: String = "null",
    val username: String = "null",
    val date: String = "null",
    val post: String = "null",
    val likeCount: Int = 0,
    val imageUrl: String = "null"
)
