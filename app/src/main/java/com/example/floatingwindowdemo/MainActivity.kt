package com.example.floatingwindowdemo

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.floatingwindowdemo.bluetooth.BluetoothHelper
import com.example.floatingwindowdemo.bluetooth.CallHelper

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var btnOpen: Button = findViewById(R.id.btn_open)
        var btnClose: Button = findViewById(R.id.btn_close)
        var btnCall: Button = findViewById(R.id.btn_call)
        BluetoothHelper.instance.registerReceiver(this)
        btnOpen.setOnClickListener {
            BluetoothHelper.instance.openBluetooth(this)
        }
        btnClose.setOnClickListener {
            BluetoothHelper.instance.closeBluetooth(this)
        }
        btnCall.setOnClickListener {
            CallHelper.instance.preCallPhone(this,"15311956835")
        }
    }

    override fun onDestroy() {
        BluetoothHelper.instance.unregisterReceiver(this)
        super.onDestroy()
    }
}