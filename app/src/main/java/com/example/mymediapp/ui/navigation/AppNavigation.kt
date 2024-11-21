package com.example.mymediapp.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.twotone.AccountCircle
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.CalendarMonth
import androidx.compose.material.icons.twotone.Help
import androidx.compose.material.icons.twotone.Home
import androidx.compose.material.icons.twotone.LocationOn
import androidx.compose.material.icons.twotone.Logout
import androidx.compose.material.icons.twotone.Medication
import androidx.compose.material.icons.twotone.Restaurant
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.compose.secondaryContainerLight
import com.example.compose.secondaryLight
import com.example.compose.tertiaryContainerLight
import com.example.mymediapp.factory.DietViewModelFactory
import com.example.mymediapp.ui.screens.AboutUsScreen
import com.example.mymediapp.ui.screens.CalendarScreen
import com.example.mymediapp.ui.screens.MapScreenContent
import com.example.mymediapp.ui.screens.ReminderScreen
import com.example.mymediapp.ui.screens.StartScreen
import com.example.mymediapp.ui.screens.deit.DietScreen
import com.example.mymediapp.ui.screens.homeScreen
import com.example.mymediapp.ui.screens.login.LoginScreen
import com.example.mymediapp.ui.screens.myMedicationsScreen
import com.example.mymediapp.ui.screens.settings.SettingsScreen
import com.example.mymediapp.ui.screens.signup.SignUpScreen
import com.example.mymediapp.ui.screens.userProfile.UserProfileScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val dietViewModelFactory = DietViewModelFactory(db, auth)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(navController = navController, drawerState = drawerState)
            }
        },
        gesturesEnabled = true

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
                if (currentRoute(navController) !in listOf("startscreen", "login", "signup", "reminder", "map", "profile/{userId}", "AboutUs", "diet")) {
                    FloatingActionButton(
                        onClick = { navController.navigate("reminder") },
                        containerColor = secondaryContainerLight) {
                        Icon(Icons.TwoTone.Add, contentDescription = "Add Reminder", tint = Color.White

                            )
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
                composable("diet") { DietScreen(navController, factory = dietViewModelFactory) }
                composable("calendar") { CalendarScreen(navController, factory = dietViewModelFactory) }
                composable("settings") { SettingsScreen(navController) }
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
            icon = { Icon(Icons.TwoTone.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == "home",
            onClick = { navController.navigate("home") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.TwoTone.Medication, contentDescription = "My Medications") },
            label = { Text("My Medications") },
            selected = currentRoute == "medications",
            onClick = { navController.navigate("medications") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.TwoTone.Restaurant, contentDescription = "My Diet") },
            label = { Text("My Diet") },
            selected = currentRoute == "diet",
            onClick = { navController.navigate("diet") }
        )
    }
}

@Composable
fun DrawerContent(navController: NavHostController, modifier: Modifier = Modifier, drawerState: DrawerState) {
    val scope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance() //important for logout KHALI
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
                imageVector = Icons.TwoTone.CalendarMonth,
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
                imageVector = Icons.TwoTone.LocationOn,
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
                imageVector = Icons.TwoTone.AccountCircle,
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
    Spacer(modifier = Modifier.height(8.dp))
    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = Icons.TwoTone.Settings,
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
                navController.navigate("settings")
                drawerState.close()
            }
        }
    )
    Spacer(modifier = Modifier.height(300.dp))
    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = Icons.TwoTone.Logout,
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
                // Log out the user
                auth.signOut()
                navController.navigate("startscreen") {
                    popUpTo("startscreen") { inclusive = true }
                }
                drawerState.close()
            }
        }
    )

    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = Icons.TwoTone.Help,
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
