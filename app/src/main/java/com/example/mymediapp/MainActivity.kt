package com.example.mymediapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mymediapp.ui.screens.LoginScreen
import com.example.mymediapp.ui.screens.SignUpScreen
import com.example.mymediapp.ui.screens.StartScreen
import com.example.mymediapp.ui.screens.UserProfileScreen
import com.example.mymediapp.ui.theme.AppTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Initializes the Firebase
        FirebaseApp.initializeApp(this)


        setContent {
            AppTheme {
                MyApp()
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MyApp() {
    //NavHostController to mange navigation between screens in the app
    val navController = rememberNavController()
    Scaffold(
        floatingActionButton = {
            if (currentRoute(navController) != "reminder") {
                FloatingActionButton(onClick = { navController.navigate("reminder") }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Reminder")
                }
            }
        }
    ) {

        NavHost(navController = navController, startDestination = "start") {
            composable("start") { StartScreen(navController) }
            composable("login") { LoginScreen(navController) }
            composable("signup") { SignUpScreen(navController) }
            composable("home") { HomeScreen(navController) }
            composable("reminder") { ReminderScreen(navController) }
            composable("Profile") { UserProfileScreen() }
        }
    }
}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}
