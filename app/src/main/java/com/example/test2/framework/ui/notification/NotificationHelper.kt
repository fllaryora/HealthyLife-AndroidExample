package com.example.test2.framework.ui.notification

import android.app.NotificationManager
import android.content.Context
import com.example.test2.data.notification.Notification
import com.example.test2.features.notification.ui.NotificationActivity

/**
 * This class is in charge of show notification popup or dismiss it
 * Notification trigger
 */
class NotificationHelper (val mContext: Context) {
    private val mCompositionRoot: HelperCompositionRoot = HelperCompositionRoot(mContext)

    fun displayNotification(notificationData: Notification) {
        val notificationManager : NotificationManager = mCompositionRoot.getNotificationManager()

        val notificationIntent = mCompositionRoot.createActivityPendingIntent(
            NotificationActivity::class.java, notificationData)
        val notification: android.app.Notification = mCompositionRoot.makeNotification(
            notificationData, notificationIntent)

        notificationManager.notify(notificationData.notificationCode, notification)
    }

    fun dismissNotification( notificationCode:Int) {
        val notificationManager = mCompositionRoot.getNotificationManager()
        notificationManager.cancel(notificationCode)
    }

}