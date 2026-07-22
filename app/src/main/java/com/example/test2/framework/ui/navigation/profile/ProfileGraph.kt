package com.example.test2.framework.ui.navigation.profile

import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.test2.features.water.data.repository.WaterRepositoryImpl
import com.example.test2.features.water.ui.WaterScreen
import com.example.test2.features.water.ui.viewmodel.WaterViewModel

@Composable
fun ProfileGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = "profile",
        modifier = Modifier.safeDrawingPadding()
    ) {

        composable("profile") {
            val viewModel: WaterViewModel = viewModel(
                factory = WaterViewModel.Factory(
                    WaterRepositoryImpl
                )
            )
            WaterScreen(viewModel)
        }

    }
}