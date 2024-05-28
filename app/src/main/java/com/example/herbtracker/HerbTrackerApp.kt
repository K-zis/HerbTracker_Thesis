package com.example.herbtracker

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.inventory.ui.navigation.HerbTrackerNavHost

@Composable
fun HerbTrackerApp(navController: NavHostController = rememberNavController()) {
    HerbTrackerNavHost(navController = navController)
}