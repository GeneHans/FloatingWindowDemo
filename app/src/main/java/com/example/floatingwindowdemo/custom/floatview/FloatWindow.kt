package com.example.floatingwindowdemo.custom.floatview

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.example.floatingwindowdemo.DeviceUtils
import com.example.floatingwindowdemo.R


class FloatWindow : LinearLayout {
    constructor(context: Context) : super(context) {}
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {}
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr) {
    }

    private var image: AppCompatImageView? = null
    private var text: TextView? = null
    private var lastX: Int = 0
    private var lastY: Int = 0

    private var displayMetrics: DisplayMetrics = resources.displayMetrics
    private var screenWidth = displayMetrics.widthPixels
    private var screenHeight = displayMetrics.heightPixels

    init {
        View.inflate(context, R.layout.float_window_layout, this)
        image = findViewById(R.id.img_float_window)
        text = findViewById(R.id.text_float_window)
        //减去虚拟按键的高度
        screenHeight -= DeviceUtils.instance.getVirtualBarHeight(context)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        var action = ev?.action ?: return super.onInterceptTouchEvent(ev)
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = ev.rawX.toInt()
                lastY = ev.rawY.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                var dx = ev.rawX.toInt() - lastX
                var dy = ev.rawY.toInt() - lastY
                var l = left + dx
                var r = right + dx
                var t = top + dy
                var b = bottom + dy
                //当滑动出边界时需要重新设置位置
                if (l < 0) {
                    l = 0
                    r = width
                }
                if (t < 0) {
                    t = 0
                    b = height
                }
                if (r > screenWidth) {
                    r = screenWidth
                    l = screenWidth - width
                }
                if (b > screenHeight) {
                    b = screenHeight
                    t = screenHeight - height
                }
                layout(l, t, r, b)
                lastX = ev.rawX.toInt()
                lastY = ev.rawY.toInt()
            }
            MotionEvent.ACTION_UP -> {
                var lp = LinearLayout.LayoutParams(width, height)
                lp.setMargins(left, top, right, bottom)
                layoutParams = lp
            }
        }
        return super.onInterceptTouchEvent(ev)
    }
}