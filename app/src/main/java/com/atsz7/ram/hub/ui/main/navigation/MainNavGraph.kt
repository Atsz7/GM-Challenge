package com.atsz7.ram.hub.ui.main.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.atsz7.ram.hub.ui.main.screens.detail.coordinator.rememberDetailCoordinator
import com.atsz7.ram.hub.ui.main.screens.list.coordinator.rememberListCoordinator
import com.atsz7.ram.hub.ui.main.screens.detail.DetailScreen
import com.atsz7.ram.hub.ui.main.screens.list.ListScreen

@Composable
fun MainNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = MainRoute.Characters) {

        composable<MainRoute.Characters> {
            ListScreen(
                coordinator = rememberListCoordinator(
                    onNavigateToDetail = { id ->
                        navController.navigate(MainRoute.Detail(characterId = id))
                    }
                )
            )
        }

        composable<MainRoute.Detail> {
            DetailScreen(
                coordinator = rememberDetailCoordinator(
                    onBackClick = { navController.popBackStack() }
                )
            )
        }
    }
}
