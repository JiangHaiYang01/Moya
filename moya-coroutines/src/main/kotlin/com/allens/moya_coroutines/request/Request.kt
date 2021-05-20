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
// get
//=============================================================
suspend fun Request.Builder.executeGet(parameter: String): String? {
    val baseUrl = manager.retrofit.baseUrl().toString()
    var getUrl: String = baseUrl + parameter
    if (config.map.size > 0) {
        val param: String = UrlTool.prepareParam(config.map)
        if (param.trim().isNotEmpty()) {
            getUrl += "?$param"
        }
    }
    return getService(manager).doGet(config.heard, getUrl).string()
}

suspend inline fun <reified T : Any> Request.Builder.doGet(
    parameter: String,
): HttpResult<T> = execute(manager) { executeGet(parameter) }


inline fun <reified T : Any> Request.Builder.doGet(
    parameter: String,
    crossinline init: HttpBuilder<T>.() -> Unit
): Disposable = executeDisable(config, manager, init) {
    executeGet(parameter)
}

@Deprecated(
    message = MoyaMessage.Deprecated,
    level = DeprecationLevel.WARNING,
    replaceWith = ReplaceWith(MoyaMessage.GET, imports = arrayOf(MoyaMessage.IMPORTS))
)
inline fun <reified T : Any> Request.Builder.doGetBlock(
    parameter: String,
    crossinline block: suspend (HttpResult<T>) -> Unit
): Disposable = executeDisable(config, manager, block) {
    executeGet(parameter)
}

//=============================================================
// post 表单提交
//=============================================================
suspend fun Request.Builder.executePost(parameter: String): String? =
    getService(manager).doPost(parameter, config.heard, config.map).string()

suspend inline fun <reified T : Any> Request.Builder.doPost(
    parameter: String,
): HttpResult<T> = execute(manager) { executePost(parameter) }


inline fun <reified T : Any> Request.Builder.doPost(
    parameter: String,
    crossinline init: HttpBuilder<T>.() -> Unit
): Disposable = executeDisable(config, manager, init) {
    executePost(parameter)
}

@Deprecated(
    message = MoyaMessage.Deprecated,
    level = DeprecationLevel.WARNING,
    replaceWith = ReplaceWith(MoyaMessage.POST, imports = arrayOf(MoyaMessage.IMPORTS))
)
inline fun <reified T : Any> Request.Builder.doPostBlock(
    parameter: String,
    crossinline block: suspend (HttpResult<T>) -> Unit
): Disposable = executeDisable(config, manager, block) {
    executePost(parameter)
}


//=============================================================
// post json 的方式请求
//=============================================================
suspend fun Request.Builder.executeBody(parameter: String): String? {
    val requestBody = manager.gson.toJson(config.map)
        .toRequestBody("application/json".toMediaTypeOrNull())
    return getService(manager).doBody(parameter, config.heard, requestBody).string()
}

suspend inline fun <reified T : Any> Request.Builder.doBody(
    parameter: String,
): HttpResult<T> = execute(manager) { executeBody(parameter) }


inline fun <reified T : Any> Request.Builder.doBody(
    parameter: String,
    crossinline init: HttpBuilder<T>.() -> Unit
): Disposable = executeDisable(config, manager, init) {
    executeBody(parameter)
}


@Deprecated(
    message = MoyaMessage.Deprecated,
    level = DeprecationLevel.WARNING,
    replaceWith = ReplaceWith(MoyaMessage.BODY, imports = arrayOf(MoyaMessage.IMPORTS))
)
inline fun <reified T : Any> Request.Builder.doBodyBlock(
    parameter: String,
    crossinline block: suspend (HttpResult<T>) -> Unit
): Disposable = executeDisable(config, manager, block) {
    executeBody(parameter)
}

//=============================================================
// delete
//=============================================================
suspend fun Request.Builder.executeDelete(parameter: String): String? =
    getService(manager).doDelete(parameter, config.heard, config.map).string()

suspend inline fun <reified T : Any> Request.Builder.doDelete(
    parameter: String,
): HttpResult<T> = execute(manager) { executeDelete(parameter) }


inline fun <reified T : Any> Request.Builder.doDelete(
    parameter: String,
    crossinline init: HttpBuilder<T>.() -> Unit
): Disposable = executeDisable(config, manager, init) {
    executeDelete(parameter)
}

@Deprecated(
    message = MoyaMessage.Deprecated,
    level = DeprecationLevel.WARNING,
    replaceWith = ReplaceWith(MoyaMessage.DELETE, imports = arrayOf(MoyaMessage.IMPORTS))
)
inline fun <reified T : Any> Request.Builder.doDeleteBlock(
    parameter: String,
    crossinline block: suspend (HttpResult<T>) -> Unit
): Disposable = executeDisable(config, manager, block) {
    executeDelete(parameter)
}

//=============================================================
// put
//=============================================================
suspend fun Request.Builder.executePut(parameter: String): String? =
    getService(manager).doPut(parameter, config.heard, config.map).string()

suspend inline fun <reified T : Any> Request.Builder.doPut(
    parameter: String,
): HttpResult<T> = execute(manager) { executePut(parameter) }


inline fun <reified T : Any> Request.Builder.doPut(
    parameter: String,
    crossinline init: HttpBuilder<T>.() -> Unit
): Disposable = executeDisable(config, manager, init) {
    executePut(parameter)
}

@Deprecated(
    message = MoyaMessage.Deprecated,
    level = DeprecationLevel.WARNING,
    replaceWith = ReplaceWith(MoyaMessage.PUT, imports = arrayOf(MoyaMessage.IMPORTS))
)
inline fun <reified T : Any> Request.Builder.doPutBlock(
    parameter: String,
    crossinline block: suspend (HttpResult<T>) -> Unit
): Disposable = executeDisable(config, manager, block) {
    executePut(parameter)
}

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

//=============================================================
// 下载
//=============================================================
suspend fun Request.Builder.doDownLoad(
    request: DownLoadRequest,
    init: (DownLoadBuilder.() -> Unit)? = null
) {
    withContext(Dispatchers.IO) {
        val coroutinesDownLoadRequest = convertToCoroutinesRequest(request, this)
        val data = CoroutinesDownLoadManager.startDownLoad(coroutinesDownLoadRequest)
        withContext(Dispatchers.Main) {
            data.liveData?.observerState(
                manager = CoroutinesDownLoadManager,
                owner = config.owner,
                request = coroutinesDownLoadRequest,
                init = init
            )
        }
    }
}

private fun Request.Builder.convertToCoroutinesRequest(
    request: DownLoadRequest,
    scope: CoroutineScope? = null
): CoroutinesDownLoadRequest {
    return CoroutinesDownLoadRequest().also {
        if (scope != null) {
            it.coroutines = scope
        }
        it.url = request.url
        it.listener = request.listener
        it.name = request.name
        it.tag = request.tag
        it.path = request.path
        it.manager = manager
    }
}

//如果在请求的时候没有tag 则使用url 作为key
fun Request.Builder.doCancelDownLoad(request: DownLoadRequest) {
    CoroutinesDownLoadManager.cancel(convertToCoroutinesRequest(request))
}

//暂停某一个请求的下载
fun Request.Builder.doPauseDownLoad(request: DownLoadRequest) {
    CoroutinesDownLoadManager.pause(convertToCoroutinesRequest(request))
}


//全部取消下载
fun Request.Builder.doDownLoadCancelAll() {
    CoroutinesDownLoadManager.cancelAll()
}

//全部终止下载
fun Request.Builder.doDownLoadPauseAll() {
    CoroutinesDownLoadManager.pauseAll()
}

