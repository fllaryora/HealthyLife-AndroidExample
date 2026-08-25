package com.example.test2.features.notification.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.test2.data.notification.Notification
import com.example.test2.framework.ui.notification.NOTIFICATION_PARAM
import com.example.test2.framework.ui.notification.NotificationHelper

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import com.example.test2.data.entities.enums.TypeofRecorder
import com.example.test2.ui.theme.Test2Theme

class NotificationActivity : ComponentActivity() {
    private val mIntent : Intent
        get() = this.intent

    val mNotification: Notification?
        get() = if (mIntent.hasExtra(NOTIFICATION_PARAM))
            (mIntent.getBundleExtra(NOTIFICATION_PARAM) as Bundle)
                .getSerializable(NOTIFICATION_PARAM) as Notification
        else null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Test2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NotificationScreen( mNotification, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
    override fun onStart() {
        super.onStart()
        val mNotificationHelper: NotificationHelper = NotificationHelper(this)
        mNotification?.let{
            mNotificationHelper.dismissNotification(it.notificationCode)
        }
    }
}


@Composable
fun NotificationScreen(
    mNotification: Notification?,
    modifier: Modifier = Modifier
) {
    val description = when (mNotification?.type) {
        TypeofRecorder.NONE -> "Ninguna"
        TypeofRecorder.WEIGHT_RECORDER -> "Es una alerta para pesarme"
        TypeofRecorder.WATER_RECORDER -> "Es una alerta para tomar agua"
        TypeofRecorder.PILL_RECORDER -> "Es una alerta para tomar una vitamina"
        TypeofRecorder.NUMBER_TWO_RECORDER -> "Es una alerta para ir al baño"
        null -> "Sin información"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = mNotification?.notificationTitle ?: "Sin información")
        Text(text = mNotification?.notificationTicker ?: "Sin información")
        Text(text = mNotification?.notificationCode?.toString() ?: "Sin información")
        Text(text = description)

        mNotification?.let {
            Text("Hora: %02d:%02d".format(it.hour, it.minute))
            Text("Día: ${it.dayOfYear}")
            Text("Actividad: ${it.activityId}")
        }
    }
}