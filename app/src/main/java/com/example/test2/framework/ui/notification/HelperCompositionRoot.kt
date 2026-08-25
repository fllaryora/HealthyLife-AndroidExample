package com.example.test2.framework.ui.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import androidx.annotation.StringRes
import com.example.test2.data.notification.Notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.graphics.Typeface
import android.media.AudioAttributes
import android.media.AudioAttributes.Builder
import android.media.RingtoneManager
import android.net.Uri
import android.view.LayoutInflater
import androidx.core.app.NotificationCompat
import com.example.test2.R
import com.example.test2.features.notification.domain.SetNextAlarmUseCaseRepository

class HelperCompositionRoot(
    private val mContext: Context
) {

    companion object {
        const val NOTIFICATION_CHANNEL = "alarm manager notification channel"
        //   const val FLAG_CODE : Int = 0
    }
    private val mResources: Resources
        get() = mContext.resources

    fun getString(@StringRes stringId: Int): String {
        return mResources.getString(stringId)
    }

    fun getString(@StringRes stringId: Int, argument: String): String {
        return mResources.getString(stringId, argument)
    }

    fun getTypeface(fileName: String): Typeface {
        return Typeface.createFromAsset( mContext.assets, fileName)
    }

    fun getAlarmManager(): AlarmManager {
        return  mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    private var mNotificationURI: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    private var att = Builder()
        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()
    fun getNotificationManager(): NotificationManager {
        val notificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager
        createNotificationChannel(notificationManager)
        return notificationManager
    }
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel =
            NotificationChannel(
                NOTIFICATION_CHANNEL,
                NOTIFICATION_CHANNEL,
                importance
            ).apply {
                description = NOTIFICATION_CHANNEL
                enableLights(true)
                setShowBadge(true)
                lightColor = Color.RED
                setSound(mNotificationURI, att)
            }
        notificationManager.createNotificationChannel(channel)
    }

    fun makeNotification(
        notificationData: Notification,
        notificationIntent: PendingIntent
    ): android.app.Notification {
        val allowDeleteNotificationBySwipe = true
        //so it is not ongoing
        val isOnGoing = !allowDeleteNotificationBySwipe
        return NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL)
            .setContentTitle(notificationData.notificationTitle)
            .setTicker(notificationData.notificationTicker)
            .setSmallIcon(R.drawable.ic_app_foreground)
            .setOngoing(isOnGoing)
            .setContentIntent(notificationIntent)
            .setSilent(false)
            .setSound(mNotificationURI)
            .setColor(Color.RED)
            .setDefaults(android.app.Notification.DEFAULT_SOUND or
                    android.app.Notification.DEFAULT_LIGHTS)
            .build()
    }

    /**
     * When it is triggered it call off the alarm
     * or start it
     */
    fun createBroadcastPendingIntent(
        parameterClass: Class<*>,
        notificationData: Notification
    ): PendingIntent? {
        val intent = Intent(
            mContext,
            parameterClass
        )
        val bundle = Bundle().apply {
            putSerializable(NOTIFICATION_PARAM, notificationData)
        }
        intent.putExtra(NOTIFICATION_PARAM, bundle)
        return getPendingIntent(notificationData, intent)
    }

    private fun getPendingIntent(
        notificationData: Notification,
        intent: Intent
    ): PendingIntent? {
        return PendingIntent.getBroadcast(
            mContext,
            notificationData.notificationCode, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    /**
     * It is the pending intent for the guy clicking on it (the notification)
     *
     */
    fun createActivityPendingIntent(
        parameterClass: Class<*>,
        notificationData: Notification
    ): PendingIntent {
        val intent = Intent(
            mContext,
            parameterClass
        )
        val bundle = Bundle().apply {
            putSerializable(NOTIFICATION_PARAM, notificationData)
        }
        intent.putExtra(NOTIFICATION_PARAM, bundle)
        return PendingIntent.getActivity(mContext,
            notificationData.notificationCode, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

    }

    private val mAlarmHelper : AlarmHelper
        get() =  AlarmHelper(mContext)
    val mNotificationHelper: NotificationHelper
        get() = NotificationHelper(mContext)

    val mSetNextAlarmUseCaseRepository : SetNextAlarmUseCaseRepository
        get() = SetNextAlarmUseCaseRepository(mAlarmHelper)

    fun getLayoutInflater(): LayoutInflater {
        return mContext.getSystemService(
            Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }
}