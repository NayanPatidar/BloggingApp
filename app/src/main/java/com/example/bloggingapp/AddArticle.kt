package com.example.bloggingapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bloggingapp.Model.BloggingModel
import com.example.bloggingapp.Model.UserData
import com.example.bloggingapp.databinding.ActivityAddArticleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date

class AddArticle : AppCompatActivity() {
    companion object {
        private const val TAG = "AddArticle"
    }

    private val binding: ActivityAddArticleBinding by lazy {
        ActivityAddArticleBinding.inflate(layoutInflater)
    }

    private val dbRef: DatabaseReference =
        FirebaseDatabase.getInstance("https://blogging-app-71899-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("Blogs")
    private val userReference: DatabaseReference =
        FirebaseDatabase.getInstance("https://blogging-app-71899-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("users")
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Activity started")

        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.AddBlogButton.setOnClickListener {
            Log.d(TAG, "AddBlogButton clicked")

            val title = binding.BlogTitle.editText?.text.toString()
            val description = binding.BlogDescription.editText?.text.toString()

            Log.d(TAG, "Title: $title")
            Log.d(TAG, "Description length: ${description.length}")

            if (title.isEmpty() || description.isEmpty()) {
                Log.w(
                    TAG,
                    "Empty fields detected - Title empty: ${title.isEmpty()}, Description empty: ${description.isEmpty()}"
                )
                Toast.makeText(this, "PLEASE FILL ALL FIELDS", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user: FirebaseUser? = auth.currentUser

            if (user != null) {
                val userId = user.uid
                val username = user.displayName ?: "Anonymous"

                Log.d(TAG, "User authenticated - UserID: $userId, Username: $username")

                userReference.child(userId)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            Log.d(TAG, "User data snapshot received")

                            val userData = snapshot.getValue(UserData::class.java)
                            if (userData != null) {
                                val userNameFromDB = userData.name
                                val userImageUrlFromDB = userData.profileImageUrl

                                Log.d(
                                    TAG,
                                    "User data retrieved - Name: $userNameFromDB, Image URL: $userImageUrlFromDB"
                                )

                                val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Date())
                                Log.d(TAG, "Current date: $currentDate")

                                val blogItem = BloggingModel(
                                    title,
                                    userNameFromDB,
                                    currentDate,
                                    description,
                                    0,
                                    userImageUrlFromDB
                                )

                                Log.d(TAG, "BlogItem created successfully")

                                val key = dbRef.push().key
                                if (key != null) {
                                    Log.d(TAG, "Database key generated: $key")
                                    blogItem.postId = key
                                    dbRef.child(key).setValue(blogItem).addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            Log.d(TAG, "Blog successfully added to database")
                                            finish()
                                        } else {
                                            Log.e(
                                                TAG, "Failed to add blog to database", it.exception
                                            )
                                            Toast.makeText(
                                                this@AddArticle,
                                                "Failed to add blog",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                } else {
                                    Log.e(TAG, "Failed to generate database key")
                                }
                            } else {
                                Log.w(TAG, "User data is null in database")
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e(
                                TAG,
                                "Database error occurred: ${error.message}",
                                error.toException()
                            )
                            Toast.makeText(
                                this@AddArticle,
                                "Database error: ${error.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            } else {
                Log.w(TAG, "User is not authenticated")
                Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
