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

