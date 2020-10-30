package com.example.floatingwindowdemo.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.floatingwindowdemo.utils.LogUtils

class BluetoothMonitorReceiver : BroadcastReceiver() {
    private var TAG = "bluetooth"
    override fun onReceive(context: Context?, intent: Intent?) {
        var action = intent?.action ?: return
        when (action) {
            BluetoothAdapter.ACTION_STATE_CHANGED -> {
                var state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)
                when (state) {
                    BluetoothAdapter.STATE_ON -> {
                        LogUtils.instance.getLogPrint(TAG, "蓝牙已经打开")
                    }
                    BluetoothAdapter.STATE_TURNING_ON -> {
                        LogUtils.instance.getLogPrint(TAG, "蓝牙正在打开")

                    }
                    BluetoothAdapter.STATE_OFF -> {
                        LogUtils.instance.getLogPrint(TAG, "蓝牙已经关闭")
                    }
                    BluetoothAdapter.STATE_TURNING_OFF -> {
                        LogUtils.instance.getLogPrint(TAG, "蓝牙正在关闭")
                    }
                }
            }
            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                LogUtils.instance.getLogPrint(TAG, "蓝牙设备已断开连接")
            }
            BluetoothDevice.ACTION_ACL_CONNECTED -> {
                LogUtils.instance.getLogPrint(TAG, "蓝牙设备已连接")
            }
        }
    }
}