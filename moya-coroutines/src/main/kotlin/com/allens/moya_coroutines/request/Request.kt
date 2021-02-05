package com.allens.moya_coroutines.request

import androidx.lifecycle.LifecycleOwner
import com.allens.moya.livedata.observerState
import com.allens.moya.message.MoyaMessage
import com.allens.moya.request.*
import com.allens.moya.result.*
import com.allens.moya.tools.MoyaLogTool
import com.allens.moya.tools.UrlTool
import com.allens.moya_coroutines.manager.DownLoadManager
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
    if (map.size > 0) {
        val param: String = UrlTool.prepareParam(map)
        if (param.trim().isNotEmpty()) {
            getUrl += "?$param"
        }
    }
    return getService(manager).doGet(heard, getUrl).string()
}

suspend inline fun <reified T : Any> Request.Builder.doGet(
    parameter: String,
): HttpResult<T> = execute(manager) { executeGet(parameter) }


inline fun <reified T : Any> Request.Builder.doGet(
    parameter: String,
    crossinline init: HttpBuilder<T>.() -> Unit
): Disposable = executeDisable(viewModel, owner, manager, init) {
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
): Disposable = executeDisable(viewModel, owner, manager, block) {
    executeGet(parameter)
}

//=============================================================
// post 表单提交
//=============================================================
suspend fun Request.Builder.executePost(parameter: String): String? =
    getService(manager).doPost(parameter, heard, map).string()

suspend inline fun <reified T : Any> Request.Builder.doPost(
    parameter: String,
): HttpResult<T> = execute(manager) { executePost(parameter) }


inline fun <reified T : Any> Request.Builder.doPost(
    parameter: String,
    crossinline init: HttpBuilder<T>.() -> Unit
): Disposable = executeDisable(viewModel, owner, manager, init) {
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
): Disposable = executeDisable(viewModel, owner, manager, block) {
    executePost(parameter)
}


//=============================================================
// post json 的方式请求
//=============================================================
suspend fun Request.Builder.executeBody(parameter: String): String? {
    val requestBody = manager.gson.toJson(map)
        .toRequestBody("application/json".toMediaTypeOrNull())
    return getService(manager).doBody(parameter, heard, requestBody).string()
}

suspend inline fun <reified T : Any> Request.Builder.doBody(
    parameter: String,
): HttpResult<T> = execute(manager) { executeBody(parameter) }


inline fun <reified T : Any> Request.Builder.doBody(
    parameter: String,
    crossinline init: HttpBuilder<T>.() -> Unit
): Disposable = executeDisable(viewModel, owner, manager, init) {
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
): Disposable = executeDisable(viewModel, owner, manager, block) {
    executeBody(parameter)
}

//=============================================================
// delete
//=============================================================
suspend fun Request.Builder.executeDelete(parameter: String): String? =
    getService(manager).doDelete(parameter, heard, map).string()

suspend inline fun <reified T : Any> Request.Builder.doDelete(
    parameter: String,
): HttpResult<T> = execute(manager) { executeDelete(parameter) }


inline fun <reified T : Any> Request.Builder.doDelete(
    parameter: String,
    crossinline init: HttpBuilder<T>.() -> Unit
): Disposable = executeDisable(viewModel, owner, manager, init) {
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
): Disposable = executeDisable(viewModel, owner, manager, block) {
    executeDelete(parameter)
}

//=============================================================
// put
//=============================================================
suspend fun Request.Builder.executePut(parameter: String): String? =
    getService(manager).doPut(parameter, heard, map).string()

suspend inline fun <reified T : Any> Request.Builder.doPut(
    parameter: String,
): HttpResult<T> = execute(manager) { executePut(parameter) }


inline fun <reified T : Any> Request.Builder.doPut(
    parameter: String,
    crossinline init: HttpBuilder<T>.() -> Unit
): Disposable = executeDisable(viewModel, owner, manager, init) {
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
): Disposable = executeDisable(viewModel, owner, manager, block) {
    executePut(parameter)
}


//=============================================================
// 下载
//=============================================================

//todo 2 如果绑定的viewModel 需要在其Clear 的时候 remove Observer
//todo 3 如果什么都没绑定 需要在外部由用户去 remove Observer
//todo 4 下载的队列以及优先级
//todo 5 下载取消去暂停 如果在协程内部 用户自行cancel 了  需要在状态中感知到。并且抛出cancel的状态出去
//todo 6 需要加上自行开启的协程块。
//todo 7 下载的链式调用 需要考虑一下其他的方式 待定
suspend fun Request.Builder.doDownLoad(
    request: DownLoadRequest,
    init: (DownLoadBuilder.() -> Unit)? = null
) {
    withContext(Dispatchers.IO) {
        val coroutinesDownLoadRequest = CoroutinesDownLoadRequest().also {
            it.coroutines = this
            it.url = request.url
            it.listener = request.listener
            it.name = request.name
            it.tag = request.tag
            it.path = request.path
            it.manager = manager
        }
        val data = DownLoadManager.startDownLoad(coroutinesDownLoadRequest)
        withContext(Dispatchers.Main){

        }
        withContext(Dispatchers.Main) {
            data.liveData.observerState(
                owner = owner,
                viewModel = viewModel,
                request = coroutinesDownLoadRequest,
                init = init
            )
        }
    }
}
