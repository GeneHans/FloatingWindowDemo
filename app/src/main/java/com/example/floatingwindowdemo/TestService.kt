package com.example.floatingwindowdemo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import com.example.floatingwindowdemo.custom.floatview.FloatTextView
import com.example.floatingwindowdemo.custom.floatview.FloatWindow
import com.example.floatingwindowdemo.util.LogUtils

class TestService : Service() {
    private var windowManager: WindowManager? = null
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStart(intent: Intent?, startId: Int) {
        LogUtils.instance.getLogPrint("Service启动")
        super.onStart(intent, startId)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        setForegroundService()
        showFloatingWindow()
        LogUtils.instance.getLogPrint("Service正在运行")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        LogUtils.instance.getLogPrint("Service销毁")
        super.onDestroy()
    }

    private fun showFloatingWindow() {
//        MainActivity.isStart = true
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            LogUtils.instance.getLogPrint(Settings.canDrawOverlays(this).toString())
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
            // 获取WindowManager服务
            if(windowManager == null)
                windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            // 设置LayoutParam
            var layoutParams = WindowManager.LayoutParams()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE
            }
            layoutParams.format = PixelFormat.RGBA_8888
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            //宽高自适应
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            //显示的位置
            layoutParams.x = 300
            layoutParams.y = 300

            // 新建悬浮窗控件
            var view = FloatTextView(this)
            // 将悬浮窗控件添加到WindowManager
            windowManager?.addView(view, layoutParams)
        }
        else{
            LogUtils.instance.getLogPrint("未打开权限")
        }
    }

    private fun addView(context: Context) {
        if (windowManager == null) {
            windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        }
        var view: FloatWindow? = null
        var layoutParam = WindowManager.LayoutParams()
        //设置宽和高
        layoutParam.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParam.width = WindowManager.LayoutParams.WRAP_CONTENT
        //设置初始位置在左上角
        layoutParam.format = PixelFormat.TRANSPARENT
        layoutParam.gravity = Gravity.START or Gravity.TOP
        layoutParam.verticalMargin = 0.2f
        // FLAG_LAYOUT_IN_SCREEN：将window放置在整个屏幕之内,无视其他的装饰(比如状态栏)； FLAG_NOT_TOUCH_MODAL：不阻塞事件传递到后面的窗口
        layoutParam.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        //设置悬浮窗属性
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParam.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            // 设置窗体显示类型(TYPE_TOAST:与toast一个级别)
            layoutParam.type = WindowManager.LayoutParams.TYPE_TOAST
        }
        if (view == null)
            view = FloatWindow(context)
        windowManager?.addView(view, layoutParam)
    }

    /**
     * 将service设置为前台服务
     */
    private fun setForegroundService() {
        val channelId = "测试应用ID"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            var notificationChannel = manager.getNotificationChannel(channelId)
            if (notificationChannel == null) {
                notificationChannel = NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_HIGH)
                notificationChannel.vibrationPattern = LongArray(0)
                notificationChannel.setSound(null, null)
                manager.createNotificationChannel(notificationChannel)
            }
        }
        val builder = NotificationCompat.Builder(this, channelId)
        builder.setSound(null)
        builder.setVibrate(longArrayOf())
        startForeground(101, builder.build())
    }
}