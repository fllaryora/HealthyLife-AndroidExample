package com.example.test2.features.home.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test2.BackgroundContent
import com.example.test2.R

@Composable
fun HomeScreen(
    onWeight: () -> Unit,
    onNumberTwo: () -> Unit,
    onPill: () -> Unit,
    onActivity: () -> Unit,
    onExport: () -> Unit,
    onImport: () -> Unit,
) {

    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.0.dp)
    ){
        WelcomeCard()

        Button(
            onClick = {
                onWeight()
            }
        ) {
            Text("Abrir Weight Screen")
        }

        Button(
            onClick = {
                onNumberTwo()
            }
        ) {
            Text("Abrir WC  Screen")
        }

        Button(
            onClick = {
                onPill()
            }
        ) {
            Text("Abrir  Pill  Screen")
        }

        Card (
            colors = CardDefaults.cardColors(containerColor = colorResource(R.color.colorPrimary)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            onClick = {
                onActivity()
            }
        ) {
            Column(
                modifier = Modifier.padding(15.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Abrir  Activity  Screen",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center
                )
            }
        }

        Button(
            onClick = {
                onExport()
            }
        ) {
            Text("Abrir Export Screen")
        }

        Button(
            onClick = {
                onImport()
            }
        ) {
            Text("Abrir Import Screen")
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
fun PreviewHomeScreen( ) {
    BackgroundContent {
        Column( modifier = Modifier.safeDrawingPadding().fillMaxSize()) {
            HomeScreen(
                onWeight = {
                },
                onNumberTwo = {
                },
                onPill = {
                },
                onActivity = {
                },
                onExport = {
                },
            ) {
            }
        }
    }

}

