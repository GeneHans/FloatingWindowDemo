package com.example.floatingwindowdemo.custom.floatview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import com.example.floatingwindowdemo.utils.LogUtils

class FloatTextView : AppCompatTextView {
    constructor(context: Context) : super(context) {}
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {}
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int)
            : super(context, attributeSet, defStyleAttr) {
    }

    init {

    }

    //记录最后的位置
    private var lastX: Int = 0
    private var lastY: Int = 0

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        var action = event?.action ?: return super.onTouchEvent(event)
        var isLongTouch = event
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.rawX.toInt()
                lastY = event.rawY.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                //偏移距离
                var dx = (event.rawX.toInt()) - lastX
                var dy = (event.rawY.toInt()) - lastY
                var l = left + dx
                var r = right + dx
                var b = bottom + dy
                var t = top + dy
                //利用layout方法重新更新view的位置
                layout(l, t, r, b)
                lastX = event.rawX.toInt()
                lastY = event.rawY.toInt()

            }
            MotionEvent.ACTION_UP -> {
                //此处重新设置LayoutParams,防止当父布局重新刷新时导致控件回归原处
                var lp = LinearLayout.LayoutParams(width, height)
                lp.setMargins(left, top, 0, 0)
                LogUtils.instance.getLogPrint("$left   $top   $right   $bottom")
                layoutParams = lp
            }
        }
        return super.onTouchEvent(event)
    }
}