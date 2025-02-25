package com.test.github.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.test.github.ui.screen.ScreenMain
import com.test.github.ui.screen.ScreenRepository
import kotlinx.serialization.Serializable

@Composable
fun AppNavHost(navController: NavHostController)  {
    NavHost(
        navController,
        startDestination = MainScreen,
    ) {
        composable<MainScreen> {
            ScreenMain( navController = navController)
        }
        composable<DetailsScreen> {
            ScreenRepository(args = it.toRoute<DetailsScreen>(), navController = navController)
        }
    }
}

@Serializable
object MainScreen

@Serializable
data class DetailsScreen(
    val data: String
)