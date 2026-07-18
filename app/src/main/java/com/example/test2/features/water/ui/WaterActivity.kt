package com.example.test2.features.water.ui

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.test2.features.water.data.local.WaterEntity
import com.example.test2.features.water.data.repository.WaterRepositoryImpl
import com.example.test2.features.water.ui.viewmodel.WaterViewModel
import com.example.test2.ui.theme.Test2Theme
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.text.forEach

class WaterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel = ViewModelProvider(
            this,
            WaterViewModel.Factory(WaterRepositoryImpl)
        )[WaterViewModel::class.java]

        setContent {
            Test2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WaterScreen(viewModel, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}


@Composable
fun WaterScreen(
    viewModel: WaterViewModel,
    modifier: Modifier = Modifier
) {

    val waters: List<WaterEntity>  by viewModel.waters.collectAsState()
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
                volume: Float->
            viewModel.addWaterIntake(volume)
        })
        JustTheList(
            waters,
            {
                    water: WaterEntity->
                viewModel.deleteWater(water)
            }
        )
    }

}

@Composable
fun FieldWithCow (
    waterAddPressed: (Float) -> Unit
) {


    var waterText by remember {
        mutableStateOf("")
    }


    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {


        OutlinedTextField(
            value = waterText,
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

                waterText = filtered
            },
            label = {
                Text("Water Intake")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            )
        )

        val waterValue = waterText
            .replace(',', '.')
            .toFloatOrNull()

        Button(
            enabled = waterValue != null,
            onClick = {

                waterValue?.let { value ->
                    waterAddPressed(value)
                    waterText = ""
                }
            }
        ) {
            Text("Add Intake")
        }
    }

}

@Composable
fun JustTheList(
    waters: List<WaterEntity>,
    waterDeletePressed: (WaterEntity) -> Unit
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
            items(waters) { water : WaterEntity ->
                JustTheItem(water, waterDeletePressed)
            }
        }
    }
}


@Composable
fun JustTheItem(
    water: WaterEntity,
    waterDeletePressed: (WaterEntity) -> Unit
) {

    val locale = Locale.getDefault()

    val pattern = when (locale.country.uppercase()) {
        "US" -> "MM-dd-yyyy"
        "JP" -> "yyyy-MM-dd"
        else -> "dd-MM-yyyy"
    }

    val formattedDate = water.date.format(
        DateTimeFormatter.ofPattern(pattern)
    )

    val formattedVolume = String.format(
        locale,
        "%.2f",
        water.volume
    )

    Row{
        Text(
            text = "$formattedDate - $formattedVolume"
        )
        Button(
            onClick = {
                waterDeletePressed(water)
            }
        ) {
            Text("Delete")
        }
    }
}
