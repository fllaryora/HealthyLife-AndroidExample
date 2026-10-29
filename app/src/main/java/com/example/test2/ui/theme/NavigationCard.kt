package com.example.test2.ui.theme

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test2.R

data class NavigationCardData (
    val borderColor: Color,
    val  bgColor: Color,
    val painterNavigation: Painter,
    val textTitle: String,
    val textSubtitle: String
)

@Composable
fun NavigationCard(
    onCardClick: () -> Unit,
    data: NavigationCardData
) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(15.dp),
        contentAlignment = Alignment.Center
    ) {
        val iconCardSize: Dp =  56.dp
        val halfIconCardSize: Dp =  28.dp
        val arrowCardSize: Dp =  38.dp
        val halfArrowCardSize: Dp =  19.dp
        val cardShape = RoundedCornerShape(12.dp) // Define the shape once
        // Card Content
        Card (
            colors = CardDefaults.cardColors(containerColor = data.bgColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.fillMaxWidth().padding(top= 15.dp, bottom = 15.dp,  start = halfIconCardSize , end = halfArrowCardSize ),
            shape = cardShape,
            onClick = {
                onCardClick()
            },
        ) {
            Column(
                modifier = Modifier.padding(top= 15.dp, bottom = 15.dp,  start = halfIconCardSize + 8.dp, end = halfArrowCardSize + 8.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(5.0.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = data.textTitle,
                    color = data.borderColor,
                    modifier = Modifier.align(Alignment.Start),
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.W700,
                    fontSize = 26.sp
                )
                Text(
                    text = data.textSubtitle,
                    color = data.borderColor,
                    modifier = Modifier.align(Alignment.Start),
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.W500,
                    fontSize = 14.sp
                )
            }
        }
        // Card Icon
        Card(
            modifier = Modifier
                .size(width = iconCardSize, height = iconCardSize)
                .align(Alignment.CenterStart),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    painter = data.painterNavigation,
                    contentDescription = "Navigate Next",
                    tint = data.borderColor,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        // Card colled arrow
        Card(
            modifier = Modifier
                .size(width = arrowCardSize, height = arrowCardSize)
                .align(Alignment.CenterEnd),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            shape = RoundedCornerShape(halfArrowCardSize)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_right), // Crisp standard arrow
                    contentDescription = "Navigate Next",
                    tint = data.borderColor
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
    name = "PreviewNav"
)
@Composable
fun PreviewNavCard( ) {
    val borderColor: Color = colorResource(id = R.color.colorPrimary)
    val painterNavigation: Painter = painterResource(id = R.drawable.ic_menu_weighing)
    val bgColor: Color = colorResource(id = R.color.colorCard)
    val textTitle =  "Pesaje"
    val textSubtitle = "Registro de peso"

    val currentData = NavigationCardData(
        borderColor = borderColor,
        painterNavigation = painterNavigation,
        bgColor = bgColor,
        textTitle= textTitle, textSubtitle = textSubtitle
    )
    BackgroundContent {
        Column( modifier = Modifier.safeDrawingPadding().fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.0.dp)
        ) {
            NavigationCard(onCardClick = {}, data = currentData,)
        }

    }

}