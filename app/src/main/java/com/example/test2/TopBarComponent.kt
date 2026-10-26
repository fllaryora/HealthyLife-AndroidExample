package com.example.test2

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarComponent (currentTab: AppDestinations, openDrawer: () -> Unit) {
    TopAppBar(
        title = {
            Text( currentTab.label, color = Color.White)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(id = R.color.colorPrimary)
        ),
        navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(
                    painter = painterResource(R.drawable.hamburger),
                    contentDescription = "Abri menu",
                    tint = Color.White
                )
            }
        },
        actions = {
            var menuExpanded by remember { mutableStateOf(false) }
            IconButton(onClick = {menuExpanded = true }) {
                Icon(
                    painter = painterResource(R.drawable.more),
                    tint = Color.White,
                    contentDescription = "Mas opciones"
                )
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = {menuExpanded = false}
            ) {
                DropdownMenuItem(
                    text = {Text("Ajustes") },
                    onClick = {
                        menuExpanded = false
                        //todo
                    }
                )
                DropdownMenuItem(
                    text = {Text("Acerca de") },
                    onClick = {
                        menuExpanded = false
                        //todo
                    }
                )
            }

        }
    )
}