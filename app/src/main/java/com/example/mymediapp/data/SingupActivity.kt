/*package com.example.mymediapp.data

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
    //variables for first name, last name, email, password and error message.
    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    //Firestore instance
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
            label = { Text("Name") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") }
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
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))

        //Button for sending user data to Firestore
        Button(onClick = {
            if (name.isNotBlank() && lastName.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                // Create user with email and password using Firebase Authentication
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // User created successfully, now save additional user data in Firestore
                            val userId = auth.currentUser?.uid
                            val user = hashMapOf(
                                "name" to name,
                                "lastName" to lastName,
                                "email" to email
                            )

                            // Save user data in the collection "users" in Firestore
                            userId?.let {
                                db.collection("users").document(it)
                                    .set(user)
                                    .addOnSuccessListener {
                                        // Navigate to home screen if registration is successful
                                        navController.navigate("home")
                                    }
                                    .addOnFailureListener { e ->
                                        errorMessage = "Failed to save user data: ${e.message}"
                                    }
                            }
                        } else {
                            errorMessage = "Failed to register: ${task.exception?.message}"
                        }
                    }
            } else {
                errorMessage = "All fields are required"
            }
        }) {
            Text("Sign Up")
        }


        //Error message if one is set already
        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(16.dp))

        //Button for login screen
        Button(onClick = { navController.navigate("login") }) {
            Text("Already have an account? Login")
        }
    }
}
*/