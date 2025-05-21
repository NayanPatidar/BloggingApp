package com.example.bloggingapp.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bloggingapp.MainActivity
import com.example.bloggingapp.R
import com.example.bloggingapp.databinding.ActivitySignInandRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignInAndRegisterActivity : AppCompatActivity() {

    private val binding:ActivitySignInandRegisterBinding by lazy {
        ActivitySignInandRegisterBinding.inflate(layoutInflater)
    }
    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance("https://blogging-app-71899-default-rtdb.asia-southeast1.firebasedatabase.app")
        val action = intent.getStringExtra("action")

        Log.d("SignInAndRegister", "Action Received: $action")

        if (action =="login" ){
            binding.loginButton.visibility = View.VISIBLE
            binding.loginPassword.visibility = View.VISIBLE
            binding.loginEmail.visibility = View.VISIBLE

            binding.registerButton.isClickable = false
            binding.registerPassword.visibility = View.GONE
            binding.registerEmailAddress.visibility = View.GONE
            binding.registerName.visibility = View.GONE
            binding.registerButton.visibility = View.VISIBLE

            binding.loginButton.setOnClickListener {
                val email = binding.loginEmail.text.toString()
                val password = binding.loginPassword.text.toString()
                if (email.isEmpty() || password.isEmpty()){
                    Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show()
                } else {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                Log.d("LOGIN", "Login Successful")
                                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, MainActivity::class.java ))
                                finish()
                            } else {
                                val error = task.exception?.message ?: "Unknown error"
                                Log.e("LOGIN", "Login failed: $error")
                                Toast.makeText(
                                    this@SignInAndRegisterActivity,
                                    "Registration failed: $error",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                }
            }

        } else if (action == "register"){
            binding.loginEmail.visibility = View.INVISIBLE
            binding.loginPassword.visibility = View.INVISIBLE
            binding.loginButton.visibility = View.INVISIBLE
            binding.isNewHere.visibility = View.INVISIBLE

            binding.registerButton.setOnClickListener {
                val email = binding.registerEmailAddress.text.toString()
                val password = binding.registerPassword.text.toString()
                val name = binding.registerName.text.toString()
                if (email.isEmpty() || password.isEmpty() ||  name.isEmpty()){
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                } else {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser;
                                auth.signOut()
                                Log.d("REGISTRATION", "SUCESSFULL $user")
                                FirebaseDatabase.getInstance().getReference("test_write").setValue("test")
                                    .addOnSuccessListener { Log.d("REGISTRATION", "Test write successful") }
                                    .addOnFailureListener { e -> Log.e("REGISTRATION", "Test write failed", e) }

                                user?.let {
                                    val userReference = database.getReference("users")
                                    val userId = user.uid
                                    val userData =
                                        com.example.bloggingapp.Model.UserData(name, email)
                                    Log.d("REGISTRATION", "DATA SAVED")

                                    userReference.child(userId).setValue(userData)
                                        .addOnSuccessListener {
                                            Log.d("REGISTRATION", "Data saved successfully")
                                            Toast.makeText(
                                                this@SignInAndRegisterActivity,
                                                "Registration complete!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("REGISTRATION", "Data save failed", e)
                                            Toast.makeText(
                                                this@SignInAndRegisterActivity,
                                                "Profile setup failed: ${e.message}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }

                                }
                                Toast.makeText(
                                    this,
                                    "User Successfully Registered",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(Intent(this, WelcomeActivity::class.java))
                                finish()

                            } else {
                                val error = task.exception?.message ?: "Unknown error"
                                Log.e("REGISTRATION", "Auth failed: $error")
                                Toast.makeText(
                                    this@SignInAndRegisterActivity,  // Fix context reference
                                    "Registration failed: $error",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                }
            }
        }
    }
}