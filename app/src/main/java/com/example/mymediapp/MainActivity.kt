package com.example.mymediapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.compose.secondaryLight
import com.example.compose.tertiaryContainerLight
import com.example.mymediapp.ui.screens.ReminderScreen
import com.example.mymediapp.ui.screens.homeScreen
import com.example.mymediapp.ui.screens.myMedicationsScreen
import com.example.mymediapp.ui.screens.settingScreen
import com.example.mymediapp.ui.theme.AppTheme
import com.example.mymediapp.model.Diet
import com.example.mymediapp.model.MyCalendar
import com.example.mymediapp.navigation.AppNavigation
import com.example.mymediapp.ui.screens.UserProfileScreen
import com.example.mymediapp.ui.screens.LoginScreen
import com.example.mymediapp.ui.screens.SignUpScreen
import com.example.mymediapp.ui.screens.StartScreen
import com.example.mymediapp.ui.screens.AboutUsScreen
import com.example.mymediapp.ui.screens.MapScreenContent
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_MyMediAPP)
        super.onCreate(savedInstanceState)
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
    AppNavigation()
}