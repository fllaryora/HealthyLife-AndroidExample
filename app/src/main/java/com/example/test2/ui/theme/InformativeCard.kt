package com.example.test2.ui.theme

import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test2.R

@Composable
fun InformativeCard(
    onCardClick: () -> Unit,
    borderColor: Color,
    contentChild: @Composable () -> Unit,
) {
    //val borderColor = colorResource(id = R.color.colorBlueStrong)
    val strokeWidthDp = 8.dp
    val cardShape = RoundedCornerShape(12.dp) // Define the shape once
    Card (
        colors = CardDefaults.cardColors(containerColor = Color.LightGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier.fillMaxWidth().padding(15.dp).clip(cardShape).drawWithContent {
            // 1. Draw the native Card background color and shadows first
            drawContent()

            // 2. Overlay the accent color line directly onto the card's background
            val strokeWidthPx = strokeWidthDp.toPx()
            drawLine(
                color = borderColor,
                start = androidx.compose.ui.geometry.Offset(x = strokeWidthPx / 2, y = 0f),
                end = androidx.compose.ui.geometry.Offset(x = strokeWidthPx / 2, y = size.height),
                strokeWidth = strokeWidthPx
            )
        },
        shape = cardShape,
        onClick = {
            onCardClick()
        },
    ) {
        contentChild()
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
fun PreviewCard( ) {
    val borderColor: Color = colorResource(id = R.color.colorBlueStrong)
    BackgroundContent {
        Column( modifier = Modifier.safeDrawingPadding().fillMaxSize()) {
            InformativeCard(onCardClick = {}, borderColor = borderColor ){
            Column(
                modifier = Modifier.padding(15.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Por favor, haga click aqui para navegar desde esta vista a la sección de programación de actividades",
                    color = colorResource(R.color.colorBlueStrong),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.W500,
                    fontSize = 20.sp
                )
            }
        }

        }
    }

}