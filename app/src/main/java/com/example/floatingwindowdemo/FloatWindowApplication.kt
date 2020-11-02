package com.example.floatingwindowdemo

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.example.floatingwindowdemo.custom.floatview.FloatWindowHelper
import com.example.floatingwindowdemo.util.LogUtils

class FloatWindowApplication : Application() {

    private var activityCount = 0
    private var flowGroup: FloatWindowHelper? = null

    override fun onCreate() {
        super.onCreate()
        mContext = this
        if (flowGroup == null)
            flowGroup = FloatWindowHelper()
        showWindow()
        startService()
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStarted(activity: Activity) {
                if (activityCount == 0) {
                    hideWindow()
                }
                ++activityCount
            }

            override fun onActivityDestroyed(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityStopped(activity: Activity) {
                --activityCount
                if (activityCount == 0) {
                    showWindow()
                }
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            }

            override fun onActivityResumed(activity: Activity) {
            }
        })
    }

    private fun startService() {
        Log.d("Message", "启动Service")
        var intent = Intent(applicationContext, TestService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    fun showWindow() {
        flowGroup?.showView(this)
    }

    fun hideWindow() {
        flowGroup?.hideView(this)
    }

    companion object {
        var mContext:Context? = null
        fun startTargetActivity() {
            if (mContext != null) {
                var intent = Intent()
                intent.setClass(mContext!!, TestTargetActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                mContext?.startActivity(intent)
            }
        }
    }
}