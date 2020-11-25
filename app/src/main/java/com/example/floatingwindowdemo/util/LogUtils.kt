package com.example.floatingwindowdemo.util

import android.util.Log
import android.widget.Toast
import com.example.floatingwindowdemo.FloatWindowApplication

class LogUtils {

    private var TAG = "Message"

    fun getLogPrint(message: String) {
        Log.d(TAG,message)
    }

    fun getLogPrint(Tag:String,message:String){
        Log.d(Tag,message)
    }
    fun toastPrint(message: String){
        if(FloatWindowApplication.mContext!=null) {
            Toast.makeText(FloatWindowApplication.mContext, message, Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        var instance = LogUtils()
    }
}