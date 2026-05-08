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
        composable(ROUTE_SPLASH) { Splashscreen(navController) }
        composable(ROUTE_START) { StartScreen(navController) }
        composable(ROUTE_LOGIN) { LoginScreen(navController) }
        composable(ROUTE_REGISTER_USER) { RegisterUserScreen(navController) }
        composable(ROUTE_REGISTER_PROVIDER) { RegisterProviderScreen(navController) }
        composable(ROUTE_USER_HOME) { UserHomeScreen(navController) }
        composable(ROUTE_PROVIDER_HOME) { ProviderHomeScreen(navController, profileViewModel) }
        composable(ROUTE_USER_PROFILE) { UserProfileScreen(navController, profileViewModel) }
        composable(ROUTE_ADD_SERVICES) { ServicesScreen(navController) }
        composable(ROUTE_BASIC_DETAILS) { BasicDetailsScreen(navController, profileViewModel) }
        composable(ROUTE_PROVIDER_DASHBOARD) { ProviderDashboardScreen(navController) }
        composable(ROUTE_SELLER_PROFILE) { SellerProfileScreen(navController) }
        composable(ROUTE_SETTINGS) { 
            SettingsScreen(
                navController = navController, 
                isDarkTheme = isDarkTheme, 
                onThemeChange = onThemeChange
            ) 
        }
        composable(ROUTE_SEARCH) { SearchScreen(navController) }
    }
}
