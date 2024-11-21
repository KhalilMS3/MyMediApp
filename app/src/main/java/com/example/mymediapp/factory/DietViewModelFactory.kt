package com.example.mymediapp.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mymediapp.ui.screens.deit.DietViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DietViewModelFactory(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DietViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DietViewModel(db, auth) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
