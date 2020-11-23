package com.example.floatingwindowdemo.util

import android.os.Environment
import com.example.floatingwindowdemo.FloatWindowApplication
import java.io.File
import java.lang.StringBuilder

class FileUtils {

    //设置一个通用文件夹路径
    private val appDirName = "FloatWindowDemo"

    //判断文件存不存在
    fun isFileExist(filePath: String): Boolean {
        if (filePath == "")
            return false
        return try {
            var file: File = File(filePath)
            file.exists()
        } catch (e: Exception) {
            false
        }
    }

    //获取当前应用的通用文件存储路径
    fun getAppDir(): String? {
        var stringBuilder = StringBuilder()
        var sdCardPath: String? = getSDCardPath() ?: return null
        stringBuilder.append(sdCardPath).append(File.separator).append(appDirName)
            .append(File.separator)
        return stringBuilder.toString()
    }

    //查询文件可不可读
    fun fileCanRead(fileName: String?): Boolean {
        if (fileName == null)
            return false
        var file = File(fileName)
        return isFileExist(fileName) && file.canRead()
    }

    //获取外部存储路径
    fun getSDCardPath(): String? {
        return try {
            return Environment.getExternalStorageDirectory().absolutePath
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        val instance = FileUtils()
    }
}