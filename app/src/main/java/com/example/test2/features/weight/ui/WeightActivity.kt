package com.example.test2.features.weight.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.test2.features.weight.data.local.WeightEntity
import com.example.test2.features.weight.data.repository.WeightRepositoryImpl
import com.example.test2.features.weight.ui.viewmodel.WeightViewModel
import com.example.test2.ui.theme.Test2Theme
import java.time.format.DateTimeFormatter
import java.util.Locale


class WeightActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        val viewModel = ViewModelProvider(
            this,
            WeightViewModel.Factory(WeightRepositoryImpl)
        )[WeightViewModel::class.java]

        setContent {
            Test2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WeightScreen(viewModel, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}


@Composable
fun WeightScreen(
    viewModel: WeightViewModel,
    modifier: Modifier
) {

    val weights: List<WeightEntity>  by viewModel.weights.collectAsState()
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
                weight: Float->
            viewModel.addWeight(weight)
        })
        JustTheList(
            weights,
            {
                    weight: WeightEntity->
                viewModel.deleteWeight(weight)
            }
        )
    }

}

@Composable
fun FieldWithCow (
    weightAddPressed: (Float) -> Unit
) {


    var weightText by remember {
        mutableStateOf("")
    }


    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {


        OutlinedTextField(
            value = weightText,
            onValueChange = { newValue ->

                val filtered = buildString {
                    var dotCount = 0

                    newValue.forEach { char ->
                        when {
                            char.isDigit() -> append(char)

                            char == '.' && dotCount == 0 -> {
                                append(char)
                                dotCount++
                            }
                        }
                    }
                }

                weightText = filtered
            },
            label = {
                Text("Weight")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            )
        )

        val weightValue = weightText
            .replace(',', '.')
            .toFloatOrNull()

        Button(
            enabled = weightValue != null,
            onClick = {

                weightValue?.let { value ->
                        weightAddPressed(value)
                        weightText = ""
                    }
            }
        ) {
            Text("Add")
        }
    }

}

@Composable
fun JustTheList(
     weights: List<WeightEntity>,
     weightDeletePressed: (WeightEntity) -> Unit
) {
    if( weights.isEmpty()  ){
        Text(
            text = "Empty View",

            style = MaterialTheme.typography.headlineSmall
        )
        return
    }

    Column {

        Text(
            text = "Weights (${weights.size})",
            style = MaterialTheme.typography.headlineSmall
        )

        LazyColumn {
            items(weights) { weight : WeightEntity ->
                JustTheItem(weight, weightDeletePressed)
            }
        }
    }
}


@Composable
fun JustTheItem(
    weight: WeightEntity,
    weightDeletePressed: (WeightEntity) -> Unit
) {

    val locale = Locale.getDefault()

    val pattern = when (locale.country.uppercase()) {
        "US" -> "MM-dd-yyyy"
        "JP" -> "yyyy-MM-dd"
        else -> "dd-MM-yyyy"
    }

    val formattedDate = weight.date.format(
        DateTimeFormatter.ofPattern(pattern)
    )

    val formattedWeight = String.format(
        locale,
        "%.2f",
        weight.weight
    )

    Row{
        Text(
            text = "$formattedDate - $formattedWeight"
        )
        Button(
            onClick = {
                weightDeletePressed(weight)
            }
        ) {
            Text("Delete")
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Test2Theme {
        WeightScreen("Android")
    }
}*/