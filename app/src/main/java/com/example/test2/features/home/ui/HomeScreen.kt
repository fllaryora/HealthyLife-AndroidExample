package com.example.test2.features.home.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


@Composable
fun HomeScreen(
    onWeight: ()-> Unit,
    onNumberTwo: ()-> Unit,
    onPill: ()-> Unit,
    onActivity: ()-> Unit,
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

        Button(
            onClick = {
                onActivity()
            }
        ) {
            Text("Abrir  Activity  Screen")
        }
    }
}