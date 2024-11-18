package com.example.mymediapp.ui.screens.userProfile

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymediapp.repository.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow

class UserProfileViewModel(private val userRepository: UserProfileRepository) : ViewModel() {
    // MutableStateFlows for hold user data and other UI
    val name = MutableStateFlow("")
    val lastName = MutableStateFlow("")
    val email = MutableStateFlow("")
    val profileImageUrl = MutableStateFlow<String?>(null)
    val errorMessage = MutableStateFlow("")
    val successMessage = MutableStateFlow("")
    val isEditing = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow("")
    //val errorMessage: StateFlow<String> = _errorMessage

    fun setErrorMessage(message: String) {
        _errorMessage.value = message
    }


    init {
        //Fetching profile data from repository
        fetchProfileData()
    }
    // Fetching user-profile-data from the repository and updates the stateflows
    private fun fetchProfileData() {
        viewModelScope.launch {
            // Fetch profile data from repository
            val result = userRepository.fetchProfileData()
            if (result.isSuccess) {
                val data = result.getOrNull() ?: return@launch
                name.value = data["name"] as? String ?: ""
                lastName.value = data["lastName"] as? String ?: ""
                email.value = data["email"] as? String ?: ""
                profileImageUrl.value = data["profileImageUrl"] as? String
            } else {
                errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to fetch profile data"
            }
        }
    }
    //Update profile data function to update
    fun updateProfileData() {
        viewModelScope.launch {
            // Call repository to update the profile
            val result = userRepository.updateProfileData(name.value, lastName.value, email.value)
            if (result.isSuccess) {
                successMessage.value = "Profile updated successfully!"
            } else {
                errorMessage.value = result.exceptionOrNull()?.message ?: "Error updating profile"
            }
        }
    }
    //Function to change the password
    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            val result = userRepository.changePassword(currentPassword, newPassword)
            if (result.isSuccess) {
                successMessage.value = "Password changed successfully!"
            } else {
                errorMessage.value = result.exceptionOrNull()?.message ?: "Error changing password"
            }
        }
    }
    //Function to delete the account
    fun deleteAccount(onAccountDeleted: () -> Unit) {
        viewModelScope.launch {
            val result = userRepository.deleteAccount()
            if (result.isSuccess) {
                successMessage.value = "Account deleted successfully!"
                onAccountDeleted()
            } else {
                errorMessage.value = result.exceptionOrNull()?.message ?: "Error deleting account"
            }
        }
    }
    //Function to upload a new profile image
    fun uploadProfileImage(image: Bitmap) {
        viewModelScope.launch {
            // Get the current users-ID
            val userId = userRepository.auth.currentUser?.uid ?: return@launch
            val result = userRepository.uploadProfileImage(image)
            if (result.isSuccess) {
                val imageUrl = result.getOrNull() ?: return@launch
                userRepository.saveImageUrlToFirestore(userId, imageUrl)
                profileImageUrl.value = imageUrl
            } else {
                errorMessage.value = result.exceptionOrNull()?.message ?: "Error uploading image"
            }
        }
    }

    fun toggleEditingMode() {
        isEditing.value = !isEditing.value
    }
}
