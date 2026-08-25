package com.example.test2.framework.ui.notification
import android.content.BroadcastReceiver
import android.content.Context

abstract class BaseBroadcast : BroadcastReceiver() {
    private var mCompositionRoot: HelperCompositionRoot? = null

    protected fun getCompositionRoot(context: Context): HelperCompositionRoot {
        if (mCompositionRoot == null) {
            mCompositionRoot = HelperCompositionRoot(context)
        }
        return mCompositionRoot!!
    }

}