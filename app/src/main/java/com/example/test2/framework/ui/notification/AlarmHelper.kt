package com.example.test2.framework.ui.notification

import android.app.AlarmManager
import android.content.Context
import com.example.test2.R
import com.example.test2.data.converter.NotificationConverter
import com.example.test2.data.notification.Notification
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity

/**
 * This class is in charge of schedule an alarm or call off it.
 * The alarm trigger the broadcast provider.
 */
class AlarmHelper (val mContext: Context) {
    private val mCompositionRoot: HelperCompositionRoot = HelperCompositionRoot(mContext)

    fun startAlarmManager(activity: DailyActivityEntity, dayOfYear :Int):Boolean {
        val notificationData: Notification =
            convertActivityToNotification(activity, dayOfYear)
        val alarmMgr = mCompositionRoot.getAlarmManager()
        val alarmIntent = mCompositionRoot.createBroadcastPendingIntent(
            StartAlarmBroadcastReceiver::class.java, notificationData) ?: return false

        val timeInMillis: Long = NotificationConverter.convertInputTimerToMilliseconds(activity)
        alarmMgr.set(AlarmManager.RTC_WAKEUP, timeInMillis, alarmIntent)
        return true
    }

    private fun convertActivityToNotification(activity: DailyActivityEntity, dayOfYear: Int): Notification {
        val title :String= mCompositionRoot.getString(R.string.alarm_title_formatter, activity.name)
        return NotificationConverter.convertActivityToNotification(
            title, activity, dayOfYear)
    }
}