package com.allens.moya.request


import androidx.annotation.Nullable
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*
import java.io.IOException

class ProgressRequestBody(
    private val requestBody: RequestBody,
    val progressBlock: ((
        bytesWriting: Long,
        contentLength: Long,
        progress: Int
    ) -> Unit)? = null,
     val errorBlock: ((Throwable) -> Unit)? = null
) : RequestBody() {
    private var bufferedSink: BufferedSink? = null

    private var lastProgress: Int = 0


    fun getRequestBody(): RequestBody {
        return requestBody
    }

    @Nullable
    override fun contentType(): MediaType? {
        return requestBody.contentType()
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return requestBody.contentLength()
    }

    //关键方法
    override fun writeTo(sink: BufferedSink) {
        try {
            if (null == bufferedSink) bufferedSink = sink(sink).buffer()
            requestBody.writeTo(bufferedSink!!)
            //必须调用flush，否则最后一部分数据可能不会被写入
            bufferedSink?.flush()
        } catch (t: Throwable) {
            errorBlock?.let { it(t) }
        }
    }

    private fun sink(sink: Sink): Sink {
        return object : ForwardingSink(sink) {
            var bytesWriting = 0L
            var contentLength = 0L

            @Throws(IOException::class)
            override fun write(source: Buffer, byteCount: Long) {
                super.write(source, byteCount)
                if (0L == contentLength) contentLength = contentLength()
                bytesWriting += byteCount
                //调用接口，把上传文件的进度传过去
                val progress = (bytesWriting.toFloat() / contentLength * 100).toInt() // 计算百分比
                if (lastProgress != progress) {
                    lastProgress = progress
                    progressBlock?.let { it(bytesWriting, contentLength, progress) }
                }
            }
        }
    }

}