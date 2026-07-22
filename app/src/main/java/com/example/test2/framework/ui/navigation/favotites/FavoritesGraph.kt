package com.example.test2.framework.ui.navigation.favotites

import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.test2.features.weight.data.repository.WeightRepositoryImpl
import com.example.test2.features.weight.ui.WeightScreen
import com.example.test2.features.weight.ui.viewmodel.WeightViewModel

@Composable
fun FavoritesGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = "favorites",
        modifier = Modifier.safeDrawingPadding()
    ) {

        composable("favorites") {
            val viewModel: WeightViewModel = viewModel(
                factory = WeightViewModel.Factory(
                    WeightRepositoryImpl
                )
            )

            WeightScreen(viewModel)
        }
    }
}