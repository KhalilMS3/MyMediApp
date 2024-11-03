package com.example.mymediapp.ui.screens

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import coil.compose.rememberImagePainter
import android.Manifest
import android.widget.Toast
import androidx.camera.core.Camera
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import java.io.ByteArrayOutputStream

@Composable
fun UserProfileScreen(userId: String, navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance() // Initialize Firebase Storage

    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var profileImageUrl by remember { mutableStateOf<String?>(null) } // Changed to hold the image URL

    // Password fields
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    // Edit mode state
    var isEditing by remember { mutableStateOf(false) }

    // Camera launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            if (imageBitmap != null) {
                // Upload image to Firebase Storage
                uploadProfileImage(imageBitmap) { url ->
                    // Handle the image URL (store it in Firestore, for example)
                    if (url != null) {
                        // Save the image URL to Firestore
                        saveImageUrlToFirestore(auth.currentUser?.uid, url)
                        profileImageUrl = url // Update profile image URL
                    } else {
                        errorMessage = "Error uploading image"
                    }
                }
            }
        }
    }

    fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        launcher.launch(intent)
    }


    // Gets the profile information when the composable function is loaded
    LaunchedEffect(Unit) {
        fetchProfileData(auth, db, { fetchedName, fetchedLastName, fetchedEmail, fetchedImageUrl ->
            name = fetchedName
            lastName = fetchedLastName
            email = fetchedEmail
            profileImageUrl = fetchedImageUrl // Set fetched image URL
        }, { error ->
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
        // Profile Image or Camera Icon
        Box(
            modifier = Modifier
                .size(100.dp) // Size of the main circle
                .clip(CircleShape) // Clip to circle
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)), // Background color
            contentAlignment = Alignment.Center // Center content
        ) {
            if (profileImageUrl != null) {
                // Use Coil to display the profile image if available
                Image(
                    painter = rememberImagePainter(profileImageUrl),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape) // Clip image to circle
                )
            } else {
                // If no image, show camera icon
                Icon(
                    imageVector = Icons.Default.Camera, // Camera icon
                    contentDescription = "Camera Icon",
                    modifier = Modifier.size(48.dp) // Size of camera icon
                        .clickable { openCamera() } // Open camera on click
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { openCamera() }) {
            Text("Take Profile Picture")
        }

        Text(text = "Profile", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(25.dp))

        // Editable TextFields for user data
        TextField(
            value = name,
            onValueChange = { if (isEditing) name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            enabled = isEditing // Only editable in edit mode
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = lastName,
            onValueChange = { if (isEditing) lastName = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth(),
            enabled = isEditing // Only editable in edit mode
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = email,
            onValueChange = { if (isEditing) email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(autoCorrect = true),
            modifier = Modifier.fillMaxWidth(),
            enabled = isEditing // Only editable in edit mode
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Password change fields
        TextField(
            value = currentPassword,
            onValueChange = { if (isEditing) currentPassword = it },
            label = { Text("Current Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = isEditing // Only editable in edit mode
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = newPassword,
            onValueChange = { if (isEditing) newPassword = it },
            label = { Text("New Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = isEditing // Only editable in edit mode
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Single button to toggle edit mode and save changes
        Button(onClick = {
            if (isEditing) {
                updateProfileData(auth, db, name, lastName, email) { success, error ->
                    if (!success) {
                        errorMessage = error ?: "Error updating profile"
                    } else {
                        errorMessage = "Profile updated successfully!"
                    }
                }

                if (currentPassword.isNotEmpty() && newPassword.isNotEmpty()) {
                    changePassword(auth, currentPassword, newPassword) { success, error ->
                        if (!success) {
                            errorMessage = error ?: "Error changing password"
                        } else {
                            errorMessage = "Password changed successfully!"
                        }
                    }
                }
            }
            // Toggle editing mode
            isEditing = !isEditing
        }) {
            Text(if (isEditing) "Save" else "Edit")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Button to delete account
        Button(onClick = {
            deleteAccount(auth) { success, error ->
                if (!success) {
                    errorMessage = error ?: "Error deleting account"
                } else {
                    errorMessage = "Account deleted successfully!"
                    navController.navigate("login") // Navigate to login screen after deletion
                }
            }
        }) {
            Text("Delete Account")
        }

        // Error message if something goes wrong
        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}

// Function to upload profile image to Firebase Storage
private fun uploadProfileImage(image: Bitmap?, onComplete: (String?) -> Unit) {
    if (image != null) {
        // Convert Bitmap to ByteArray
        val byteArrayOutputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()

        // Create a reference to the Firebase Storage
        val storageReference = FirebaseStorage.getInstance().reference.child("profileImages/${System.currentTimeMillis()}.jpg")

        // Upload the image
        val uploadTask: UploadTask = storageReference.putBytes(data)

        uploadTask.addOnSuccessListener {
            // Get the download URL
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                onComplete(uri.toString()) // Return the URL of the uploaded image
            }
        }.addOnFailureListener { exception ->
            onComplete(null) // Return null if there's an error
        }
    } else {
        onComplete(null) // Return null if image is null
    }
}

// Function to save the image URL to Firestore
private fun saveImageUrlToFirestore(userId: String?, imageUrl: String) {
    val db = FirebaseFirestore.getInstance()
    if (userId != null) {
        db.collection("users").document(userId).update("profileImageUrl", imageUrl)
            .addOnSuccessListener { Log.d("Firestore", "Image URL successfully written!") }
            .addOnFailureListener { e -> Log.e("Firestore", "Error writing image URL", e) }
    }
}

// Function to fetch profile data from Firestore
private fun fetchProfileData(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    onSuccess: (String, String, String, String?) -> Unit,
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

                    // Get user data from document
                    val name = document.getString("name") ?: ""
                    val lastName = document.getString("lastName") ?: ""
                    val email = document.getString("email") ?: ""
                    val imageUrl = document.getString("profileImageUrl") // Fetch image URL

                    Log.d("fetchProfileData", "Fetched data - Name: $name, Last Name: $lastName, Email: $email, Image URL: $imageUrl")
                    onSuccess(name, lastName, email, imageUrl) // Pass the image URL
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

// Function to update user profile data in Firestore
private fun updateProfileData(auth: FirebaseAuth,
                              db: FirebaseFirestore,
                              name: String,
                              lastName: String, email: String, callback: (Boolean, String?) -> Unit) {
    val userId = auth.currentUser?.uid
    if (userId != null) {
        val userUpdates: MutableMap<String, Any> = hashMapOf(
            "name" to name,
            "lastName" to lastName,
            "email" to email
        )
        db.collection("users").document(userId)
            .update(userUpdates)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    } else {
        callback(false, "User not logged in")
    }
}

// Function to change user password
private fun changePassword(auth: FirebaseAuth, currentPassword: String, newPassword: String, callback: (Boolean, String?) -> Unit) {
    val user = auth.currentUser
    if (user != null) {
        val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
        user.reauthenticate(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful) {
                        callback(true, null)
                    } else {
                        callback(false, updateTask.exception?.message)
                    }
                }
            } else {
                callback(false, task.exception?.message)
            }
        }
    } else {
        callback(false, "User not logged in")
    }
}

// Function to delete user account
private fun deleteAccount(auth: FirebaseAuth, callback: (Boolean, String?) -> Unit) {
    val user = auth.currentUser
    if (user != null) {
        user.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true, null)
            } else {
                callback(false, task.exception?.message)
            }
        }
    } else {
        callback(false, "User not logged in")
    }
}
