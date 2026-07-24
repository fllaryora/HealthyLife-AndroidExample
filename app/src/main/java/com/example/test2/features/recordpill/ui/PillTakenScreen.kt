package com.example.test2.features.recordpill.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.test2.features.recordpill.data.local.PillTakenEntity
import com.example.test2.features.recordpill.ui.viewmodel.PillTakenViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale


@Composable
fun PillTakenScreen(
    viewModel: PillTakenViewModel,
    modifier: Modifier = Modifier
) {

    val pillsTaken: List<PillTakenEntity>  by viewModel.pillsTaken.collectAsState()
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
        FieldWithCow( { ->
            viewModel.addPillTaken()
        })
        JustTheList(
            pillsTaken,
            {
                    pillTaken: PillTakenEntity->
                viewModel.deletePillTaken(pillTaken)
            }
        )
    }

}

@Composable
fun FieldWithCow (
    pillTakenAddPressed: () -> Unit
) {


    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Button(
            onClick = {
                pillTakenAddPressed()

            }
        ) {
            Text("Add")
        }
    }

}

@Composable
fun JustTheList(
    pillsTaken: List<PillTakenEntity>,
    pillTakenDeletePressed: (PillTakenEntity) -> Unit
) {
    if( pillsTaken.isEmpty()  ){
        Text(
            text = "Empty View",

            style = MaterialTheme.typography.headlineSmall
        )
        return
    }

    Column {

        Text(
            text = "Pill Takens (${pillsTaken.size})",
            style = MaterialTheme.typography.headlineSmall
        )

        LazyColumn {
            items(pillsTaken) { pillTaken : PillTakenEntity ->
                JustTheItem(pillTaken, pillTakenDeletePressed)
            }
        }
    }
}


@Composable
fun JustTheItem(
    pillTaken : PillTakenEntity,
    pillTakenDeletePressed: (PillTakenEntity) -> Unit
) {

    val locale = Locale.getDefault()

    val pattern = when (locale.country.uppercase()) {
        "US" -> "MM-dd-yyyy"
        "JP" -> "yyyy-MM-dd"
        else -> "dd-MM-yyyy"
    }

    val formattedDate = pillTaken.date.format(
        DateTimeFormatter.ofPattern(pattern)
    )

    Row{
        Text(
            text = "$formattedDate - ${pillTaken.pillEntity.target.name}"
        )
        Button(
            onClick = {
                pillTakenDeletePressed(pillTaken)
            }
        ) {
            Text("Delete")
        }
    }
}
