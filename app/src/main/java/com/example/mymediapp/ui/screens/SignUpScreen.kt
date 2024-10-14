package com.example.mymediapp.ui.screens

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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    //Firebase Authentication and Firestore for registering and storing user data
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Navn") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Etternavn") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-post") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Passord") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Validates the password has a minimum of 6 characters
        Button(onClick = {
            Log.d("SignUp", "Attempting to sign up with email: $email")
            if (password.length < 6) {
                errorMessage = "Passord må være minst 6 tegn"
                return@Button
            }

            // Registers user with email and password using the Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // If registration successful, we get the users ID
                        val userId = auth.currentUser?.uid
                        val user = hashMapOf(
                            "name" to name,
                            "lastName" to lastName,
                            "email" to email
                        )

                        // saving user-data in Firestore with users ID as a document ID
                        userId?.let {
                            db.collection("users").document(it)
                                .set(user)
                                .addOnSuccessListener {
                                    Log.d("SignUp", "User data added to Firestore")
                                    navController.navigate("login") // Naviger til login etter vellykket registrering
                                }
                                //Errors when saving users-data to Firestore
                                .addOnFailureListener { e ->
                                    errorMessage = "Failed to save user data: ${e.message}"
                                    Log.e("SignUp", "Firestore error: ${e.message}")
                                }
                        }
                    } else {
                        errorMessage = task.exception?.message ?: "Registrering mislyktes"
                        Log.e("SignUp", "Registrering feilet: ${task.exception?.message}")
                    }
                }
        }) {
            Text("Registrer deg")
        }
        //Error message if there is one
        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}
