package com.example.test2

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.test2.features.importer.domain.repository.ImportUseCaseRepositoryImpl
import com.example.test2.ui.theme.Test2Theme
import com.example.test2.viewmodel.MainViewModel

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
                MainComponentActivity()
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

