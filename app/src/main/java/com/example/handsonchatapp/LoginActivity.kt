package com.example.handsonchatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.handsonchatapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.loginButtonLogin.setOnClickListener {
            val email = binding.emailEdittextLogin.text.toString();
            val password = binding.passwordEdittextLogin.text.toString();

            Log.d(TAG, "Email is: $email")
            Log.d(TAG, "password is: $password")
        }
        binding.backToRegisterTextLogin.setOnClickListener {
            Log.d(TAG, "try to show register activity")

            finish()
        }
    }
}