package com.example.test2.features.importer.domain.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.test2.features.importer.domain.ui.viewmodel.ImportViewModel

@Composable
fun ImportScreen(
    viewModel: ImportViewModel,
    modifier: Modifier = Modifier
) {

    val loading by viewModel.loading.collectAsState()
    val context = LocalContext.current

    val openDocumentLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->

            uri?.let {
                viewModel.importAction(
                    context = context,
                    uri = it
                )
            }
        }

    if (loading) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Column {

        Button(
            onClick = {
                openDocumentLauncher.launch(
                    arrayOf("application/json")
                )
            }
        ) {
            Text("Read from File system")
        }

    }
}