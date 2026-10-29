package com.example.test2.features.measures.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test2.ui.theme.BackgroundContent
import com.example.test2.R
import com.example.test2.ui.theme.InformativeCard
import com.example.test2.ui.theme.NavigationCard
import com.example.test2.ui.theme.NavigationCardData

@Composable
fun MeasuresScreen(
    onWeight: () -> Unit,
    onNumberTwo: () -> Unit,
    onPill: () -> Unit,
    onWaterIntake: () -> Unit
) {

    val borderColor: Color = colorResource(id = R.color.colorPrimary)

    val bgColor: Color = colorResource(id = R.color.colorCard)

    val titleList = listOf<String>(
        "Pesaje",
        "Ingesta de agua",
        "Recordatorio para vitaminas",
        "Ir de cuerpo"
    )

    val subTitleList = listOf<String>(
        "Registro de peso",
        "Registro de Ingesta de agua",
        "Recordatorio para vitaminas",
        "Registro de visitas al baño"
    )
    val navigationList = listOf<() -> Unit>(
        onWeight, onWaterIntake, onPill, onNumberTwo
    )

    val painterList = listOf<Painter>(
        painterResource(id = R.drawable.ic_menu_weighing),
        painterResource(id = R.drawable.ic_menu_water_intake),
        painterResource(id = R.drawable.ic_menu_pill_reminder),
        painterResource(id = R.drawable.ic_menu_number_two),
    )

    val cardDataList = mutableListOf<NavigationCardData>()

    titleList.forEachIndexed { index, textTitle ->
        cardDataList.add(
            NavigationCardData(
                borderColor = borderColor,
                painterNavigation = painterList[index],
                bgColor = bgColor,
                textTitle= textTitle, textSubtitle = subTitleList[index]
            )
        )
    }


    LazyColumn (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(7.0.dp,  alignment = Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {

        // 3. Render the dynamic list
        itemsIndexed(
            items = cardDataList,
            // Pro-Tip: Explicit keys prevent unnecessary recompositions during structural list changes
            key = { index, _ -> index }
        ) { index, currentData ->
            NavigationCard(onCardClick = { navigationList[index]() }, data = currentData,)
        }


    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp",
    uiMode = Configuration.UI_MODE_NIGHT_YES or
            Configuration.UI_MODE_TYPE_NORMAL,
    name = "Preview"
)
@Composable
fun PreviewHomeScreen( ) {
    BackgroundContent {
        Column( modifier = Modifier.safeDrawingPadding().fillMaxSize()) {
            MeasuresScreen(
                onWeight = {
                },
                onNumberTwo = {
                },
                onPill = {
                },
                onWaterIntake = {
                }
            )
        }
    }

}

