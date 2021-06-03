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

