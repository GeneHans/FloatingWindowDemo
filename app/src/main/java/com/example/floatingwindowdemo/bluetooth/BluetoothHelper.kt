package com.example.floatingwindowdemo.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.IntentFilter

class BluetoothHelper {
    private var receiver: BluetoothMonitorReceiver? = null
    private var TAG = "bluetooth"

    //注册方法
    fun registerReceiver(context: Context) {
        if (receiver == null) {
            receiver = BluetoothMonitorReceiver()
        }
        var intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        context.registerReceiver(receiver, intentFilter)
    }

    //取消注册
    fun unregisterReceiver(context: Context) {
        if (receiver == null)
            return
        context.unregisterReceiver(receiver)
    }

    //打开蓝牙
    fun openBluetooth(context: Context) {
        var bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothAdapter.enable()
    }

    //关闭蓝牙
    fun closeBluetooth(context: Context) {
        var bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothAdapter.disable()
    }

    companion object {
        val instance = BluetoothHelper()
    }
}