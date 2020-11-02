package com.example.floatingwindowdemo.custom.floatview

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager

class FloatWindowHelper {
    private var windowManager: WindowManager? = null
    private var view: FloatWindow? = null

    private fun addView(context: Context) {
        if (windowManager == null) {
            windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        }
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

    fun showView(context: Context) {
        if (view == null || view?.windowToken == null) {
            addView(context)
        }
        if (view != null)
            view?.visibility = View.VISIBLE
    }

    fun hideView(context: Context) {
        view?.visibility = View.GONE
    }


    companion object {
        val instance = FloatWindowHelper()
    }
}