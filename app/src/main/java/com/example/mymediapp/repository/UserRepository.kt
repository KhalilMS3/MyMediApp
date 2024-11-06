package com.example.mymediapp.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun createUser(email: String, password: String, name: String, lastName: String): Result<String> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val user = hashMapOf(
                    "name" to name,
                    "lastName" to lastName,
                    "email" to email
                )
                db.collection("users").document(userId).set(user).await()
                Result.success("User created successfully")
            } else {
                Result.failure(Exception("Failed to create user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
