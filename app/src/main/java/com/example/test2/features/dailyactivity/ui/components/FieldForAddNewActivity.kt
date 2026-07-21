package com.example.test2.features.dailyactivity.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.test2.data.entities.enums.DaysOfWeekEnum
import com.example.test2.data.entities.enums.TypeofRecorder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldForAddNewActivity(
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
            onAlertSelected = { selected: TypeofRecorder ->
                activityTypeOfRecorder = selected
            }
        )

        ActivityWeekDaysCheckboxGroup(
            selectedDays = activityDaysOfWeek,
            onAlertSelected = { daysSelected: Set<DaysOfWeekEnum> ->
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
            Text("Add Activity")
        }
    }
}