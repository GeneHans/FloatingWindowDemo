package com.example.floatingwindowdemo.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import androidx.core.app.ActivityCompat.startActivityForResult
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class QRCodeBitmapUtils {

    /**
     * 生成简单二维码
     *
     * @param content                字符串内容
     * @param width                  二维码宽度
     * @param height                 二维码高度
     * @param characterSet          编码方式（一般使用UTF-8）
     * @param errorCorrectionLevel 容错率 L：7% M：15% Q：25% H：35%
     * @param margin                 空白边距（二维码与边框的空白区域）
     * @param colorBlack            黑色色块
     * @param colorWhite            白色色块
     * @return BitMap
     */
    fun createQRCodeBitmap(
            content: String?, width: Int, height: Int,
            characterSet: String?, errorCorrectionLevel: String?,
            margin: String?, colorBlack: Int, colorWhite: Int
    ): Bitmap? {
        // 字符串内容判空
        if (TextUtils.isEmpty(content)) {
            return null
        }
        // 宽和高>=0
        if (width < 0 || height < 0) {
            return null
        }
        return try {
            /** 1.设置二维码相关配置  */
            val hints =
                    Hashtable<EncodeHintType, String?>()
            // 字符转码格式设置
            if (!TextUtils.isEmpty(characterSet)) {
                hints[EncodeHintType.CHARACTER_SET] = characterSet
            }
            // 容错率设置
            if (!TextUtils.isEmpty(errorCorrectionLevel)) {
                hints[EncodeHintType.ERROR_CORRECTION] = errorCorrectionLevel
            }
            // 空白边距设置
            if (!TextUtils.isEmpty(margin)) {
                hints[EncodeHintType.MARGIN] = margin
            }
            /** 2.将配置参数传入到QRCodeWriter的encode方法生成BitMatrix(位矩阵)对象  */
            val bitMatrix =
                    QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints)

            /** 3.创建像素数组,并根据BitMatrix(位矩阵)对象为数组元素赋颜色值  */
            val pixels = IntArray(width * height)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    //bitMatrix.get(x,y)方法返回true是黑色色块，false是白色色块
                    if (bitMatrix[x, y]) {
                        pixels[y * width + x] = colorBlack //黑色色块像素设置
                    } else {
                        pixels[y * width + x] = colorWhite // 白色色块像素设置
                    }
                }
            }
            /** 4.创建Bitmap对象,根据像素数组设置Bitmap每个像素点的颜色值,并返回Bitmap对象  */
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
        //调用示例代码
