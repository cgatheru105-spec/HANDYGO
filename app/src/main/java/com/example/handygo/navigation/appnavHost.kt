package com.example.handygo.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.handygo.ProfileViewModel
import com.example.handygo.providerscreens.*
import com.example.handygo.userscreens.*

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUTE_SPLASH,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val profileViewModel: ProfileViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(ROUTE_SPLASH) { Splashscreen(navController, profileViewModel) }
        composable(ROUTE_START) { StartScreen(navController) }
        composable(ROUTE_LOGIN) { LoginScreen(navController) }
<<<<<<< HEAD
        composable(ROUTE_REGISTER_USER) { RegisterUserScreen(navController, profileViewModel) }
        composable(ROUTE_REGISTER_PROVIDER) { RegisterProviderScreen(navController, profileViewModel) }
        composable(ROUTE_USER_HOME) { UserHomeScreen(navController, profileViewModel) }
=======
        composable(ROUTE_REGISTER_USER) { RegisterUserScreen(navController) }
        composable(ROUTE_REGISTER_PROVIDER) { RegisterProviderScreen(navController, profileViewModel) }
        composable(ROUTE_USER_HOME) { UserHomeScreen(navController) }
>>>>>>> 82772831ccf908dab54a6e848f21f2de22dbdd5f
        composable(ROUTE_PROVIDER_HOME) { ProviderHomeScreen(navController, profileViewModel) }
        composable(ROUTE_USER_PROFILE) { UserProfileScreen(navController, profileViewModel) }
        composable(ROUTE_ADD_SERVICES) { ServicesScreen(navController, profileViewModel) }
        composable(ROUTE_BASIC_DETAILS) { BasicDetailsScreen(navController, profileViewModel) }
<<<<<<< HEAD
        composable(ROUTE_LOCATION) { LocationScreen(navController) }
        composable(ROUTE_PROVIDER_DASHBOARD) { ProviderDashboardScreen(navController, profileViewModel) }
=======
        composable(ROUTE_PROVIDER_DASHBOARD) { ProviderDashboardScreen(navController) }
>>>>>>> 82772831ccf908dab54a6e848f21f2de22dbdd5f
        composable(ROUTE_SELLER_PROFILE) { SellerProfileScreen(navController, profileViewModel) }
        composable(ROUTE_SETTINGS) { 
            SettingsScreen(
                navController = navController, 
                isDarkTheme = isDarkTheme, 
                onThemeChange = onThemeChange
            ) 
        }
<<<<<<< HEAD
        composable(ROUTE_SEARCH) { SearchScreen(navController, profileViewModel) }
=======
        composable(ROUTE_SEARCH) { SearchScreen(navController) }
        composable(ROUTE_LOCATION) { LocationScreen(navController, profileViewModel) }
>>>>>>> 82772831ccf908dab54a6e848f21f2de22dbdd5f
    }
}
