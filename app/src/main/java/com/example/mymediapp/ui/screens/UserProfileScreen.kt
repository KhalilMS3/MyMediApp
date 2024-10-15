package com.example.mymediapp.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun UserProfileScreen(userId: String) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }


    // Gets the profile information when the composable function is loaded
    LaunchedEffect(Unit) {
        fetchProfileData(auth, db, { fetchedName, fetchedLastName, fetchedEmail ->

            // Updates variables with data from Firestore
            name = fetchedName
            lastName = fetchedLastName
            email = fetchedEmail
        }, { error ->
            //Error message if is error witch fetching data
            errorMessage = error
            Log.e("UserProfile", "Error fetching profile data: $error")
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "User Profile Icon",
            modifier = Modifier.height(94.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Profile", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(25.dp))

        // Showing user-data
        Text(text = "Name: $name")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Last Name: $lastName")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Email: $email")
        //Error message if is something wrong with displays the user-data
        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}
// Function to fetch data from Firestore based on user ID
fun fetchProfileData(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    onSuccess: (String, String, String) -> Unit,
    onFailure: (String) -> Unit
) {
    val userId = auth.currentUser?.uid
    Log.d("fetchProfileData", "User ID: $userId")

    if (userId != null) {
        Log.d("fetchProfileData", "Fetching data for user ID: $userId")
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    Log.d("fetchProfileData", "Document found: ${document.data}")

                    // Get users-data from document
                    val name = document.getString("name") ?: ""
                    val lastName = document.getString("lastName") ?: ""
                    val email = document.getString("email") ?: ""

                    Log.d("fetchProfileData", "Fetched data - Name: $name, Last Name: $lastName, Email: $email")
                    //Success with fetched data
                    onSuccess(name, lastName, email)
                } else {
                    Log.d("fetchProfileData", "No profile data found for user ID: $userId")
                    onFailure("No profile data found")
                }
            }
            .addOnFailureListener { e ->
                Log.e("fetchProfileData", "Error fetching data: ${e.message}", e)
                onFailure("Failed to fetch data: ${e.message}")
            }
    } else {
        Log.e("fetchProfileData", "User not logged in")
        onFailure("User not logged in")
    }
}
