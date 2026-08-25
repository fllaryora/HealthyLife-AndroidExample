package com.example.test2.framework.ui.notification

import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootBroadcastReceiver : BaseBroadcast() {
    private val TAG: String = javaClass.simpleName
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null &&
            intent?.action == "android.intent.action.BOOT_COMPLETED") {
            //If you declare a broadcast receiver in your manifest,
            // the system launches your app (if the app is not already running)
            // when the broadcast is sent.
            val mCompositionRoot = getCompositionRoot(context)
            CoroutineScope(Dispatchers.IO).launch {
                val isCreated = mCompositionRoot
                    .mSetNextAlarmUseCaseRepository
                    .setNextAlarm()
                Log.d(TAG, "isCreated: $isCreated")
            }
        }
    }



}