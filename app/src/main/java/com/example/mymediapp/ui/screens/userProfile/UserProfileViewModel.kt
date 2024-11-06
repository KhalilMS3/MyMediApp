package com.example.mymediapp.ui.screens.userProfile

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymediapp.repository.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class UserProfileViewModel(private val userRepository: UserProfileRepository) : ViewModel() {
    val name = MutableStateFlow("")
    val lastName = MutableStateFlow("")
    val email = MutableStateFlow("")
    val profileImageUrl = MutableStateFlow<String?>(null)
    val errorMessage = MutableStateFlow("")
    val successMessage = MutableStateFlow("")
    val isEditing = MutableStateFlow(false)

    init {
        fetchProfileData()
    }

    private fun fetchProfileData() {
        viewModelScope.launch {
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

    fun updateProfileData() {
        viewModelScope.launch {
            val result = userRepository.updateProfileData(name.value, lastName.value, email.value)
            if (result.isSuccess) {
                successMessage.value = "Profile updated successfully!"
            } else {
                errorMessage.value = result.exceptionOrNull()?.message ?: "Error updating profile"
            }
        }
    }

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

    fun uploadProfileImage(image: Bitmap) {
        viewModelScope.launch {
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
