package com.example.floatingwindowdemo.util

class IntentUtil {

    val openImageGalleryCode = 2
    val requestCameraPermissionCode = 1

    companion object {
        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { IntentUtil() }
    }
}