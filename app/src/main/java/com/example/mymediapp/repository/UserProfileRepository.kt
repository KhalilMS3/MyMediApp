package com.example.mymediapp.repository

import android.graphics.Bitmap
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

class UserProfileRepository {
    val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    suspend fun fetchProfileData(): Result<Map<String, Any>> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
            val documentSnapshot = db.collection("users").document(userId).get().await()
            if (documentSnapshot.exists()) {
                Result.success(documentSnapshot.data ?: emptyMap())
            } else {
                Result.failure(Exception("No profile data found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfileData(name: String, lastName: String, email: String): Result<Boolean> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
            val userUpdates: MutableMap<String, Any> = hashMapOf(
                "name" to name,
                "lastName" to lastName,
                "email" to email
            )
            db.collection("users").document(userId).update(userUpdates).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Boolean> {
        return try {
            val user = auth.currentUser ?: throw Exception("User not logged in")
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
            user.reauthenticate(credential).await()
            user.updatePassword(newPassword).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAccount(): Result<Boolean> {
        return try {
            val user = auth.currentUser ?: throw Exception("User not logged in")
            user.delete().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadProfileImage(image: Bitmap): Result<String> {
        return try {
            val byteArrayOutputStream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val data = byteArrayOutputStream.toByteArray()

            val storageReference = storage.reference.child("profileImages/${System.currentTimeMillis()}.jpg")
            storageReference.putBytes(data).await()

            val downloadUrl = storageReference.downloadUrl.await()
            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveImageUrlToFirestore(userId: String, imageUrl: String): Result<Boolean> {
        return try {
            db.collection("users").document(userId).update("profileImageUrl", imageUrl).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
