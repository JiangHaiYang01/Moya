package com.allens.moya.tools


import com.allens.moya.request.DownLoadRequest
import com.allens.moya.tools.MoyaLogTool
import okhttp3.ResponseBody
import java.io.File
import java.io.RandomAccessFile
import java.nio.channels.FileChannel
import java.text.DecimalFormat


object FileTool {


    internal fun downToFile(
        currentLength: Long,
        responseBody: ResponseBody,
        request: DownLoadRequest,
        error: () -> Unit,
        success: (String) -> Unit,
        progress: (currentProgress: Int, currentSaveLength: Long, fileLength: Long) -> Unit,
        stop: () -> Boolean,
        afterStopSave :()->Unit
    ) {
        val filePath = getFilePath(request.path!!, request.name!!)
        if (filePath == null) {
            error()
            return
        }
        val fileLength = getFileLength(currentLength, responseBody)
        val inputStream = responseBody.byteStream()
        val accessFile = RandomAccessFile(File(filePath), "rwd")
        val channel = accessFile.channel
        val mappedBuffer = channel.map(
            FileChannel.MapMode.READ_WRITE,
            currentLength,
            fileLength - currentLength
        )
        val buffer = ByteArray(1024 * 4)
        var len :Int
        var lastProgress = 0
        var currentSaveLength = currentLength // 当前的长度

        while (inputStream.read(buffer).also { len = it } != -1) {
            if (stop()) {
                afterStopSave()
                break
            }
            mappedBuffer.put(buffer, 0, len)
            currentSaveLength += len
            val currentProgress = (currentSaveLength.toFloat() / fileLength * 100).toInt() // 计算百分比
            if (lastProgress != currentProgress) {
                lastProgress = currentProgress
                progress(currentProgress, currentSaveLength, fileLength)
                if (currentSaveLength == fileLength) {
                    success(filePath)
                }
            }
        }
        inputStream.close()
        accessFile.close()
        channel.close()
    }

    //数据总长度
    private fun getFileLength(
        currentLength: Long,
        responseBody: ResponseBody
    ) =
        if (currentLength == 0L) responseBody.contentLength() else currentLength + responseBody.contentLength()


    // 获取下载地址
    private fun getFilePath(savePath: String, saveName: String): String? {
        if (!createFile(savePath)) {
            return null
        }
        return "$savePath/$saveName"

    }


    // 创建文件夹
    private fun createFile(downLoadPath: String): Boolean {
        val file = File(downLoadPath)
        if (!file.exists()) {
            return file.mkdirs()
        }
        return true
    }


    // 格式化小数
    fun bytes2kb(bytes: Long): String {
        return bytes.toKB()
    }
}

// 定义GB的计算常量
private const val GB = 1024 * 1024 * 1024

// 定义MB的计算常量
private const val MB = 1024 * 1024

// 定义KB的计算常量
private const val KB = 1024

// 拓展函数格式化
fun Long.toKB(): String {
    val format = DecimalFormat("###.0")
    return when {
        this / GB >= 1 -> {
            format.format(this / GB) + "GB"
        }
        this / MB >= 1 -> {
            format.format(this / MB) + "MB"
        }
        this / KB >= 1 -> {
            format.format(this / KB) + "KB"
        }
        else -> {
            "${this}B"
        }
    }
}