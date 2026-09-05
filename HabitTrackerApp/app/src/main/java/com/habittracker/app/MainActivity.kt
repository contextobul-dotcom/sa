package com.habittracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.habittracker.app.ui.screens.HabitsScreen
import com.habittracker.app.ui.screens.HomeScreen
import com.habittracker.app.ui.screens.NegativeAppsScreen
import com.habittracker.app.ui.theme.HabitTrackerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (application as HabitTrackerApplication).container

        setContent {
            HabitTrackerTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            container = container,
                            onNavigateToHabits = { navController.navigate("habits") },
                            onNavigateToNegativeApps = { navController.navigate("negativeApps") }
                        )
                    }
                    composable("habits") {
                        HabitsScreen(
                            container = container,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("negativeApps") {
                        NegativeAppsScreen(
                            application = application,
                            container = container,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
