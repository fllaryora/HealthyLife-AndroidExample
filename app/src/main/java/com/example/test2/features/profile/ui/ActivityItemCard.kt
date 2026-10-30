package com.example.test2.features.profile.ui

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test2.R
import com.example.test2.data.entities.enums.DaysOfWeekEnum
import com.example.test2.data.entities.enums.TypeofRecorder
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import com.example.test2.ui.theme.BackgroundContent

//private val ScheduleBlueLight = Color(0xFFEAF1FF)

val scheduled_hour_formatter ="%02d:%02d Hs"

fun getDaysOfDailyActivity(dailyActivity: DailyActivityEntity): List<String> {
    val listDays = mutableListOf<String>()
    val dayArray = listOf<DaysOfWeekEnum>(
        DaysOfWeekEnum.MONDAY,
        DaysOfWeekEnum.TUESDAY,
        DaysOfWeekEnum.WEDNESDAY,
        DaysOfWeekEnum.THURSDAY,
        DaysOfWeekEnum.FRIDAY,
        DaysOfWeekEnum.SATURDAY,
        DaysOfWeekEnum.SUNDAY
    )
    val names = listOf<String>("Lunes",
        "Martes", "Miercoles", "Jueves",
        "Viernes", "Sabado", "Domingo")

    dayArray.forEachIndexed { index: Int, dayOfWeek: DaysOfWeekEnum ->
        if(
            dayOfWeek.applyOn(dailyActivity.daysOfWeek)
        ) {
            listDays.add(names[index])
        }

    }
    return listDays
}

fun getSubtitle(dailyActivity: DailyActivityEntity): String {
    val pepe :TypeofRecorder = TypeofRecorder.fromInt(dailyActivity.typeOfRecorder)
    return  when (pepe) {
        TypeofRecorder.NONE -> "Registro de actividad"
        TypeofRecorder.WEIGHT_RECORDER ->"Registro de peso"
        TypeofRecorder.WATER_RECORDER->"Registro de Toma de agua"
        TypeofRecorder.PILL_RECORDER->"Toma de vitaminas"
        TypeofRecorder.NUMBER_TWO_RECORDER->"Visitas al baño"

    }
}

@Composable
fun ActivityItemCard(
    data: DailyActivityEntity,
    onSettingsClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val days: List<String> = getDaysOfDailyActivity( data)
    val subtitle: String = getSubtitle(data)

    val borderColor: Color = colorResource(id = R.color.colorPrimary)
    val bgColor: Color = colorResource(id = R.color.colorCard)
    val grayColor: Color = colorResource(id = R.color.colorGray)

    Card(
        modifier = modifier.fillMaxWidth().padding(start = 15.dp, end = 15.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Header: icon, time, title, subtitle and actions.
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Schedule icon.
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .background(
                            color = bgColor,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_account_box),
                        contentDescription = null,
                        tint = borderColor,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Text takes the available space.
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = String.format(scheduled_hour_formatter, data.hour, data.minute),
                        color = grayColor,
                        fontSize = 16.sp
                    )

                    Text(
                        text =data.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = grayColor
                    )
                }

                // Secondary actions.
                Row (
                    modifier = Modifier.align(Alignment.Top),
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier
                            .background(
                                color = bgColor,
                                shape = CircleShape
                            ).size(36.dp),
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(18.dp),
                            painter = painterResource(R.drawable.ic_gear),
                            contentDescription = "Configurar",
                            tint = borderColor
                        )
                    }

                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier
                            .background(
                                color = bgColor,
                                shape = CircleShape
                            ).size(36.dp),
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(18.dp),
                            painter = painterResource(R.drawable.ic_bin),
                            contentDescription = "Eliminar",
                            tint = borderColor
                        )
                    }
                }
            }

            // Weekdays: four on the first row, three on the second.
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    days.take(4).forEach { day ->
                        ScheduleDayChip(
                            day = day,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    days.drop(4).take(3).forEach { day ->
                        ScheduleDayChip(
                            day = day,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                thickness = 1.dp
            )

            // Primary action, aligned to the right.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                FloatingActionButton(
                    onClick = onContinueClick,
                    modifier = Modifier.size(52.dp),
                    shape = CircleShape,
                    containerColor = borderColor,
                    contentColor = Color.White
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_right),
                        contentDescription = "Continuar",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ScheduleDayChip(
    day: String,
    modifier: Modifier = Modifier
) {
    val bgColor: Color = colorResource(id = R.color.colorBlueFaded)
    val blueColor: Color = colorResource(id = R.color.colorBlue)
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = bgColor
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 2.dp,
                    vertical = 10.dp
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day,
                fontSize = 12.sp,
                color = blueColor,
                maxLines = 1
            )
        }
    }
}


@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp, height=891dp",
    uiMode = Configuration.UI_MODE_NIGHT_YES or
            Configuration.UI_MODE_TYPE_NORMAL,
    name = "PreviewActivityCard"
)
@Composable
fun PreviewActCard( ) {

    val currentData = DailyActivityEntity(
        0L, "Actividad fisica", 18, 0, daysOfWeek = DaysOfWeekEnum.ALL.value,
        TypeofRecorder.NONE.value
    )

    BackgroundContent {
        Column( modifier = Modifier.safeDrawingPadding().fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.0.dp)
        ) {
            ActivityItemCard(
                data = currentData,
                onSettingsClick= {},
            onDeleteClick = {},
            onContinueClick = {})
        }

    }
}