package com.example.test2.framework.ui.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.test2.data.notification.Notification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**

* Perform steps in order to start an alarm as well notify it
 */
const val NOTIFICATION_PARAM = "mNotification"
class StartAlarmBroadcastReceiver : BaseBroadcast() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if(context != null && intent != null && intent.hasExtra(NOTIFICATION_PARAM)) {
            val mCompositionRoot = getCompositionRoot(context)
            val notificationBundle: Bundle =
                intent.getBundleExtra(NOTIFICATION_PARAM) as Bundle

            if(!notificationBundle.containsKey(NOTIFICATION_PARAM)) {
                Log.i("StartAlarmBroadcastReceiver", "notificationBundle is empty")
                return
            }
            val notificationData: Notification =
                notificationBundle.getSerializable(NOTIFICATION_PARAM) as Notification
            mCompositionRoot.mNotificationHelper.displayNotification(notificationData)
            CoroutineScope(Dispatchers.IO).launch {
                val isCreated : Boolean = mCompositionRoot.mSetNextAlarmUseCaseRepository.setNextAlarmAfterCancel()
                Log.d("StartAlarmBroadcastReceiver", "isCreated: $isCreated")
            }
        }
    }
}