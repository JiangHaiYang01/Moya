package com.allens.moya_coroutines.request

import com.allens.moya.livedata.observerState
import com.allens.moya.message.MoyaMessage
import com.allens.moya.request.*
import com.allens.moya.result.*
import com.allens.moya.tools.MoyaLogTool
import com.allens.moya.tools.UrlTool
import com.allens.moya_coroutines.manager.CoroutinesDownLoadManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody


//=============================================================
// 上传
//=============================================================
suspend fun <T : Any> Request.Builder.executeUpLoad(
    parameter: String,
    listener: UpLoadBuilder<T>
): String? {
    for ((key, value) in config.files) {
        config.files[key] =
            ProgressRequestBody(
                value.getRequestBody(),
                progressBlock = { bytesWriting, contentLength, progress ->
                    MoyaLogTool.i("上传进度:$progress $bytesWriting/$contentLength")
                    manager.handler.post {
                        listener.onProgress(progress, bytesWriting, contentLength)
                    }
                },
                errorBlock = {
                    MoyaLogTool.i("上传异常:${it.message}")
                    manager.handler.post {
                        listener.onError(it)
                    }
                })
    }
    return getServiceWithOutLogInterceptor(manager).upLoad(
        parameter,
        config.heard,
        config.map,
        config.files
    ).string()
}


inline fun <reified T : Any> Request.Builder.doUpLoad(
    parameter: String,
    crossinline init: UpLoadBuilder<T>.() -> Unit
): Disposable {
    val builder = UpLoadBuilder<T>().apply(init)
    return executeUpLoadDisable(config.viewModel, config.owner, manager, builder) {
        executeUpLoad(parameter, builder)
    }
}


