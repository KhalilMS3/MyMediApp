package com.example.mymediapp.ui.screens.login

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var loginViewModel: LoginViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        // Set the main dispatcher to the test dispatcher
        Dispatchers.setMain(testDispatcher)

        // Mock dependencies
        auth = mockk()
        sharedPreferences = mockk()
        editor = mockk()

        // Mock SharedPreferences behavior
        every { sharedPreferences.getString("email", "") } returns ""
        every { sharedPreferences.getString("password", "") } returns ""
        every { sharedPreferences.getBoolean("rememberMe", false) } returns false
        every { sharedPreferences.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor
        every { editor.putBoolean(any(), any()) } returns editor
        every { editor.remove(any()) } returns editor
        every { editor.apply() } just Runs

        // Initialize the ViewModel with mocks
        loginViewModel = LoginViewModel(auth, sharedPreferences)
    }

    @After
    fun tearDown() {
        // Reset the main dispatcher
        Dispatchers.resetMain()
    }
    @Test
    fun `signInUser with valid credentials calls onSuccess`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"

        loginViewModel.email.value = email
        loginViewModel.password.value = password

        val mockTask = mockk<Task<AuthResult>>(relaxed = true)

        every { auth.signInWithEmailAndPassword(email, password) } returns mockTask

        val slot = slot<com.google.android.gms.tasks.OnCompleteListener<AuthResult>>()

        every { mockTask.addOnCompleteListener(capture(slot)) } answers {
            every { mockTask.isSuccessful } returns true
            slot.captured.onComplete(mockTask)
            mockTask
        }

        var successCalled = false

        // Act
        loginViewModel.signInUser {
            successCalled = true
        }

        // Wait for coroutines to finish
        advanceUntilIdle()

        // Assert
        assertTrue(successCalled)
    }
    @Test
    fun `signInUser with invalid credentials sets errorMessage`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "wrongpassword"

        loginViewModel.email.value = email
        loginViewModel.password.value = password

        val mockTask = mockk<Task<AuthResult>>(relaxed = true)
        val exceptionMessage = "The password is invalid or the user does not have a password."

        every { auth.signInWithEmailAndPassword(email, password) } returns mockTask

        val slot = slot<com.google.android.gms.tasks.OnCompleteListener<AuthResult>>()

        every { mockTask.addOnCompleteListener(capture(slot)) } answers {
            every { mockTask.isSuccessful } returns false
            every { mockTask.exception } returns Exception(exceptionMessage)
            slot.captured.onComplete(mockTask)
            mockTask
        }

        // Act
        loginViewModel.signInUser { }

        // Wait for coroutines to finish
        advanceUntilIdle()

        // Assert
        assertEquals("Incorrect password. Please try again.", loginViewModel.errorMessage.value)
    }


}
