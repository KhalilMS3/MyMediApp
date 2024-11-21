package com.example.mymediapp.ui.screens.signup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.mymediapp.repository.UserRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class SignUpViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var userRepository: UserRepository
    private lateinit var signUpViewModel: SignUpViewModel

    // Using TestDispatcher for controlling coroutine execution
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        // Set the main dispatcher to the test dispatcher
        Dispatchers.setMain(testDispatcher)

        // Mock the UserRepository
        userRepository = mockk()

        // Initialize the ViewModel with the mocked repository
        signUpViewModel = SignUpViewModel(userRepository)
    }

    @After
    fun tearDown() {
        // Reset the main dispatcher to the original dispatcher
        Dispatchers.resetMain()
        // Clear all mocks
        unmockkAll()
    }

    @Test
    fun `signUpUser with valid inputs calls onSuccess`() = runTest {
        // Arrange
        val name = "John"
        val lastName = "Doe"
        val email = "john.doe@example.com"
        val password = "password123"

        // Set ViewModel state
        signUpViewModel.name.value = name
        signUpViewModel.lastName.value = lastName
        signUpViewModel.email.value = email
        signUpViewModel.password.value = password

        // Mock repository response for successful sign-up
        coEvery { userRepository.createUser(email, password, name, lastName) } returns Result.success("User created successfully")

        var successCalled = false

        // Act
        signUpViewModel.signUpUser {
            successCalled = true
        }

        // Advance until all coroutines are executed
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertTrue(successCalled)
        assertEquals("", signUpViewModel.errorMessage.value)

        // Verify repository interaction
        coVerify(exactly = 1) { userRepository.createUser(email, password, name, lastName) }
    }

    @Test
    fun `signUpUser with missing fields sets errorMessage`() = runTest {
        // Arrange
        signUpViewModel.name.value = "" // Missing name
        signUpViewModel.lastName.value = "Doe"
        signUpViewModel.email.value = "john.doe@example.com"
        signUpViewModel.password.value = "password123"

        // Act
        signUpViewModel.signUpUser {
            // Should not be called
            fail("onSuccess should not be called when fields are missing")
        }

        // Advance until all coroutines are executed
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals("All fields are required", signUpViewModel.errorMessage.value)

        // Verify repository was not called
        coVerify(exactly = 0) { userRepository.createUser(any(), any(), any(), any()) }
    }

    @Test
    fun `signUpUser with short password sets errorMessage`() = runTest {
        // Arrange
        signUpViewModel.name.value = "John"
        signUpViewModel.lastName.value = "Doe"
        signUpViewModel.email.value = "john.doe@example.com"
        signUpViewModel.password.value = "123" // Short password

        // Act
        signUpViewModel.signUpUser {
            // Should not be called
            fail("onSuccess should not be called when password is too short")
        }

        // Advance until all coroutines are executed
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals("The Password must be at least 6 characters", signUpViewModel.errorMessage.value)

        // Verify repository was not called
        coVerify(exactly = 0) { userRepository.createUser(any(), any(), any(), any()) }
    }

    @Test
    fun `signUpUser when repository fails sets errorMessage`() = runTest {
        // Arrange
        val name = "John"
        val lastName = "Doe"
        val email = "john.doe@example.com"
        val password = "password123"
        val repositoryError = "Email already in use"

        // Set ViewModel state
        signUpViewModel.name.value = name
        signUpViewModel.lastName.value = lastName
        signUpViewModel.email.value = email
        signUpViewModel.password.value = password

        // Mock repository response for failed sign-up
        coEvery { userRepository.createUser(email, password, name, lastName) } returns Result.failure(Exception(repositoryError))

        var successCalled = false

        // Act
        signUpViewModel.signUpUser {
            successCalled = true
        }

        // Advance until all coroutines are executed
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertFalse(successCalled)
        assertEquals(repositoryError, signUpViewModel.errorMessage.value)

        // Verify repository interaction
        coVerify(exactly = 1) { userRepository.createUser(email, password, name, lastName) }
    }
}