//        imageView.setImageBitmap(QRCodeBitmap.newInstance().createQRCodeBitmap("test",80,80,
//                "UTF-8","L","1", Color.BLACK,Color.WHITE));
    }


    /**
     * 保存图片到指定路径
     *
     * @param bitmap   要保存的图片
     * @param fileName 自定义图片名称
     * @param context
     * @return
     */
    fun saveBitmapToFile(bitmap: Bitmap?, fileName: String,context: Context?): Boolean {
        // 保存图片至指定路径
        if(bitmap == null)
            return false
        val storePath = FileUtils.instance.getAppDir() + QR_CODE+File.separator
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

    /***
     * 为二维码添加logo
     *
     * @param srcBitmap 二维码图片
     * @param logoBitmap logo图片
     * @param percent logo比例
     * @return 生成的最终的图片
     */
    fun addLogo(srcBitmap: Bitmap?, logoBitmap: Bitmap?, percent: Float): Bitmap? {
        //判断参数是否正确
        if (srcBitmap == null)
            return null
        if (logoBitmap == null)
            return srcBitmap
        //输入logo图片比例错误自动纠正为默认的0.2f
        var logoPercent = percent
        if (percent < 0 || percent > 1)
            logoPercent = 0.2f

        //分别获取bitmap图片的大小
        var sHeight = srcBitmap.height
        var sWidth = srcBitmap.width
        var lHeight = logoBitmap.height
        var lWidth = logoBitmap.width

        //获取缩放比例
        var scareWidth = sHeight * logoPercent / lWidth
        var scareHeight = sWidth * logoPercent / lHeight

        //使用canvas重新绘制bitmap
        var bitmap = Bitmap.createBitmap(sWidth, sHeight, Bitmap.Config.ARGB_8888)
        var canvas = Canvas(bitmap)
        canvas.drawBitmap(srcBitmap, 0f, 0f, null)
        canvas.scale(
                scareWidth,
                scareHeight,
                (sWidth / 2).toFloat(),
                (sHeight / 2).toFloat()
        )   //设置缩放中心基点
        canvas.drawBitmap(
                logoBitmap,
                (sWidth / 2 - lWidth / 2).toFloat(),
                (sHeight / 2 - lHeight / 2).toFloat(),
                null
        )
        return bitmap
    }

    /**
     * 生成带有图片效果的二维码
     *
     * @param content                字符串内容
     * @param width                  二维码宽度
     * @param height                 二维码高度
     * @param characterSet          编码方式（一般使用UTF-8）
     * @param errorCorrectionLevel 容错率 L：7% M：15% Q：25% H：35%
     * @param margin                 空白边距（二维码与边框的空白区域）
     * @param colorBlack            黑色色块
     * @param colorWhite            白色色块
     * @param bitmapOther           图片效果的图片
     * @return BitMap
     */
    fun createQRCodeWithBitmap(
            content: String?, width: Int, height: Int,
            characterSet: String?, errorCorrectionLevel: String?,
            margin: String?, colorBlack: Int, colorWhite: Int, bitmapOther: Bitmap?
    ): Bitmap? {
        // 字符串内容判空
        if (TextUtils.isEmpty(content)) {
            return null
        }
        // 宽和高>=0
        if (width < 0 || height < 0) {
            return null
        }
        return try {
            /** 1.设置二维码相关配置  */
            val hints =
                    Hashtable<EncodeHintType, String?>()
            // 字符转码格式设置
            if (!TextUtils.isEmpty(characterSet)) {
                hints[EncodeHintType.CHARACTER_SET] = characterSet
            }
            // 容错率设置
            if (!TextUtils.isEmpty(errorCorrectionLevel)) {
                hints[EncodeHintType.ERROR_CORRECTION] = errorCorrectionLevel
            }
            // 空白边距设置
            if (!TextUtils.isEmpty(margin)) {
                hints[EncodeHintType.MARGIN] = margin
            }
            /** 2.将配置参数传入到QRCodeWriter的encode方法生成BitMatrix(位矩阵)对象  */
            val bitMatrix =
                    QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints)
            var bitmapExtra: Bitmap? = null
            if (bitmapOther != null) {
                bitmapExtra = Bitmap.createScaledBitmap(bitmapOther, width, height, false)
            }
            /** 3.创建像素数组,并根据BitMatrix(位矩阵)对象为数组元素赋颜色值  */
            val pixels = IntArray(width * height)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    if (bitMatrix.get(x, y)) {// 像素块设置
                        if (bitmapExtra != null) {//图片不为null，则将黑色色块换为新位图的像素。
                            pixels[y * width + x] = bitmapExtra.getPixel(x, y);
                        } else {
                            //无图片时默认仍然为输入颜色像素块
                            pixels[y * width + x] = colorBlack;
                        }
                    } else {
                        pixels[y * width + x] = colorWhite;// 白色色块像素设置
                    }
                }
            }
            /** 4.创建Bitmap对象,根据像素数组设置Bitmap每个像素点的颜色值,并返回Bitmap对象  */
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
        //调用示例代码
//        imageView.setImageBitmap(QRCodeBitmap.newInstance().createQRCodeBitmap("test",80,80,
//                "UTF-8","L","1", Color.BLACK,Color.WHITE));
    }

    /**
     * 开启摄像头
     */
    fun openCamera(activity:Activity){
        var imageUri = FileUtils.instance.getAppDir() ?:""
        var intent = Intent("android.media.action.IMAGE_CAPTURE")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(activity,intent,1,null)
    }

    companion object {
        public const val QR_CODE = "QRCode"
        val instance = QRCodeBitmapUtils()
    }
}