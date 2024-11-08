package com.example.mymediapp.ui.screens.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(context: Context) : ViewModel() {
    //Firebase auth
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    //SharedPreferences to store user for login data
    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    //MutableStateFlows for hold email, password and the remember me state
    var email = MutableStateFlow(sharedPreferences.getString("email", "") ?: "")
    var password = MutableStateFlow(sharedPreferences.getString("password", "") ?: "")
    var rememberMe = MutableStateFlow(sharedPreferences.getBoolean("rememberMe", false))

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    // SingIn function to handle user sign-in
    fun signInUser(onSuccess: () -> Unit) {
        if (email.value.isNotEmpty() && password.value.isNotEmpty()) {

            //ViewModel scope
            viewModelScope.launch {

                //Doing Sign in with Firebase Authentication
                auth.signInWithEmailAndPassword(email.value, password.value)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            //Save login info if "Remember Me" is checked
                            with(sharedPreferences.edit()) {
                                if (rememberMe.value) {
                                    // Save email, password and remember state
                                    putString("email", email.value)
                                    putString("password", password.value)
                                    putBoolean("rememberMe", true)
                                } else {
                                    //Clear info if "Remember Me" is not active
                                    remove("email")
                                    remove("password")
                                    putBoolean("rememberMe", false)
                                }
                                apply()
                            }
                            onSuccess()
                        } else {
                            val exception = task.exception
                            _errorMessage.value = when (exception?.message) {
                                "There is no user record corresponding to this identifier. The user may have been deleted." ->
                                    "No account found with this email. Please check your email or sign up."
                                "The password is invalid or the user does not have a password." ->
                                    "Incorrect password. Please try again."
                                "A network error (such as timeout, interrupted connection or unreachable host) has occurred." ->
                                    "Network error. Please check your internet connection and try again."
                                else -> exception?.message ?: "Login failed. Please try again."
                            }
                            }
                    }
            }
        }
    }
}
