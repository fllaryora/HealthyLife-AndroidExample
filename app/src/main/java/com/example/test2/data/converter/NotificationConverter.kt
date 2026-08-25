package com.example.test2.data.converter

import com.example.test2.data.entities.enums.TypeofRecorder
import com.example.test2.data.notification.Notification
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import java.util.Calendar

object NotificationConverter {
    fun convertActivityToNotification(title :String,
                                      activity: DailyActivityEntity,
                                      dayOfYear:Int): Notification {
        return Notification(notificationTitle = title,
            notificationTicker= activity.name,
            activityId = activity.id,
            type = TypeofRecorder.fromInt(activity.typeOfRecorder),
            notificationCode= activity.name.hashCode(),
            dayOfYear = dayOfYear,
            hour = activity.hour, minute = activity.minute)
    }

    fun convertInputTimerToMilliseconds(activity: DailyActivityEntity): Long {
        val cal: Calendar = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, activity.hour)
        cal.set(Calendar.MINUTE, activity.minute)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        if (System.currentTimeMillis() > cal.timeInMillis) {
            cal.add(Calendar.DATE, 1)
        }
        return cal.timeInMillis
    }
}