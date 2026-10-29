package com.example.test2.framework.ui.navigation.measures

import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.test2.features.measures.ui.MeasuresScreen
import com.example.test2.features.weight.data.repository.WeightRepositoryImpl
import com.example.test2.features.weight.ui.WeightScreen
import com.example.test2.features.weight.ui.viewmodel.WeightViewModel

@Composable
fun MeasuresGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = "measures",
        modifier = Modifier.safeDrawingPadding()
    ) {

        composable("measures") {
            MeasuresScreen(
                onWeight = {
                    navController.navigate("weight")
                },
                onNumberTwo = {
                    //navController.navigate("numberTwo")
                },
                onPill = {
                    //navController.navigate("pill")
                },
                onWaterIntake = {
                    //navController.navigate("activity")
                },
            )
        }

        composable("weight") {
            val viewModel: WeightViewModel = viewModel(
                factory = WeightViewModel.Factory(
                    WeightRepositoryImpl
                )
            )

            WeightScreen(viewModel)
        }
    }
}