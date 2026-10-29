package com.example.test2.ui.theme

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.test2.R

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp",
    uiMode = Configuration.UI_MODE_NIGHT_YES or
            Configuration.UI_MODE_TYPE_NORMAL,
    name = "fondo"
)
@Composable
fun BackgroundContent(
    content: @Composable () -> Unit = { Text("Hola") }
) {
    val white = Color.White
    val redLight = colorResource(R.color.colorRedLightBG)
    val primary = colorResource(R.color.colorPrimaryBG)
    val redLighter = colorResource(R.color.colorRedLighterBG)
    val redDarker = colorResource(R.color.colorRedDarkerBG)

    val painter = remember(white, redLight, primary, redLighter, redDarker) {
        StripedBackgroundPainter(
            white = white,
            redLight = redLight,
            primary = primary,
            redLighter = redLighter,
            redDarker = redDarker
        )
    }

    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        content()
    }
}