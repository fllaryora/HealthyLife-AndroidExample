package com.example.test2.features.recordactivity.ui

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
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.test2.features.recordactivity.data.local.ActivityTakenEntity
import com.example.test2.features.recordactivity.ui.viewmodel.ActivityTakenViewModel

import java.time.format.DateTimeFormatter
import java.util.Locale


@Composable
fun ActivityTakenScreen(
    viewModel: ActivityTakenViewModel,
    modifier: Modifier = Modifier
) {

    val pillsTaken: List<ActivityTakenEntity>  by viewModel.activitiesTaken.collectAsState()
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
            viewModel.addActivityTaken()
        })
        JustTheList(
            pillsTaken,
            {
                    activityTaken: ActivityTakenEntity->
                viewModel.deleteActivityTaken(activityTaken)
            }
        )
    }

}

@Composable
fun FieldWithCow (
    activityTakenAddPressed: () -> Unit
) {


    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Button(
            onClick = {
                activityTakenAddPressed()

            }
        ) {
            Text("Add")
        }
    }

}

@Composable
fun JustTheList(
    activitiesTaken: List<ActivityTakenEntity>,
     activityTakenDeletePressed: (ActivityTakenEntity) -> Unit
) {
    if( activitiesTaken.isEmpty()  ){
        Text(
            text = "Empty View",

            style = MaterialTheme.typography.headlineSmall
        )
        return
    }

    Column {

        Text(
            text = "Activities Taken (${activitiesTaken.size})",
            style = MaterialTheme.typography.headlineSmall
        )

        LazyColumn {
            items(activitiesTaken) { activityTaken : ActivityTakenEntity ->
                JustTheItem(activityTaken, activityTakenDeletePressed)
            }
        }
    }
}


@Composable
fun JustTheItem(
    activityTaken : ActivityTakenEntity,
    activityTakenDeletePressed: (ActivityTakenEntity) -> Unit
) {

    val locale = Locale.getDefault()

    val pattern = when (locale.country.uppercase()) {
        "US" -> "MM-dd-yyyy"
        "JP" -> "yyyy-MM-dd"
        else -> "dd-MM-yyyy"
    }

    val formattedDate = activityTaken.date.format(
        DateTimeFormatter.ofPattern(pattern)
    )

    Row{
        Text(
            text = "$formattedDate - ${activityTaken.activity.target.name}"
        )
        Button(
            onClick = {
                activityTakenDeletePressed(activityTaken)
            }
        ) {
            Text("Delete")
        }
    }
}
