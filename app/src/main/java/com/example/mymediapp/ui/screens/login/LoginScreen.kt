package com.example.mymediapp.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.mymediapp.factory.LoginViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    //State from ViewModel using the factory
    val loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(context)
    )
    //State from ViewModel using in the UI
    val email by loginViewModel.email.collectAsState()
    val password by loginViewModel.password.collectAsState()
    val rememberMe by loginViewModel.rememberMe.collectAsState()
    val errorMessage by loginViewModel.errorMessage.collectAsState()

    //Layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Logo
        Image(
            painter = painterResource(id = R.drawable.mymedi_full_green),
            contentDescription = "App Logo",
            modifier = Modifier.size(150.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))

        //Welcome text
        Text(
            text = "Welcome back",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 24.dp).align(Alignment.Start)
        )

        //Email input field
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "E-mail",
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )
            OutlinedTextField(
                value = email,
                onValueChange = { loginViewModel.email.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    focusedTextColor = Color.Black,
                    focusedIndicatorColor = primaryLight,
                    unfocusedIndicatorColor = Color.Gray
                )
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        //Password input field
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Password",
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )
            OutlinedTextField(
                value = password,
                onValueChange = { loginViewModel.password.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    focusedTextColor = Color.Black,
                    focusedIndicatorColor = primaryLight,
                    unfocusedIndicatorColor = Color.Black
                )
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        //Remember Me Checkbox
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = { loginViewModel.rememberMe.value = it },
                colors = CheckboxDefaults.colors(
                    uncheckedColor = Color.Gray
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Remember Me",
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        //Login button
        Button(
            onClick = {
                loginViewModel.signInUser {
                    navController.navigate("home")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = secondaryContainerLight,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Login",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        //Error message
        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
