package com.example.floatingwindowdemo.util

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
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


    /**
     * 根据URI获取文件真实路径（兼容多张机型）
     * @param context
     * @param uri
     * @return
     */
    fun getFilePathByUri(context: Context, uri: Uri): String? {
        if ("content".equals(uri.getScheme(), ignoreCase = true)) {
            val sdkVersion = Build.VERSION.SDK_INT
            return if (sdkVersion >= 19) { // api >= 19
                getRealPathFromUriAboveApi19(context, uri)
            } else { // api < 19
                getRealPathFromUriBelowAPI19(context, uri)
            }
        } else if ("file".equals(uri.getScheme(), ignoreCase = true)) {
            return uri.getPath()
        }
        return null
    }

    /**
     * 适配api19及以上,根据uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    @SuppressLint("NewApi")
    fun getRealPathFromUriAboveApi19(context: Context, uri: Uri): String? {
        var filePath: String? = null
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是document类型的 uri, 则通过document id来进行处理
            val documentId = DocumentsContract.getDocumentId(uri)
            if (isMediaDocument(uri)) { // MediaProvider
                // 使用':'分割
                val type = documentId.split(":").toTypedArray()[0]
                val id = documentId.split(":").toTypedArray()[1]
                val selection = MediaStore.Images.Media._ID + "=?"
                val selectionArgs = arrayOf(id)

                //
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                LogUtils.instance.getLogPrint(type+"   "+id+"   "+selection+"   "+contentUri?.toString())
                filePath = getDataColumn(context, contentUri, selection, selectionArgs)
            } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                val contentUri: Uri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(documentId))
                filePath = getDataColumn(context, contentUri, null, null)
            } else if (isExternalStorageDocument(uri)) {
                // ExternalStorageProvider
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    filePath = Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else {
                //Log.e("路径错误");
            }
        } else if ("content".equals(uri.getScheme(), ignoreCase = true)) {
            // 如果是 content 类型的 Uri
            filePath = getDataColumn(context, uri, null, null)
        } else if ("file" == uri.getScheme()) {
            // 如果是 file 类型的 Uri,直接获取图片对应的路径
            filePath = uri.getPath()
        }
        return filePath
    }

    /**
     * 适配api19以下(不包括api19),根据uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    private fun getRealPathFromUriBelowAPI19(context: Context, uri: Uri): String? {
        return getDataColumn(context, uri, null, null)
    }

    /**
     * 获取数据库表中的 _data 列，即返回Uri对应的文件路径
     *
     * @return
     */
    private fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
        var path: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        var cursor: Cursor? = null
        if(uri == null)
            return null
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null)
            LogUtils.instance.getLogPrint("create cursor")
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex: Int = cursor.getColumnIndexOrThrow(projection[0])
                path = cursor.getString(columnIndex)
                LogUtils.instance.getLogPrint(columnIndex.toString()+"   "+path)
            }
        } catch (e: java.lang.Exception) {
            LogUtils.instance.getLogPrint("exception has happened")
            if (cursor != null) {
                cursor.close()
            }
            e.printStackTrace()
        }
        return path
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is MediaProvider
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.getAuthority()
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.getAuthority()
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is DownloadsProvider
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.getAuthority()
    }

    companion object {
        val instance = FileUtils()
    }
}