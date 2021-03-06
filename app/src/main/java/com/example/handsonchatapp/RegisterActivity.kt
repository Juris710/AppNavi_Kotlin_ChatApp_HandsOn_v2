package com.example.handsonchatapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.handsonchatapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private val TAG = "RegisterActivity"

    var selectPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.title = "登録"

        binding.registerButtonRegister.setOnClickListener {
            performClick()
        }
        binding.haveAccountTextRegister.setOnClickListener {
            Log.d(TAG, "try to show login activity")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        binding.selectPhotoButtonRegister.setOnClickListener {
            Log.d(TAG, "Try to show photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    private fun performClick() {
        val email = binding.emailEdittextRegister.text.toString()
        val password = binding.passwordEdittextRegister.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter text in email or password", Toast.LENGTH_SHORT)
                .show()
            return
        }

        Log.d(TAG, "Email is: $email")
        Log.d(TAG, "password is: $password")

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isCanceled) {
                    Log.d(TAG, "Canceled")
                }
                if (!it.isSuccessful) {
                    Log.d(TAG, "Failed to create user ${it.exception}")
                    return@addOnCompleteListener
                }

                Log.d(TAG, "Successfully created user with uid: ${it.result.user?.uid}")
                uploadImageToFirebaseStorage()
            }
            .addOnFailureListener {
                Log.d(TAG, "failed to create user message ${it.message}")
                Toast.makeText(this, "Failed to create user", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectPhotoUri == null) {
            Toast.makeText(this, "please select an Image", Toast.LENGTH_SHORT).show()
            return
        }

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("image/$filename")

        ref.putFile(selectPhotoUri!!)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully uploaded image:${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d(TAG, "File location :$it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {}
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, binding.usernameEdittextRegister.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "saved the user to Firebase Database")

                val intent = Intent(this, MessageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d(TAG, "Photo was selected")

            selectPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectPhotoUri)
            binding.circleViewRegister.setImageBitmap(bitmap)
            binding.selectPhotoButtonRegister.alpha = 0f
        }
    }
}