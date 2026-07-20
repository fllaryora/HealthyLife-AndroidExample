package com.example.test2.features.dailyactivity.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.test2.data.entities.enums.TypeofRecorder

@Composable
fun ActivityTypeRadioGroup(
    selectedOption: TypeofRecorder,
    onAlertSelected: (TypeofRecorder) -> Unit
) {

    val options = listOf(
        TypeofRecorder.NONE to "Ninguna",
        TypeofRecorder.WEIGHT_RECORDER to "Es una alerta para pesarme",
        TypeofRecorder.WATER_RECORDER to "Es una alerta para tomar agua",
        TypeofRecorder.PILL_RECORDER to "Es una alerta para tomar una vitamina",
        TypeofRecorder.NUMBER_TWO_RECORDER to "Es una alerta para ir al baño"
    )


    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "¿Qué tipo de alerta es?"
        )

        options.forEach { (typeOfRecorder, label) ->
            val isSelected = typeOfRecorder == selectedOption
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = isSelected,
                        onClick = {
                            onAlertSelected (typeOfRecorder)
                        },
                        role = Role.RadioButton
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = null,
                )

                Text(
                    text = label,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}