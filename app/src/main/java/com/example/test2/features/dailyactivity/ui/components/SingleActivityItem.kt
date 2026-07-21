package com.example.test2.features.dailyactivity.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.test2.data.entities.enums.DaysOfWeekEnum
import com.example.test2.data.entities.enums.TypeofRecorder
import com.example.test2.data.entities.enums.fromMask
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity


@Composable
fun SingleActivityItem(
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
                    Text("Eliminar Actividad")
                }
            }
        }
    }
}
