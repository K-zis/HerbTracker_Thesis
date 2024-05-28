/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.inventory.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.herbtracker.ui.HerbAppViewModelProvider
import com.example.herbtracker.ui.home.HomeDestination
import com.example.herbtracker.ui.home.HomeScreen
import com.example.herbtracker.ui.home.HomeViewModel
import com.example.herbtracker.ui.screens.ClassificationDestination
import com.example.herbtracker.ui.screens.ClassificationScreen
import com.example.herbtracker.ui.screens.ClassificationViewModel
import com.example.herbtracker.ui.screens.MapDestination
import com.example.herbtracker.ui.screens.MapScreen
import com.example.herbtracker.ui.screens.MapViewModel


/**
 * Provides Navigation graph for the application.
 */
@Composable
fun HerbTrackerNavHost(
    homeViewModel: HomeViewModel = hiltViewModel(),
    mapViewModel: MapViewModel = viewModel(factory = HerbAppViewModelProvider.Factory),
    classificationViewModel: ClassificationViewModel = hiltViewModel(),
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {


    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                viewModel = homeViewModel,
                mapViewModel = mapViewModel,
                navigateToClassification = { navController.navigate(ClassificationDestination.route)},
                navigateToMap = { navController.navigate(MapDestination.route) }
            )
        }
        composable(route = ClassificationDestination.route) {
            ClassificationScreen(
                homeViewModel = homeViewModel,
                mapViewModel = mapViewModel,
                classificationViewModel = classificationViewModel,
                navigateBack = { navController.popBackStack() },
                navigateToHome = { navController.popBackStack(HomeDestination.route, inclusive = false) },
                navigateToMap = { navController.navigate( MapDestination.route ) }
            )
        }
        composable(route = MapDestination.route) {
            MapScreen(
                mapViewModel = mapViewModel,
                navigateBack = { navController.popBackStack() },
                navigateToHome = { navController.popBackStack(HomeDestination.route, inclusive = false) },
            )
        }
    }
}

