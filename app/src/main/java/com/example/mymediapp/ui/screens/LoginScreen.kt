package com.example.mymediapp.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material3.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

import com.example.compose.primaryLight
import com.example.compose.secondaryContainerLight
import com.example.mymediapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {


    val context = LocalContext.current







    // Setup EncryptedSharedPreferences hentet fra stack overflow just for å huske
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)





    var email by rememberSaveable { mutableStateOf(sharedPreferences.getString("email", "") ?: "") }
    var password by rememberSaveable { mutableStateOf(sharedPreferences.getString("password", "") ?: "") }
    var errorMessage by rememberSaveable { mutableStateOf("") }
    var rememberMe by rememberSaveable { mutableStateOf(sharedPreferences.getBoolean("rememberMe", false)) }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.mymedi_full_green),
            contentDescription = "App Logo",
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Welcome text
        Text(
            text = "Welcome back",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 24.dp).align(Alignment.Start)

        )

        // Email input field
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "E-mail",
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    focusedTextColor = Color.Black,
                    focusedIndicatorColor = primaryLight,
                    unfocusedIndicatorColor = Color.Gray
                )
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Password input field
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Password",
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    focusedTextColor = Color.Black,
                    focusedIndicatorColor = primaryLight,
                    unfocusedIndicatorColor = Color.Black
                )
            )
        }
        Spacer(modifier = Modifier.height(10.dp))


        // Remember Me Checkbox
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically

        ) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = { rememberMe = it },
                colors = CheckboxDefaults.colors(
                    //checkedColor = secondaryContainerLight,
                    uncheckedColor = Color.Gray
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Remember Me",
            )
        }

        Spacer(modifier = Modifier.height(32.dp)
        )


        // Login button
        Button(
            onClick = {
                // Firebase authentication logic
                Log.d("Login", "Attempting to log in with email: $email")

                val auth = FirebaseAuth.getInstance()
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("Login", "Login successful")

                            // Save login info user clicked on  "Remember Me"  box (is checked)
                            with(sharedPreferences.edit()) {
                                if (rememberMe) {
                                    putString("email", email)
                                    putString("password", password)
                                    putBoolean("rememberMe", true)
                                } else {
                                    // Clear email if "Remember Me" is unchecked
                                    remove("email")
                                    remove("password")
                                    putBoolean("rememberMe", false)
                                }
                                apply()
                            }

                            navController.navigate("home")
                        } else {
                            errorMessage = task.exception?.message ?: "Login failed"
                            Log.e("Login", "Login failed: ${task.exception?.message}")
                        }
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = secondaryContainerLight,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Login",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Error message
        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
