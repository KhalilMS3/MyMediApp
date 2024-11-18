package com.example.mymediapp.ui.screens.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.compose.primaryLight
import com.example.compose.secondaryContainerLight
import com.example.mymediapp.R
import com.example.mymediapp.factory.SignUpViewModelFactory
import com.example.mymediapp.repository.UserRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {
    val context = LocalContext.current
    //Initialize UserRepository and SignUpViewModel with factory
    val userRepository = UserRepository()
    val signUpViewModel: SignUpViewModel = viewModel(
        factory = SignUpViewModelFactory(userRepository)
    )
    //State values from the ViewModel
    val name by signUpViewModel.name.collectAsState()
    val lastName by signUpViewModel.lastName.collectAsState()
    val email by signUpViewModel.email.collectAsState()
    val password by signUpViewModel.password.collectAsState()
    val errorMessage by signUpViewModel.errorMessage.collectAsState()
    //Dialogs view
    var showDialog by remember { mutableStateOf(false) }



    //Layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Logo and title
        Image(
            painter = painterResource(id = R.drawable.mymedi_full_green),
            contentDescription = "App Logo",
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(100.dp))

        Text(
            text = "Create your account",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(16.dp))

        //Name fields
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "First name", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    //Value from ViewModel
                    value = name,
                    onValueChange = { signUpViewModel.name.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.White,
                        unfocusedTextColor = Color.DarkGray,
                        focusedTextColor = Color.Black,
                        focusedIndicatorColor = primaryLight,
                        unfocusedIndicatorColor = Color.Gray
                    )
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Last name", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    // Value from ViewModel
                    value = lastName,
                    onValueChange = { signUpViewModel.lastName.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.White,
                        unfocusedTextColor = Color.DarkGray,
                        focusedTextColor = Color.Black,
                        focusedIndicatorColor = primaryLight,
                        unfocusedIndicatorColor = Color.Gray
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        //Email field
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "E-mail", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = email,
                onValueChange = { signUpViewModel.email.value = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    unfocusedTextColor = Color.DarkGray,
                    focusedTextColor = Color.Black,
                    focusedIndicatorColor = primaryLight,
                    unfocusedIndicatorColor = Color.Gray
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        //Password field
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Password", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = password,
                onValueChange = { signUpViewModel.password.value = it },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    unfocusedTextColor = Color.DarkGray,
                    focusedTextColor = Color.Black,
                    focusedIndicatorColor = primaryLight,
                    unfocusedIndicatorColor = Color.Gray
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        //Sign up button
        Button(
            onClick = {
                signUpViewModel.signUpUser {
                    navController.navigate("home")
                }
                if (errorMessage.isNotEmpty()) {
                    showDialog = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = secondaryContainerLight,
                contentColor = Color.White
            ),
            enabled = name.isNotBlank() && lastName.isNotBlank() && email.isNotBlank() && password.isNotBlank()
        ) {
            Text(
                text = "Sign up",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        //Show dialog if there is any error message
        if (errorMessage.isNotEmpty() && showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "Error") },
                text = { Text(text = errorMessage) },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text(text = "OK")
                    }
                }
            )
        }


    }
}
