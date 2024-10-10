/*package com.example.mymediapp.data

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class SingupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySingupBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingupBinding.inflate(layoutInflater)
        auth = Firebase.auth


        setContentView(binding.root)
        auth = Firebase.auth
        binding.continueBtn.setOnClickListener{auth.createUserWithEmailAndPassword(binding.email.getText().toString().trim(),
            binding.password.getText().toString().trim())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    val intent = Intent(this, User::class.java)
                    intent.putExtra("login_message", "You have successfully signed up and logged in!")

                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }}
        binding.move.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)


        }
    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this,User::class.java)
            startActivity(intent)

        }
    }

}*/