package com.example.test2.features.exportimport.ui

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
import com.example.test2.features.exportimport.ui.viewmodel.ExportViewModel

@Composable
fun ExportScreen(
    viewModel: ExportViewModel,
    modifier: Modifier = Modifier
) {

    val loading by viewModel.loading.collectAsState()
    val context = LocalContext.current

    val createDocumentLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument("application/json")
        ) { uri: Uri? ->

            uri?.let {
                viewModel.exportToFile(
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
                createDocumentLauncher.launch("backup.json")
            }
        ) {
            Text("Share File")
        }

        Button(
            onClick = {
                viewModel.exportAndShare(context)
            }
        ) {
            Text("Share (WhatsApp)")
        }
    }
}