package com.example.test2.features.numbertwo.ui

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
import com.example.test2.features.numbertwo.data.local.NumberTwoEntity
import com.example.test2.features.numbertwo.ui.viewmodel.NumberTwoViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale


@Composable
fun NumberTwoScreen(
    viewModel: NumberTwoViewModel,
    modifier: Modifier = Modifier
) {

    val wcs: List<NumberTwoEntity>  by viewModel.wc.collectAsState()
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
            viewModel.addWCVisit()
        })
        JustTheList(
            wcs,
            {
                    wc: NumberTwoEntity->
                viewModel.deleteWC(wc)
            }
        )
    }

}

@Composable
fun FieldWithCow (
    wcAddPressed: () -> Unit
) {

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = {

                    wcAddPressed()
            }
        ) {
            Text("Record WC")
        }
    }

}

@Composable
fun JustTheList(
    waters: List<NumberTwoEntity>,
    wcDeletePressed: (NumberTwoEntity) -> Unit
) {
    if( waters.isEmpty()  ){
        Text(
            text = "Empty View",

            style = MaterialTheme.typography.headlineSmall
        )
        return
    }

    Column {

        Text(
            text = "Water Intakes (${waters.size})",
            style = MaterialTheme.typography.headlineSmall
        )

        LazyColumn {
            items(waters) { wc : NumberTwoEntity ->
                JustTheItem(wc, wcDeletePressed)
            }
        }
    }
}


@Composable
fun JustTheItem(
    wc: NumberTwoEntity,
    wcDeletePressed: (NumberTwoEntity) -> Unit
) {

    val locale = Locale.getDefault()

    val pattern = when (locale.country.uppercase()) {
        "US" -> "MM-dd-yyyy"
        "JP" -> "yyyy-MM-dd"
        else -> "dd-MM-yyyy"
    }

    val formattedDate = wc.date.format(
        DateTimeFormatter.ofPattern(pattern)
    )


    Row{
        Text(
            text = "$formattedDate"
        )
        Button(
            onClick = {
                wcDeletePressed(wc)
            }
        ) {
            Text("Delete")
        }
    }
}
