package com.example.zuru.screens.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.zuru.screens.aboutScreen.AboutUsScreen
import com.example.zuru.screens.authScreen.AuthScreen
import com.example.zuru.screens.booking_confirmation.BookingConfirmationScreen
import com.example.zuru.screens.changePassword.ChangePasswordScreen
import com.example.zuru.screens.confirmation.ConfirmationScreen
import com.example.zuru.screens.contactScreen.ContactUsScreen
import com.example.zuru.screens.editProfileScreen.EditProfileScreen
import com.example.zuru.screens.exploreScreen.ExploreScreen
import com.example.zuru.screens.homeScreen.HomeScreen
import com.example.zuru.screens.myBookings.MyBookingsScreen
import com.example.zuru.screens.myPayments.MyPaymentsScreen
import com.example.zuru.screens.onboarding.OnboardingScreen
import com.example.zuru.screens.profileScreen.ProfileScreen
import com.example.zuru.screens.settings.SettingsScreen
import com.example.zuru.screens.splashScreen.SplashScreen
import com.example.zuru.screens.paymentsScreen.PaymentsScreen
import com.example.zuru.screens.receiptsScreen.ReceiptScreen
import com.example.zuru.screens.receiptsScreen.AllReceiptsScreen
import com.example.zuru.screens.upcoming.UpcomingTripsScreen
import com.example.zuru.screens.viewmodels.SettingsViewModel

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier, settingsViewModel: SettingsViewModel) {
    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = modifier
    ) {
        composable("splash") {
            SplashScreen(navController)
        }

        composable("auth") {
            AuthScreen(navController)
        }

        composable("home") {
            HomeScreen(navController)
        }

        composable("explore") {
            ExploreScreen(navController)
        }

        composable("contact") {
            ContactUsScreen(navController)
        }

        composable("profile") {
            ProfileScreen(navController)
        }

        composable("animation") {
            ConfirmationScreen(navController)
        }

        composable("settings") {
            SettingsScreen(navController = navController, settingsViewModel = settingsViewModel)
        }

        composable("edit_profile") {
            EditProfileScreen(navController)
        }

        composable("change_password") {
            ChangePasswordScreen(navController)
        }
        composable("about") {
            AboutUsScreen(navController)
        }


        composable(
            route = "payments/{destination}",
            arguments = listOf(navArgument("destination") { type = NavType.StringType })
        ) { backStackEntry ->
            val destination = backStackEntry.arguments?.getString("destination") ?: ""
            PaymentsScreen(navController, destination)
        }

        composable(
            route = "bookingConfirmation/{destination}",
            arguments = listOf(navArgument("destination") { type = NavType.StringType })
        ) { backStackEntry ->
            val destination = backStackEntry.arguments?.getString("destination") ?: ""
            BookingConfirmationScreen(navController, destination)
        }

        composable("myBookings") {
            MyBookingsScreen(navController)
        }

        composable("myPayments") {
            MyPaymentsScreen(navController)
        }

        // Route for AllReceiptsScreen
        composable("allReceipts") {
            AllReceiptsScreen(navController = navController)
        }
        composable("onboarding") {
            OnboardingScreen(navController)
        }
        composable("upcomingTrips") {
             UpcomingTripsScreen(navController)
        }


        // Route for ReceiptScreen
        composable(
            route = "receipt/{destination}/{method}/{amount}/{timestamp}/{travelMode}/{vehicleType}/{dateofTravel}",
            arguments = listOf(
                navArgument("destination") { type = NavType.StringType },
                navArgument("method") { type = NavType.StringType },
                navArgument("amount") { type = NavType.StringType },
                navArgument("timestamp") { type = NavType.LongType },
                navArgument("travelMode") { type = NavType.StringType },
                navArgument("vehicleType") { type = NavType.StringType },
                navArgument("dateofTravel") { type = NavType.StringType },
            )
        ) {
            val destination = it.arguments?.getString("destination") ?: ""
            val method = it.arguments?.getString("method") ?: ""
            val amount = it.arguments?.getString("amount") ?: ""
            val timestamp = it.arguments?.getLong("timestamp") ?: 0L
            val travelMode = it.arguments?.getString("travelMode") ?: ""
            val vehicleType = it.arguments?.getString("vehicleType") ?: ""
            val dateofTravel = it.arguments?.getString("dateofTravel") ?: ""

            ReceiptScreen(
                navController,
                destination,
                method,
                amount,
                timestamp,
                travelMode,
                vehicleType,
                dateofTravel
            )
        }
    }
}