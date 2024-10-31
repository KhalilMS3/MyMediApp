package com.example.mymediapp.navigation

import android.annotation.SuppressLint
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
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.compose.secondaryLight
import com.example.compose.tertiaryContainerLight
import com.example.mymediapp.model.Diet
import com.example.mymediapp.model.MyCalendar
import com.example.mymediapp.ui.screens.*
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavigation() {
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
                if (currentRoute(navController) !in listOf("startscreen", "login", "signup", "reminder")) {
                    TopBar(
                        onOpenDrawer = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        }
                    )
                }
            },
            bottomBar = {
                if (currentRoute(navController) !in listOf("startscreen", "login", "signup", "reminder")) {
                    BottomNavigationBar(navController)
                }
            },
            floatingActionButton = {
                if (currentRoute(navController) !in listOf("startscreen", "login", "signup", "reminder", "map")) {
                    FloatingActionButton(onClick = { navController.navigate("reminder") }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Reminder")
                    }
                }
            }
        ) { padding ->
            // Define NavHost to handle navigation between screens
            NavHost(navController = navController, startDestination = "startscreen", Modifier.padding(padding)) {
                composable("startscreen") { StartScreen(navController) }
                composable("login") { LoginScreen(navController) }
                composable("signup") { SignUpScreen(navController) }
                composable("home") { homeScreen(navController) }
                composable("reminder") { ReminderScreen(navController) }
                composable("medications") { myMedicationsScreen(navController) }
                composable("diet") { Diet().MealApp() }
                composable("calendar") { MyCalendar().CalendarView(mealItems = listOf()) }
                composable("settings") { settingScreen(navController) }
                composable("profile/{userId}") { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId") ?: ""
                    UserProfileScreen(userId, navController)
                }
                composable("map") { MapScreenContent() }
                composable("AboutUs") { AboutUsScreen(navController) }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        val currentRoute = navController.currentBackStackEntryAsState()?.value?.destination?.route
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == "home",
            onClick = { navController.navigate("home") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Star, contentDescription = "My Medications") },
            label = { Text("My Medications") },
            selected = currentRoute == "medications",
            onClick = { navController.navigate("medications") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Favorite, contentDescription = "My Diet") },
            label = { Text("My Diet") },
            selected = currentRoute == "diet",
            onClick = { navController.navigate("diet") }
        )
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
                navController.navigate("calendar")
                drawerState.close()
            }
        }
    )
    Spacer(modifier = Modifier.height(8.dp))
    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = Icons.Rounded.Place,
                contentDescription = "Find Pharmacy",
                modifier = Modifier.size(27.dp)
            )
        },
        label = {
            Text(
                text = "Nearby Pharmacies",
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(16.dp)
            )
        },
        selected = false,
        onClick = {
            scope.launch {
                navController.navigate("map")
                drawerState.close()
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
                navController.navigate("profile/{userId}")
                drawerState.close()
            }
        }
    )
    Spacer(modifier = Modifier.height(300.dp))
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
                navController.navigate("startscreen")
                drawerState.close()
            }
        }
    )

    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.Send,
                contentDescription = "About Us",
                modifier = Modifier.size(27.dp)
            )
        },
        label = {
            Text(
                text = "About Us",
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(16.dp)
            )
        },
        selected = false,
        onClick = {
            scope.launch {
                navController.navigate("AboutUs")
                drawerState.close()
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
        title = { Text("") }
    )
}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}
