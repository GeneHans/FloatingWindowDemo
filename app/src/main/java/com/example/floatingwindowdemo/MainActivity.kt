package com.example.floatingwindowdemo

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.floatingwindowdemo.custom.floatview.FloatTextView
import com.example.floatingwindowdemo.custom.floatview.FloatWindow
import com.example.floatingwindowdemo.util.FileUtils
import com.example.floatingwindowdemo.util.ImageUtils
import com.example.floatingwindowdemo.util.QRCodeBitmapUtils
import java.io.File


class MainActivity : AppCompatActivity() {

    private var isDebug = true
    private var isStart = false
    private var imgQRCode: ImageView? = null
    private var fileName = "testQrCode1.png"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var floatTextView: FloatTextView = findViewById(R.id.float_text1)
        var floatWindow: FloatWindow = findViewById(R.id.float_window1)
        var btnService: Button = findViewById(R.id.btn_service)
        imgQRCode = findViewById(R.id.img_qr_code)
        requestPermission()
//        startService()
        btnService.setOnClickListener {
//            startService()
            createQrCode()
        }
        imgQRCode?.setOnClickListener {
            shareQrCode()
        }
        floatTextView.setAttachAble(false)
        floatWindow.setAttachAble(false)
        if (isDebug) {
//            finish()
        }
    }

    private fun shareQrCode() {
        ImageUtils.instance.shareImg(FileUtils.instance.getAppDir()+QRCodeBitmapUtils.QR_CODE +File.separator+ fileName, this)
    }

    /**
     * 申请全局绘制权限
     */
    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_SHORT)
            val intent = Intent()
            intent.action = Settings.ACTION_MANAGE_OVERLAY_PERMISSION
            intent.data = Uri.parse("package:$packageName")
            startActivityForResult(intent, 0)
        }
    }

    private fun startService() {
        Log.d("Message", "启动Service")
        var intent = Intent(this, TestService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
            finish()
        } else {
            startService(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show()
                if (!isStart) startService(Intent(this@MainActivity, TestService::class.java))
            }
        }
    }

    private fun createQrCode() {
        var bitmap = QRCodeBitmapUtils.instance.createQRCodeBitmap("this is a test", 300, 300,
                "UTF-8", "L", "2", Color.BLACK, Color.WHITE)
        var logoBitmap: Bitmap? = null
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            var vectorDrawable = ContextCompat.getDrawable(this, R.drawable.ic_launcher_background)
            if (vectorDrawable != null) {
                logoBitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth,
                        vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
                var canvas = Canvas(logoBitmap)
                vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
                vectorDrawable.draw(canvas)
            }
        } else {
            logoBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_background);
        }
        if (bitmap != null) {
            var qrCodeBitmap = QRCodeBitmapUtils.instance.addLogo(bitmap, logoBitmap, 0.2f)
            QRCodeBitmapUtils.instance.saveBitmapToFile(qrCodeBitmap, fileName, this)
            imgQRCode?.setImageBitmap(qrCodeBitmap)
        }
    }
}