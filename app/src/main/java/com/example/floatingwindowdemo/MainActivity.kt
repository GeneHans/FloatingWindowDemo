package com.example.floatingwindowdemo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.floatingwindowdemo.custom.floatview.FloatTextView
import com.example.floatingwindowdemo.custom.floatview.FloatWindow
import com.example.floatingwindowdemo.util.*
import java.io.File


class MainActivity : AppCompatActivity() {

    private var imgQRCode: ImageView? = null
    private var fileName = "testQrCode1.png"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var floatTextView: FloatTextView = findViewById(R.id.float_text1)
        var floatWindow: FloatWindow = findViewById(R.id.float_window1)
        var btnService: Button = findViewById(R.id.btn_service)
        imgQRCode = findViewById(R.id.img_qr_code)
        btnService.setOnClickListener {
            openImageUtils()
        }
        imgQRCode?.setOnClickListener {
            shareQrCode()
        }
        floatTextView.setAttachAble(false)
        floatWindow.setAttachAble(false)
    }

    private fun shareQrCode() {
        ShareUtils.instance.shareImg(FileUtils.instance.getAppDir() + QRCodeBitmapUtils.QR_CODE + File.separator + fileName, this)
    }

    private fun openCamera() {
        if (PermissionUtils.instance.checkPermission(this, Manifest.permission.CAMERA)) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, IntentUtil.instance.requestCameraPermissionCode)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), IntentUtil.instance.requestCameraPermissionCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            IntentUtil.instance.requestCameraPermissionCode ->{
                openCamera()
            }
            else ->{

            }
        }
    }

    private fun openImageUtils() {
        val intent: Intent
        if (Build.VERSION.SDK_INT < 19) {
            intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
        } else {
            intent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        }
        startActivityForResult(intent, IntentUtil.instance.openImageGalleryCode)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                0 -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                        Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show()
                    }
                }
                IntentUtil.instance.requestCameraPermissionCode -> {
                    var extra = data?.extras
                    var photo = extra?.get("data") as Bitmap?
                    if (photo != null) {
                        var path = FileUtils.instance.getAppDir()
                        if (path != null) {
                            ImageUtils.instance.saveBitmapToFile(photo, System.currentTimeMillis().toString() + ".png",
                                    "TestImg", this)
                        }
                        imgQRCode?.setImageBitmap(photo)
                    }
                }
                IntentUtil.instance.openImageGalleryCode -> {
                    var uri = data?.data
                    if (data != null && uri != null) {
                        var imgPath = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                            ImageUtils.instance.handleImageBeforeKitKat(data, this)
                        } else {
                            ImageUtils.instance.handleImageOnKitKat(data, this)
                        }
                        displayImage(imgPath)
                    }
                }
                else -> {

                }
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

    private fun displayImage(imgPath: String?) {
        LogUtils.instance.getLogPrint(imgPath ?: "没有获取到路径")
        if (imgPath != null && FileUtils.instance.isFileExist(imgPath)) {
            var bitmap = BitmapFactory.decodeFile(imgPath)
            imgQRCode?.setImageBitmap(bitmap)
        }
    }

}