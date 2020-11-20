package com.example.floatingwindowdemo

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.example.floatingwindowdemo.custom.floatview.FloatTextView
import com.example.floatingwindowdemo.custom.floatview.FloatWindow


class MainActivity : AppCompatActivity() {

    private var isDebug = true
    private var isStart = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var floatTextView: FloatTextView = findViewById(R.id.float_text1)
        var floatWindow: FloatWindow = findViewById(R.id.float_window1)
        var btnService: Button = findViewById(R.id.btn_service)
        requestPermission()
        startService()
        btnService.setOnClickListener {
            startService()
        }
        floatTextView.setAttachAble(false)
        floatWindow.setAttachAble(false)
        if (isDebug) {
//            finish()
        }
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
}