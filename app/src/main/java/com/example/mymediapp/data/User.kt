/*package com.example.mymediapp.data

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class User : AppCompatActivity() {
    private lateinit var binding: ActivityUserDetailsBinding
    private val storage = FirebaseStorage.getInstance()
    private val db = FirebaseFirestore.getInstance() // Firestore-instans
    private val PICK_IMAGE_REQUEST = 71
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val auth = Firebase.auth
        val currentUser = auth.currentUser

        // Håndter bildeopplasting
        binding.uploadImageBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Lagre brukerinformasjon
        binding.saveUserDetailsBtn.setOnClickListener {
            val firstName = binding.firstName.text.toString().trim()
            val lastName = binding.lastName.text.toString().trim()

            if (firstName.isEmpty() || lastName.isEmpty()) {
                Toast.makeText(this, "Vennligst fyll inn både fornavn og etternavn", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Oppdater Firebase-profilen
            currentUser?.let { user ->
                val profileUpdates = userProfileChangeRequest {
                    displayName = "$firstName $lastName"
                }
                user.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Brukerprofil oppdatert!", Toast.LENGTH_SHORT).show()
                            saveUserDataToFirestore(user.uid, firstName, lastName) // Lagre data til Firestore
                        }
                    }

                // Hvis bilde er lastet opp, lagre det i Firebase Storage
                imageUri?.let {
                    val storageRef = storage.reference.child("profile_images/${user.uid}.jpg")
                    val uploadTask = storageRef.putFile(it)
                    uploadTask.addOnSuccessListener {
                        Toast.makeText(this, "Bilde lastet opp!", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(this, "Feil ved opplasting av bilde.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun saveUserDataToFirestore(userId: String, firstName: String, lastName: String) {
        val userData = hashMapOf(
            "firstName" to firstName,
            "lastName" to lastName
        )

        // Lagre data i Firestore
        db.collection("users").document(userId)
            .set(userData)
            .addOnSuccessListener {
                Toast.makeText(this, "Brukerdata lagret!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Feil ved lagring av brukerdata: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            binding.profileImage.setImageURI(imageUri)
        }
    }

}*/