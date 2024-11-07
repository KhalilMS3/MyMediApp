package com.example.mymediapp.ui.screens.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymediapp.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignUpViewModel(private val userRepository: UserRepository) : ViewModel() {
    //MutableStateFlow for hold user input for name, last name, email, and password
    val name = MutableStateFlow("")
    val lastName = MutableStateFlow("")
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    fun signUpUser(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (name.value.isBlank() || lastName.value.isBlank() || email.value.isBlank() || password.value.isBlank()) {
                _errorMessage.value = "All fields are required"
                return@launch
            }
            //Checking password length is less than 6 character
            if (password.value.length < 6) {
                _errorMessage.value = "The Password must be at least 6 characters"
                return@launch
            }
            //Try to create a user using the UserRepository
            val result = userRepository.createUser(email.value, password.value, name.value, lastName.value)
            if (result.isSuccess) {
                onSuccess()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Registration failed"
            }
        }
    }
}
