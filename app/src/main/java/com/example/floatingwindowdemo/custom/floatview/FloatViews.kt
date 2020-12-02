package com.example.floatingwindowdemo.custom.floatview

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.BounceInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.example.floatingwindowdemo.FloatWindowApplication
import com.example.floatingwindowdemo.R
import com.example.floatingwindowdemo.util.LogUtils


class FloatViews : LinearLayout, View.OnClickListener {
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

    //是否需要依附边缘
    private var needAttach = false

    init {
        View.inflate(context, R.layout.float_views_layout, this)
        image = findViewById(R.id.img_float_window)
        text = findViewById(R.id.text_float_window)
        setOnClickListener(this)
        //减去虚拟按键的高度
//        screenHeight -= DeviceUtils.instance.getVirtualBarHeight(context)
    }

    //设置是否可以依附
    fun setAttachAble(attach: Boolean) {
        needAttach = attach
    }

    override fun onClick(v: View?) {
        LogUtils.instance.getLogPrint("点击了可拖动控件" + v?.context?.packageName)
        FloatWindowApplication.startTargetActivity()
    }

    /**
     * 依附左右动画
     * @param left:是由依附左面
     * @param duration:动画持续时长
     */
    public fun attachWindowXLine(left: Boolean, duration: Long = 500) {
        var animate = animate()
                .setInterpolator(BounceInterpolator())
                .setDuration(duration)
        if (left)
            animate.x(0F).start()
        else
            animate.x((screenWidth - width).toFloat()).start()
    }

    /**
     * 依附上下动画
     * @param top:是否依附顶部
     * @param duration:动画持续时长
     */
    public fun attachWindowYLine(top: Boolean, duration: Long) {
        var animate = animate()
                .setInterpolator(BounceInterpolator())
                .setDuration(duration)
        if (top)
            animate.y(0F).start()
        else
            animate.y((screenHeight - height).toFloat()).start()
    }

    /**
     * 双向依附，判断时根据靠近边缘的距离来计算的，距离哪个边缘比较近就依附于那一条边缘
     * @param centerX:X轴的中心长度
     * @param centerY:Y轴的中心长度
     */
    private fun attachByToLine(centerX: Int, centerY: Int) {
        if (lastX < centerX) {
            if (lastY < centerY) {
                if (lastX < lastY) {
                    //贴近左边
                    attachWindowXLine(true, 500)
                } else {
                    //贴近上边
                    attachWindowYLine(true, 500)
                }
            } else {
                if (lastX < screenHeight - lastY) {
                    //贴近左面
                    attachWindowXLine(true, 500)
                } else {
                    //贴近下面
                    attachWindowYLine(false, 500)
                }
            }
        } else {
            if (lastY < centerY) {
                if (lastX < lastY) {
                    //贴近右面
                    attachWindowXLine(false, 500)
                } else {
                    //贴近上边
                    attachWindowYLine(true, 500)
                }
            } else {
                if (lastX < screenHeight - lastY) {
                    //贴近右面
                    attachWindowXLine(false, 500)
                } else {
                    //贴近下面
                    attachWindowYLine(false, 500)
                }
            }
        }
    }
}