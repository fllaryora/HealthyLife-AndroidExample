package com.example.test2.features.dailyactivity.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.test2.data.entities.enums.DaysOfWeekEnum
import com.example.test2.data.entities.enums.TypeofRecorder
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import com.example.test2.features.dailyactivity.ui.viewmodel.ActivityViewModel
import com.example.test2.data.entities.enums.fromMask
@Composable
fun ActivityScreen(
    viewModel: ActivityViewModel,
    modifier: Modifier = Modifier
) {

    val activities: List<DailyActivityEntity>  by viewModel.activities.collectAsState()
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

    Column ( modifier = modifier.verticalScroll(rememberScrollState())) {
        FieldWithCow({
            activityName,
            activityHour,
            activityMinute,
            activityDaysOfWeek,
            activityTypeOfRecorder,
            activityIsAlarmEnabled  ->

            viewModel.addActivity( activityName,
                activityHour,
                activityMinute,
                activityDaysOfWeek,
                activityTypeOfRecorder ,
                activityIsAlarmEnabled)
        })
        JustTheList(
            activities,
            {
                    activity: DailyActivityEntity->
                viewModel.deleteActivity(activity)
            }
        )
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldWithCow(
    activityAddPressed: (
        String,
        Int,
        Int,
        Set<DaysOfWeekEnum>,
        TypeofRecorder,
        Boolean
    ) -> Unit
) {

    var activityNameText by remember {
        mutableStateOf("")
    }

    var activityDaysOfWeek by remember {
        mutableStateOf<Set<DaysOfWeekEnum>>(emptySet())
    }

    var activityTypeOfRecorder: TypeofRecorder  by remember {
        mutableStateOf(TypeofRecorder.NONE)
    }

    var activityIsAlarmEnabled by remember {
        mutableStateOf(false)
    }

    val timePickerState = rememberTimePickerState(
        initialHour = 8,
        initialMinute = 0,
        is24Hour = true
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        OutlinedTextField(
            value = activityNameText,
            onValueChange = { activityNameText = it },
            label = {
                Text("Activity Name")
            }
        )

        Text("Seleccione la hora")

        TimePicker(
            state = timePickerState
        )

        ActivityTypeRadioGroup(
            selectedOption = activityTypeOfRecorder,
            onAlertSelected= { selected: TypeofRecorder ->
                activityTypeOfRecorder = selected
            }
        )

        ActivityWeekDaysCheckboxGroup(
            selectedDays = activityDaysOfWeek ,
        onAlertSelected = {daysSelected: Set<DaysOfWeekEnum> ->
            activityDaysOfWeek = daysSelected
        })

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = activityIsAlarmEnabled,
                onCheckedChange = {
                    activityIsAlarmEnabled = it
                }
            )

            Text("Alarm enabled")
        }

        Button(
            enabled = activityNameText.isNotBlank() && activityDaysOfWeek.isNotEmpty(),
            onClick = {

                activityAddPressed(
                    activityNameText,
                    timePickerState.hour,
                    timePickerState.minute,
                    activityDaysOfWeek,
                    activityTypeOfRecorder,
                    activityIsAlarmEnabled
                )

                activityNameText = ""
                activityDaysOfWeek = setOf<DaysOfWeekEnum>()
                activityTypeOfRecorder = TypeofRecorder.NONE
                timePickerState.minute = 0
                timePickerState.hour = 8

            }
        ) {
            Text("Add")
        }
    }
}

@Composable
fun JustTheList(
    activities: List<DailyActivityEntity>,
    pillDeletePressed: (DailyActivityEntity) -> Unit
) {
    if( activities.isEmpty()  ){
        Text(
            text = "Empty View",

            style = MaterialTheme.typography.headlineSmall
        )
        return
    }

    Column {

        Text(
            text = "Activities (${activities.size})",
            style = MaterialTheme.typography.headlineSmall
        )

        LazyColumn {
            items(activities) { activity : DailyActivityEntity ->
                JustTheItem(activity, pillDeletePressed)
            }
        }
    }
}


@Composable
fun JustTheItem(
    activity: DailyActivityEntity,
    activityDeletePressed: (DailyActivityEntity) -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = activity.name,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Descripción: ${activity.name}")

            Text(
                text = "Hora: %02d:%02d".format(
                    activity.hour,
                    activity.minute
                )
            )

            val optionsDay = mapOf(
                DaysOfWeekEnum.MONDAY to "Lunes",
                DaysOfWeekEnum.TUESDAY to "Martes",
                DaysOfWeekEnum.WEDNESDAY to "Miércoles",
                DaysOfWeekEnum.THURSDAY to "Jueves",
                DaysOfWeekEnum.FRIDAY to "Viernes",
                DaysOfWeekEnum.SATURDAY to "Sábado",
                DaysOfWeekEnum.SUNDAY to "Domingo"
            )

            val daysOfTheActivity = fromMask(activity.daysOfWeek)

            Text(
                text = "Días: ${
                    daysOfTheActivity.joinToString(separator = "-") { aDayInActivity : DaysOfWeekEnum ->
                        val nameOfTheCurrentDay : String = optionsDay[aDayInActivity]?: ""
                        return@joinToString nameOfTheCurrentDay
                    }
                }"
            )

            val optionsRecorder = mapOf(
                TypeofRecorder.NONE to "Ninguna",
                TypeofRecorder.WEIGHT_RECORDER to "Es una alerta para pesarme",
                TypeofRecorder.WATER_RECORDER to "Es una alerta para tomar agua",
                TypeofRecorder.PILL_RECORDER to "Es una alerta para tomar una vitamina",
                TypeofRecorder.NUMBER_TWO_RECORDER to "Es una alerta para ir al baño"
            )

            val alertTypeString =
                optionsRecorder[TypeofRecorder.fromInt(activity.typeOfRecorder)]?: ""


            Text(
                text = "Tipo de alerta: ${alertTypeString}"
            )

            Text(
                text = if (activity.isAlarmEnabled)
                    "Activa"
                else
                    "Inactiva"
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row {
                Button(
                    onClick = {
                        activityDeletePressed(activity)
                    }
                ) {
                    Text("Eliminar")
                }
            }
        }
    }
}
