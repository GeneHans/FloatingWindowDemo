package com.example.floatingwindowdemo.bluetooth

import android.content.Context
import android.content.Intent
import android.net.Uri

class CallHelper {
    /**
     * 拨打电话（直接拨打电话）
     * @param phoneNum 电话号码
     */
    fun callPhone(context: Context, phoneNum: String) {
        val intent = Intent(Intent.ACTION_CALL)
        val data: Uri = Uri.parse("tel:$phoneNum")
        intent.data = data
        context.startActivity(intent)
    }

    /**
     * 拨打电话（跳转到拨号界面，用户手动点击拨打）
     *
     * @param phoneNum 电话号码
     */
    fun preCallPhone(context: Context, phoneNum: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        val data = Uri.parse("tel:$phoneNum")
        intent.data = data
        context.startActivity(intent)
    }

    companion object {
        val instance = CallHelper()
    }
}