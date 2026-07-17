package com.example.test2.data.converter

import com.example.test2.data.entities.enums.TypeofRecorder
import com.example.test2.features.dailyactivity.data.local.DailyActivityEntity
import com.example.test2.data.notification.Notification

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
}