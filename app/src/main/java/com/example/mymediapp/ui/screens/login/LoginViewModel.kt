package com.example.mymediapp.ui.screens.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(context: Context) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    var email = MutableStateFlow(sharedPreferences.getString("email", "") ?: "")
    var password = MutableStateFlow(sharedPreferences.getString("password", "") ?: "")
    var rememberMe = MutableStateFlow(sharedPreferences.getBoolean("rememberMe", false))

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    fun signInUser(onSuccess: () -> Unit) {
        if (email.value.isNotEmpty() && password.value.isNotEmpty()) {
            viewModelScope.launch {
                auth.signInWithEmailAndPassword(email.value, password.value)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Save login info if "Remember Me" is checked
                            with(sharedPreferences.edit()) {
                                if (rememberMe.value) {
                                    putString("email", email.value)
                                    putString("password", password.value)
                                    putBoolean("rememberMe", true)
                                } else {
                                    remove("email")
                                    remove("password")
                                    putBoolean("rememberMe", false)
                                }
                                apply()
                            }
                            onSuccess()
                        } else {
                            _errorMessage.value = task.exception?.message ?: "Login failed"
                        }
                    }
            }
        } else {
            _errorMessage.value = "Email and Password cannot be empty"
        }
    }
}
