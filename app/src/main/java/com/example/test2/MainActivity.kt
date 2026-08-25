package com.example.test2

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.test2.features.importer.domain.repository.ImportUseCaseRepositoryImpl
import com.example.test2.framework.ui.navigation.favotites.FavoritesGraph
import com.example.test2.framework.ui.navigation.home.HomeGraph
import com.example.test2.framework.ui.navigation.profile.ProfileGraph
import com.example.test2.ui.theme.Test2Theme
import com.example.test2.viewmodel.MainViewModel


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

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = ViewModelProvider(
            this,
            MainViewModel.Factory(
                ImportUseCaseRepositoryImpl
            )
        )[MainViewModel::class.java]

        handleSendIntent(intent)

        enableEdgeToEdge()

        setContent {
            Test2Theme {
                Test2App()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        handleSendIntent(intent)
    }

    private fun handleSendIntent(intent: Intent) {
        //Handle only things related to content receiver
        if (intent.action != Intent.ACTION_SEND) {
            return
        }
        val fileUri: Uri? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(
                    Intent.EXTRA_STREAM,
                    Uri::class.java
                )
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(
                    Intent.EXTRA_STREAM
                )
            }

        fileUri?.let {
            mainViewModel.importFromOtherApp(
                context = this,
                uri = it
            )
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

