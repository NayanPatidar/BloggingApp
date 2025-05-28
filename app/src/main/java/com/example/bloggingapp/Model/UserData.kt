package com.example.bloggingapp.Model

data class UserData(
    val name: String,
    val password: String,
    val profileImageUrl: String
) {
    constructor() : this("", " ", "")
}
