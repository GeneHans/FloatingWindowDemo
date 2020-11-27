package com.example.floatingwindowdemo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.floatingwindowdemo.lib.zxing.activity.CaptureActivity
import com.example.floatingwindowdemo.lib.zxing.util.Constant

class TestTargetActivity : AppCompatActivity(), View.OnClickListener {
    var btnQrCode // 扫码
            : Button? = null
    var tvResult // 结果
            : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_target)
        initView()
    }

    private fun initView() {
        btnQrCode = findViewById(R.id.btn_qrcode)
        btnQrCode?.setOnClickListener(this)
        tvResult = findViewById(R.id.txt_result)
    }

    // 开始扫码
    private fun startQrCode() {
        // 申请相机权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !== PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), Constant.REQ_PERM_CAMERA)
            return
        }
        // 申请文件读写权限（部分朋友遇到相册选图需要读写权限的情况，这里一并写一下）
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Constant.REQ_PERM_EXTERNAL_STORAGE)
            return
        }
        // 二维码扫码
        val intent = Intent(this, CaptureActivity::class.java)
        startActivityForResult(intent, Constant.REQ_QR_CODE)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_qrcode -> startQrCode()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //扫描结果回调
        if (requestCode == Constant.REQ_QR_CODE && resultCode == RESULT_OK) {
            val bundle = data?.extras
            val scanResult = bundle!!.getString(Constant.INTENT_EXTRA_KEY_QR_SCAN)
            //将扫描出的信息显示出来
            tvResult?.text = scanResult
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constant.REQ_PERM_CAMERA ->                 // 摄像头权限申请
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获得授权
                    startQrCode()
                } else {
                    // 被禁止授权
                    Toast.makeText(this, "请至权限中心打开本应用的相机访问权限", Toast.LENGTH_LONG).show()
                }
            Constant.REQ_PERM_EXTERNAL_STORAGE ->                 // 文件读写权限申请
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获得授权
                    startQrCode()
                } else {
                    // 被禁止授权
                    Toast.makeText(this, "请至权限中心打开本应用的文件读写权限", Toast.LENGTH_LONG).show()
                }
        }
    }

}