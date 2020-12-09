package com.example.floatingwindowdemo.util

import android.content.Context
import android.content.pm.PackageManager

class PermissionUtils {

    fun checkPermission(context: Context, permissionName: String): Boolean {
        var packageManager = context.packageManager
        if (packageManager.checkPermission(permissionName, context.packageName) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
        return true
    }

    fun checkPermissions(context: Context, permissionList: Array<String>): Boolean {
        for (permissionName in permissionList) {
            if (!checkPermission(context, permissionName)) {
                return false
            }
        }
        return true
    }

    companion object {
        val instance: PermissionUtils by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { PermissionUtils() }
    }
}