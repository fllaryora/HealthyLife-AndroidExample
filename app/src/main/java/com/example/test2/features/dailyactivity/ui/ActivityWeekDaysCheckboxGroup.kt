package com.example.test2.features.dailyactivity.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.test2.data.entities.enums.DaysOfWeekEnum

@Composable
fun ActivityWeekDaysCheckboxGroup(
    selectedDays: Set<DaysOfWeekEnum> ,
    onAlertSelected: (Set<DaysOfWeekEnum>) -> Unit
) {

    val options = listOf(
        DaysOfWeekEnum.MONDAY to "Lunes",
        DaysOfWeekEnum.TUESDAY to "Martes",
        DaysOfWeekEnum.WEDNESDAY to "Miércoles",
        DaysOfWeekEnum.THURSDAY to "Jueves",
        DaysOfWeekEnum.FRIDAY to "Viernes",
        DaysOfWeekEnum.SATURDAY to "Sábado",
        DaysOfWeekEnum.SUNDAY to "Domingo"
    )

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Por favor, seleccione los dias en los que tendra lugar la actividad"
        )

        options.forEach { (day, label) ->

            val checked = selectedDays.contains(day)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = checked,
                        onClick = {

                            val newMask : Set<DaysOfWeekEnum> =
                                if (checked) {
                                    selectedDays - day
                                } else {
                                    selectedDays + day
                                }
                            onAlertSelected(newMask)
                        },
                        role = Role.Checkbox
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = null
                )

                Text(
                    text = label,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}