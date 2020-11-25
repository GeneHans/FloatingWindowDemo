package com.example.floatingwindowdemo.util

import android.os.Environment
import android.text.TextUtils
import java.io.File

class FileUtils {

    //设置一个通用文件夹路径
    private val appDirName = "FloatWindowDemo"

    /**
     * 检查文件目录是否存在，不存在则创建目录
     * @param dirPath:文件目录
     * @return
     */
    fun checkFileDirExist(dirPath: String): Boolean {
        try {
            var file = File(dirPath)
            if (file.exists()) {
                return true
            } else {
                file.mkdir()
                return false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    /**
     * 判断文件存不存在
     * @param filePath:带文件路径的文件名
     * @return true存在，false不存在
     */
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

    /**
     * 获取当前项目文件夹路径
     */
    fun getAppDir(): String? {
        var stringBuilder = StringBuilder()
        var sdCardPath: String? = getSDCardPath() ?: return null
        stringBuilder.append(sdCardPath).append(File.separator).append(appDirName)
                .append(File.separator)
        return stringBuilder.toString()
    }

    /**
     * 查询文件可不可读
     * @param fileName:带路径的文件名称
     * @return :需要判断文件存不存在
     */
    fun fileCanRead(fileName: String?): Boolean {
        if (fileName == null)
            return false
        var file = File(fileName)
        return isFileExist(fileName) && file.canRead()
    }

    /**
     * 获取文件外部存储的路径
     * @return
     */
    fun getSDCardPath(): String? {
        return try {
            return Environment.getExternalStorageDirectory().absolutePath
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 删除文件夹下所有文件及文件夹
     *
     * @param dirFile
     * @return
     */
    fun deleteDirs(dirFile: File?): Boolean {
        return deleteDirs("", dirFile)
    }

    /**
     * 删除文件夹下所有文件及文件夹，保留根目录
     */
    fun deleteDirs(rootDir: String, dirFile: File?): Boolean {
        return deleteDirs(rootDir, dirFile, "")
    }


    /**
     * 删除文件夹下所有文件及文件夹，保留根目录
     *
     * @param rootDir
     * @param dirFile
     * @return
     */
    fun deleteDirs(rootDir: String, dirFile: File?, exceptPath: String?): Boolean {
        return try {
            if (dirFile != null && dirFile.exists() && dirFile.isDirectory) {
                if (!TextUtils.isEmpty(exceptPath) && dirFile.path.contains(exceptPath!!)) {
                    return true
                }
                if (dirFile.listFiles() == null)
                    return true
                for (f in dirFile.listFiles()) {
                    if (f.isFile) {
                        f.delete()
                    } else if (f.isDirectory) {
                        deleteDirs(rootDir, f, exceptPath)
                    }
                }
                if (rootDir != dirFile.path) {
                    return dirFile.delete()
                }
            }
            true
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            false
        }
    }

    companion object {
        val instance = FileUtils()
    }
}