package com.example.floatingwindowdemo.util

import android.content.Context
import android.content.Intent
import android.net.Uri

class ImageUtils {

    /**
     * 分享图片
     * @param filePath:文件路径名称
     * @param context
     */
    fun shareImg(filePath: String, context: Context) {
        val uri: Uri = Uri.parse(filePath)
        var intent = Intent()
        LogUtils.instance.getLogPrint("获取文件路径："+uri.toString())
        intent.action = Intent.ACTION_SEND     //设置Action为分享
        intent.type = "image/*" //设置分享内容的类型，暂设定为图片分享
        intent.putExtra(Intent.EXTRA_STREAM, uri)    //设置分享的地址
//        intent.putExtra(Intent.EXTRA_SUBJECT, "share");//添加分享内容标题
//        intent.putExtra(Intent.EXTRA_TEXT, "share with you:" + "android");//添加分享内容
        intent = Intent.createChooser(intent, "分享")
        context.startActivity(intent)
    }

        companion object {
        val instance = ImageUtils()
    }
}