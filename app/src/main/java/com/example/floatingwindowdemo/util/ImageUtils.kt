package com.example.floatingwindowdemo.util

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ImageUtils {

    /**
     * 保存图片到指定路径
     *
     * @param bitmap   要保存的图片
     * @param fileName 自定义图片名称
     * @param pathDir 自定义的图片文件路径
     * @param context
     * @return
     */
    fun saveBitmapToFile(bitmap: Bitmap?, fileName: String, pathDir: String, context: Context?): Boolean {
        // 保存图片至指定路径
        if (bitmap == null)
            return false
        val storePath = FileUtils.instance.getAppDir() + pathDir + File.separator
        val appDir = File(storePath)
        if (!appDir.exists()) {
            appDir.mkdirs()
        }
        val file = File(appDir, fileName)
        LogUtils.instance.getLogPrint(file.absolutePath)
        try {
            val fos = FileOutputStream(file)
            //通过io流的方式来压缩保存图片(80代表压缩20%)
            val isSuccess = bitmap.compress(Bitmap.CompressFormat.PNG, 80, fos)

            fos.flush()
            fos.close()

            //发送广播通知系统图库刷新数据
            val uri: Uri = Uri.fromFile(file)
            context?.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))

            return isSuccess
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }


    /**
     * 从相册中读取图片,在4.4之后
     * @param data:打开图片选择后返回的intent
     * @param context
     * @return
     */
    fun handleImageOnKitKat(data: Intent, context: Context?): String? {
        var imagePath: String? = null
        val uri = data.data
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri!!.authority) {
                val id = docId.split(":").toTypedArray()[1] // 解析出数字格式的id
                val selection = MediaStore.Images.Media._ID + "=" + id
                LogUtils.instance.getLogPrint("id=$id,selection=$selection")
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, context)
            } else if ("com.android.providers.downloads.documents" == uri.authority) {
                val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(docId))
                imagePath = getImagePath(contentUri, null, context)
            }
        } else if ("content".equals(uri!!.scheme, ignoreCase = true)) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null, context)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.path
        }
        return imagePath
    }

    /**
     * 4.4版本以前，直接获取真实路径
     * @param data
     * @return
     */
    fun handleImageBeforeKitKat(data: Intent, context: Context?): String? {
        val uri = data.data
        return getImagePath(uri, null, context)
    }

    /**
     * 查询图库中是否存在有指定路径的图片
     * @param uri:路径URI
     * @param selection:筛选条件
     * @param context
     * @return
     */
    private fun getImagePath(uri: Uri?, selection: String?, context: Context?): String? {
        var path: String? = null
        // 通过Uri和selection来获取真实的图片路径
        val cursor: Cursor? = context?.contentResolver?.query(uri!!, null, selection, null, null)
        if (cursor != null) {
            LogUtils.instance.getLogPrint("cursor不为null  $selection")
            var i = 0
            while (i < cursor.columnCount) {
                var ss = cursor.getColumnName(i)
                LogUtils.instance.getLogPrint("$i   $ss")
                i++
            }
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                LogUtils.instance.getLogPrint("get path= $path")
            }
            cursor.close()
        }
        return path
    }


    companion object {
        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { ImageUtils() }
    }
}