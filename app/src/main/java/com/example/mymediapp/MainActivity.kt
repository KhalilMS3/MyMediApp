package com.example.mymediapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
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
import com.example.mymediapp.ui.screens.calendarScreen
import com.example.mymediapp.ui.screens.homeScreen
import com.example.mymediapp.ui.screens.loginScreen
import com.example.mymediapp.ui.screens.myDietScreen
import com.example.mymediapp.ui.screens.myMedicationsScreen
import com.example.mymediapp.ui.screens.settingScreen
import com.example.mymediapp.ui.screens.userProfileScreen
import com.example.mymediapp.ui.theme.AppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(navController = navController, drawerState = drawerState)
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    onOpenDrawer = {
                        scope.launch {
                            if (drawerState.isClosed) drawerState.open() else drawerState.close()
                        }
                    }
                )
            },
            floatingActionButton = {
                if (currentRoute(navController) != "reminder") {
                    FloatingActionButton(onClick = { navController.navigate("reminder") }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Reminder")
                    }
                }
            }
        ) { padding ->
            // Her definerer vi NavHost som håndterer navigasjonen mellom skjermene
            NavHost(navController = navController, startDestination = "home", Modifier.padding(padding)) {
                composable("home") { homeScreen(navController) }
                composable("reminder") { ReminderScreen(navController) }
                composable("medications") { myMedicationsScreen(navController) }
                composable("diet") { myDietScreen(navController) }
                composable("calendar") { calendarScreen(navController) }
                composable("settings") { settingScreen(navController) }
                composable("profile") { userProfileScreen(navController) }
                composable("logout") { loginScreen(navController) }

            }
        }
    }
}
@Composable
fun DrawerContent(navController: NavHostController, modifier: Modifier = Modifier, drawerState: DrawerState) {
    val scope = rememberCoroutineScope()
    Text(
        text = "My Medi",
        fontSize = 24.sp,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(16.dp)
    )
    HorizontalDivider()
    Spacer(modifier = Modifier.height(20.dp))
    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = Icons.Rounded.Home,
                contentDescription = "Home",
                modifier = Modifier.size(27.dp)
            )
        },
        label = {
            Text(
                text = "Home",
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(16.dp)
            )
        },
        selected = false,
        onClick = {

            scope.launch {
                navController.navigate("home")  // Navigate to "medications" screen
                drawerState.close()  // Close drawer after navigation
            }

        }
    )
    Spacer(modifier = Modifier.height(8.dp))
    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = Icons.Rounded.Star,
                contentDescription = "My medications",
                modifier = Modifier.size(27.dp)
            )
        },
        label = {
            Text(
                text = "My medications",
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(16.dp)
            )
        },
        selected = false,
        onClick = {

            scope.launch {
                navController.navigate("medications")  // Navigate to "medications" screen
                drawerState.close()  // Close drawer after navigation
            }

        }
    )

    Spacer(modifier = Modifier.height(8.dp))

    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = Icons.Rounded.Favorite,
                contentDescription = "My diet",
                modifier = Modifier.size(27.dp)

            )
        },
        label = {
            Text(
                text = "My diet",
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(16.dp)
            )
        },
        selected = false,
        onClick = {
            scope.launch {
                navController.navigate("diet")  // Navigate to "diet" screen
                drawerState.close()  // Close drawer after navigation
            }
        }
    )

    Spacer(modifier = Modifier.height(8.dp))

    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = Icons.Rounded.DateRange,
                contentDescription = "Calendar",
                modifier = Modifier.size(27.dp)

            )
        },
        label = {
            Text(
                text = "Calendar",
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(16.dp)
            )
        },
        selected = false,
        onClick = {
            scope.launch {
                navController.navigate("calendar")  // Navigate to "calendar" screen
                drawerState.close()  // Close drawer after navigation
            }
        }
    )
    Spacer(modifier = Modifier.height(8.dp))

    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = Icons.Rounded.Settings,
                contentDescription = "Settings",
                modifier = Modifier.size(27.dp)

            )
        },
        label = {
            Text(
                text = "Settings",
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(16.dp)
            )
        },
        selected = false,
        onClick = {
            scope.launch {
                navController.navigate("settings")  // Navigate to "settings" screen
                drawerState.close()  // Close drawer after navigation
            }
        }
    )

    Spacer(modifier = Modifier.height(8.dp))

    NavigationDrawerItem(

        icon = {
            Icon(
                imageVector = Icons.Rounded.AccountCircle,
                contentDescription = "My Profile",
                modifier = Modifier.size(27.dp)

            )
        },
        label = {
            Text(
                text = "My profile",
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(16.dp)
            )
        },
        selected = false,
        onClick = {
            scope.launch {
                navController.navigate("profile")  // Navigate to "profile" screen
                drawerState.close()  // Close drawer after navigation
            }
        }
    )
    Spacer(modifier = Modifier.height(208.dp))

    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ExitToApp,
                contentDescription = "Log out",
                modifier = Modifier.size(27.dp)

            )
        },
        label = {
            Text(
                text = "Log out",
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(16.dp)
            )
        },
        selected = false,
        onClick = {
            scope.launch {
                //TODO: implement logic to log out
                navController.navigate("logout")  // Navigate to "login" screen
                drawerState.close()  // Close drawer after navigation
            }
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(onOpenDrawer: () -> Unit) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = tertiaryContainerLight,
            navigationIconContentColor = secondaryLight
        ),
        navigationIcon = {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                modifier = Modifier
                    .scale(1.4f)
                    .padding(start = 16.dp, end = 16.dp)
                    .clickable {
                    onOpenDrawer()
                }
            )
        },
        title = { Text("")
        }
    )
}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}


