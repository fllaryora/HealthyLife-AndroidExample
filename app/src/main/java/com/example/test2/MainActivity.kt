package com.example.test2

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.test2.features.weight.ui.WeightScreen
import com.example.test2.ui.theme.Test2Theme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.test2.features.water.data.repository.WaterRepositoryImpl
import com.example.test2.features.water.ui.WaterScreen
import com.example.test2.features.water.ui.viewmodel.WaterViewModel
import com.example.test2.features.weight.data.repository.WeightRepositoryImpl
import com.example.test2.features.weight.ui.viewmodel.WeightViewModel


enum class AppDestinations(
    val label: String,
    val icon: Int
) {
    HOME(
        "Home",
        R.drawable.ic_home
    ),
    FAVORITES(
        "Favorites",
        R.drawable.ic_favorite
    ),
    PROFILE(
        "Profile",
        R.drawable.ic_account_box
    )
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            Test2Theme {
                Test2App()
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp",
    uiMode = Configuration.UI_MODE_NIGHT_YES or
            Configuration.UI_MODE_TYPE_NORMAL,
    name = "Preview"
)
@Composable
fun Test2App() {

    var currentTab by rememberSaveable {
        mutableStateOf(AppDestinations.HOME)
    }

    val homeNavController = rememberNavController()
    val favoritesNavController = rememberNavController()
    val profileNavController = rememberNavController()

    NavigationSuiteScaffold(
        navigationSuiteItems = {

            AppDestinations.entries.forEach { destination ->

                item(
                    selected = currentTab == destination,
                    onClick = {
                        currentTab = destination
                    },
                    icon = {
                        Icon(
                            painterResource(destination.icon),
                            contentDescription = destination.label
                        )
                    },
                    label = {
                        Text(destination.label)
                    }
                )
            }
        }
    ) {

        when (currentTab) {

            AppDestinations.HOME -> {
                HomeGraph(homeNavController)
            }

            AppDestinations.FAVORITES -> {
                FavoritesGraph(favoritesNavController)
            }

            AppDestinations.PROFILE -> {
                ProfileGraph(profileNavController)
            }
        }
    }
}


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
            Greeting(
                onWeight = {
                    navController.navigate("weight")
                }
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

@Composable
fun Greeting(
    onWeight: ()-> Unit
) {

    Column {

        Text(
            text = "Pantalla HOME"
        )

        Button(
            onClick = {
                onWeight()
            }
        ) {
            Text("Abrir Weight Screen")
        }
    }
}