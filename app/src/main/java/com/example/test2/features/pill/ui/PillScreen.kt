package com.example.test2.features.pill.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.test2.features.pill.data.local.PillEntity
import com.example.test2.features.pill.ui.viewmodel.PillViewModel

@Composable
fun PillScreen(
    viewModel: PillViewModel,
    pillTakenAddPressed: (PillEntity) -> Unit,
    modifier: Modifier = Modifier
) {

    val pills: List<PillEntity>  by viewModel.pills.collectAsState()
    val loading: Boolean  by viewModel.loading.collectAsState()

    if (loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        return
    }

    Column ( modifier = modifier) {
        FieldWithCow({
                pillName: String->
            viewModel.addPill(pillName)
        })
        JustTheList(
            pills,
            {
                    pill: PillEntity->
                viewModel.deletePill(pill)
            },
            {
                    pill: PillEntity->
                pillTakenAddPressed(pill)
            }
        )
    }

}

@Composable
fun FieldWithCow (
    pillAddPressed: (String) -> Unit
) {


    var pillText by remember {
        mutableStateOf("")
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        OutlinedTextField(
            value = pillText,
            onValueChange = { pillText = it },
            label = {
                Text("Pill Name")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text
            )
        )

        Button(
            enabled = pillText.isNotBlank(),
            onClick = {
                pillAddPressed(pillText)
                pillText = ""
            }
        ) {
            Text("Add")
        }
    }

}

@Composable
fun JustTheList(
    pills: List<PillEntity>,
    pillDeletePressed: (PillEntity) -> Unit,
    pillTakenAddPressed: (PillEntity) -> Unit,
) {
    if( pills.isEmpty()  ){
        Text(
            text = "Empty View",

            style = MaterialTheme.typography.headlineSmall
        )
        return
    }

    Column {

        Text(
            text = "Pills (${pills.size})",
            style = MaterialTheme.typography.headlineSmall
        )

        LazyColumn {
            items(pills) { pill : PillEntity ->
                JustTheItem(pill, pillDeletePressed, pillTakenAddPressed)
            }
        }
    }
}


@Composable
fun JustTheItem(
    pill: PillEntity,
    pillDeletePressed: (PillEntity) -> Unit,
    pillTakenAddPressed: (PillEntity) -> Unit,
) {

    Row{
        Text(
            text = pill.name
        )
        Button(
            onClick = {
                pillDeletePressed(pill)
            }
        ) {
            Text("Delete")
        }
        Button(
            onClick = {
                pillTakenAddPressed(pill)
            }
        ) {
            Text("Add PILL INTAKE")
        }
    }
}
