package com.example.mymediapp.ui.screens.userProfile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.mymediapp.factory.UserProfileViewModelFactory
import com.example.mymediapp.repository.UserProfileRepository
import com.example.mymediapp.ui.screens.userProfile.UserProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(userId: String, navController: NavController) {
    //Initializing UserRepository and ViewModel with factory
    val userRepository = UserProfileRepository()
    val viewModel: UserProfileViewModel = viewModel(
        factory = UserProfileViewModelFactory(userRepository)
    )
    //State values from ViewModel
    val name by viewModel.name.collectAsState()
    val lastName by viewModel.lastName.collectAsState()
    val email by viewModel.email.collectAsState()
    val profileImageUrl by viewModel.profileImageUrl.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()

    //State to manage the dialog for errorMessages
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }


    val scope = rememberCoroutineScope()



    if (errorMessage.isNotEmpty()) {
        showErrorDialog = true
    }

    //Show success dialog when successMessage is not empty
    if (successMessage.isNotEmpty()) {
        showSuccessDialog = true
    }




    //Launcher for camera opening and getting a image
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            imageBitmap?.let {
                //Uploading image
                viewModel.uploadProfileImage(it)
            }
        }
    }

    //Function for camera opening
    fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        launcher.launch(intent)
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
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            if (profileImageUrl != null) {
                Image(
                    painter = rememberImagePainter(profileImageUrl),
                    contentDescription = "Profile Image",
                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                )
            } else {
                Icon(
                    imageVector = Icons.TwoTone.CameraAlt,
                    contentDescription = "Camera Icon",
                    modifier = Modifier.size(48.dp).clickable { openCamera() }
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = { openCamera() }) {
            Text("Take Profile Picture")
        }

        Text(text = "Profile", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(25.dp))

        //Editable TextFields for user data
        OutlinedTextField(
            value = name,
            onValueChange = { if (isEditing) viewModel.name.value = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            enabled = isEditing
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = lastName,
            onValueChange = { if (isEditing) viewModel.lastName.value = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth(),
            enabled = isEditing
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { if (isEditing) viewModel.email.value = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = isEditing
        )
        Spacer(modifier = Modifier.height(16.dp))

        //Password change fields
        var currentPassword by remember { mutableStateOf("") }
        var newPassword by remember { mutableStateOf("") }

        OutlinedTextField(
            value = currentPassword,
            onValueChange = { if (isEditing) currentPassword = it },
            label = { Text("Current Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = isEditing
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = newPassword,
            onValueChange = { if (isEditing) newPassword = it },
            label = { Text("New Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = isEditing
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row {
            //Save editing
            TextButton(
                modifier = Modifier.padding(end = 10.dp),
                onClick = {
                    if (isEditing) {
                        viewModel.updateProfileData()
                        if (currentPassword.isNotEmpty() && newPassword.isNotEmpty()) {
                            viewModel.changePassword(currentPassword, newPassword)
                        }
                    }
                    viewModel.toggleEditingMode()
                }
            ) {
                Text(if (isEditing) "Save Changes" else "Edit information")
            }
            //Delete Account button
            TextButton(
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                onClick = {
                    // Shows confirmation dialog
                    showDeleteDialog = true
                }
            ) {
                Text("Delete Account")
            }

            //Show Delete Confirmation Dialog
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showDeleteDialog = false
                    }, //Close dialog on outside touch
                    title = { Text("Delete Account") },
                    text = { Text("Are you sure you want to delete your account?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                scope.launch {
                                    viewModel.deleteAccount {
                                        navController.navigate("startscreen")
                                    }
                                }
                                showDeleteDialog = false
                            }
                        ) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showDeleteDialog = false }
                        ) {
                            Text("No")
                        }
                    }
                )
            }

            //Show error message dialog
            if (showErrorDialog) {
                AlertDialog(
                    onDismissRequest = { showErrorDialog = false },
                    title = { Text("Error") },
                    text = { Text(errorMessage) },
                    confirmButton = {
                        TextButton(onClick = { showErrorDialog = false }) {
                            Text("OK")
                        }
                    }
                )
            }

            //Show success message dialog
            if (showSuccessDialog) {
                AlertDialog(
                    onDismissRequest = { showSuccessDialog = false },
                    title = { Text("Success") },
                    text = { Text(successMessage) },
                    confirmButton = {
                        TextButton(onClick = { showSuccessDialog = false }) {

                        }
                    }
                )
            }
        }
    }
}
