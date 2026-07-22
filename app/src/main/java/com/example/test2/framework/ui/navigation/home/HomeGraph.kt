package com.example.test2.framework.ui.navigation.home

import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import com.example.test2.features.dailyactivity.data.repository.ActivityRepositoryImpl
import com.example.test2.features.dailyactivity.ui.ActivityScreen
import com.example.test2.features.dailyactivity.ui.viewmodel.ActivityViewModel
import com.example.test2.features.home.ui.HomeScreen
import com.example.test2.features.numbertwo.data.repository.NumberTwoRepositoryImpl
import com.example.test2.features.numbertwo.ui.NumberTwoScreen
import com.example.test2.features.numbertwo.ui.viewmodel.NumberTwoViewModel
import com.example.test2.features.pill.data.local.PillEntity
import com.example.test2.features.pill.data.repository.PillRepositoryImpl
import com.example.test2.features.pill.ui.PillScreen
import com.example.test2.features.pill.ui.viewmodel.PillViewModel
import com.example.test2.features.recordactivity.data.repository.ActivityTakenRepositoryImpl
import com.example.test2.features.recordactivity.ui.ActivityTakenScreen
import com.example.test2.features.recordactivity.ui.viewmodel.ActivityTakenViewModel
import com.example.test2.features.recordpill.data.repository.PillTakenRepositoryImpl
import com.example.test2.features.recordpill.ui.PillTakenScreen
import com.example.test2.features.recordpill.ui.viewmodel.PillTakenViewModel
import com.example.test2.features.weight.data.repository.WeightRepositoryImpl
import com.example.test2.features.weight.ui.WeightScreen
import com.example.test2.features.weight.ui.viewmodel.WeightViewModel


@Composable
fun HomeGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = Modifier.safeDrawingPadding()
    ) {

        composable("home") {
            HomeScreen(
                onWeight = {
                    navController.navigate("weight")
                },
                onNumberTwo = {
                    navController.navigate("numberTwo")
                },
                onPill = {
                    navController.navigate("pill")
                },
                onActivity = {
                    navController.navigate("activity")
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

        composable("numberTwo") {

            val viewModel: NumberTwoViewModel = viewModel(
                factory = NumberTwoViewModel.Factory(
                    NumberTwoRepositoryImpl
                )
            )

            NumberTwoScreen(
                viewModel
            )
        }

        composable("pill") {

            val viewModel: PillViewModel = viewModel(
                factory = PillViewModel.Factory(
                    PillRepositoryImpl
                )
            )
            PillScreen(viewModel, pillTakenAddPressed = {
                    pill : PillEntity->

                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("pill", pill)

                navController.navigate("pillIntake")
            })
        }

        composable("activity") {

            val viewModel: ActivityViewModel = viewModel(
                factory = ActivityViewModel.Factory(
                    ActivityRepositoryImpl
                )
            )
            ActivityScreen(viewModel, activityTakenAddPressed = {
                    activity : DailyActivityEntity->

                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("activity", activity)

                navController.navigate("activityRecord")
            })
        }

        composable(route = "pillIntake") { backStackEntry ->

            val pill :PillEntity = requireNotNull(
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<PillEntity>("pill")
            ) {
                "pill is mandatory"
            }


            val viewModel: PillTakenViewModel = viewModel(
                factory = PillTakenViewModel.Factory(
                    PillTakenRepositoryImpl, pill
                )
            )
            PillTakenScreen(viewModel)
        }

        composable(route = "activityRecord") { backStackEntry ->

            val activity : DailyActivityEntity = requireNotNull(
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<DailyActivityEntity>("activity")
            ) {
                "DailyActivityEntity is mandatory"
            }


            val viewModel: ActivityTakenViewModel = viewModel(
                factory = ActivityTakenViewModel.Factory(
                    ActivityTakenRepositoryImpl, activity
                )
            )
            ActivityTakenScreen(viewModel)
        }


    }
}

