package com.example.test2.features.profile.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test2.ui.theme.BackgroundContent
import com.example.test2.R
import com.example.test2.ui.theme.NavigationCard
import com.example.test2.ui.theme.NavigationCardData

@Composable
fun ScheduleScreen(

) {
    val borderColor: Color = colorResource(id = R.color.colorPrimary)

    val bgColor: Color = colorResource(id = R.color.colorCard)

    val titleList = listOf<String>(
        "Pesaje",
    )

    val subTitleList = listOf<String>(
        "Registro de peso",
    )


    val painterList = listOf<Painter>(
        painterResource(id = R.drawable.ic_menu_weighing),
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


Column (modifier = Modifier.fillMaxSize()) {
    LazyColumn (
        modifier = Modifier.fillMaxWidth().weight(0.8f),
        verticalArrangement = Arrangement.spacedBy(7.0.dp,  alignment = Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {

        // 3. Render the dynamic list
        itemsIndexed(
            items = cardDataList,
            // Pro-Tip: Explicit keys prevent unnecessary recompositions during structural list changes
            key = { index, _ -> index }
        ) { index, currentData ->
            NavigationCard(onCardClick = { }, data = currentData,)
        }
    }
    // el box permite superponer elementos
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalDivider(
            color = Color.LightGray,
            thickness = 1.dp,
            modifier = Modifier.align(Alignment.Center)
        )
        FloatingActionButton( onClick = {},
            modifier = Modifier.align(Alignment.CenterEnd).padding(end=20.dp).size(50.dp)) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(R.drawable.ic_arrow_right), // Crisp standard arrow
                    contentDescription = "Navigate Next",
                    tint = borderColor
                )

        }
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
            ScheduleScreen(

            )
        }
    }

}

