package com.example.bloggingapp

import android.os.Bundle
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
    private val binding: ActivityAddArticleBinding by lazy {
        ActivityAddArticleBinding.inflate(layoutInflater)
    }

    private val dbRef: DatabaseReference =
        FirebaseDatabase.getInstance("https://blogging-app-71899-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("Blogs")
    private val userReference: DatabaseReference =
        FirebaseDatabase.getInstance("https://blogging-app-71899-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("Users")
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.AddBlogButton.setOnClickListener {
            val title = binding.BlogTitle.editText?.text.toString()
            val description = binding.BlogDescription.editText?.text.toString()

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "PLEASE FILL ALL FIELDS", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user: FirebaseUser? = auth.currentUser
            if (user != null) {
                val userId = user.uid
                val username = user.displayName ?: "Anonymous"

                userReference.child(userId)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val userData = snapshot.getValue(UserData::class.java)
                            if (userData != null) {
                                val userNameFromDB = userData.name
                                val userImageUrlFromDB = userData.profileImageUrl

                                val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Date())

                                val blogItem = BloggingModel(
                                    title,
                                    userNameFromDB,
                                    currentDate,
                                    description,
                                    0,
                                    userImageUrlFromDB
                                )

                                val key = dbRef.push().key
                                if (key != null) {
                                    dbRef.child(key).setValue(blogItem).addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            finish()
                                        } else {
                                            Toast.makeText(
                                                this@AddArticle,
                                                "Failed to add blog",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(
                                this@AddArticle,
                                "Database error: ${error.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                    });

            }
        }
    }
}