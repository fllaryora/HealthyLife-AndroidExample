package com.example.test2

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.test2.framework.ui.navigation.favotites.FavoritesGraph
import com.example.test2.framework.ui.navigation.home.HomeGraph
import com.example.test2.framework.ui.navigation.profile.ProfileGraph

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp",
    uiMode = Configuration.UI_MODE_NIGHT_YES or
            Configuration.UI_MODE_TYPE_NORMAL,
    name = "Preview"
)
@Composable
fun MainComponentActivity() {
    var currentTab by rememberSaveable {
        mutableStateOf(AppDestinations.HOME)
    }

    val homeNavController = rememberNavController()
    val measuresNavController = rememberNavController()
    val schedulingNavController = rememberNavController()
    val performanceNavController = rememberNavController()

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
                    }
                )
            }
        }
    ) {
        AppNavigationDrawer(
            currentTab = currentTab,
            onDestinationSelected = {currentTab= it}
        ) { openDrawer ->
            Scaffold(topBar = {
                TopBarComponent (currentTab = currentTab, openDrawer = openDrawer)
            }
            ) { innerPadding ->
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                    when (currentTab) {

                        AppDestinations.HOME -> {
                            BackgroundContent{
                                HomeGraph(homeNavController)
                            }
                        }

                        AppDestinations.MEASURES -> {
                            BackgroundContent {
                                FavoritesGraph(measuresNavController)
                            }
                        }

                        AppDestinations.SCHEDULING -> {
                            BackgroundContent {
                                ProfileGraph(schedulingNavController)
                            }
                        }
                        AppDestinations.PERFORMANCE -> {
                            BackgroundContent {
                                ProfileGraph(performanceNavController)
                            }
                        }
                    }
                }
            }
        }

    }
}
