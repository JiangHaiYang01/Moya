package com.allens.moya_coroutines.request

import com.allens.moya.livedata.observerState
import com.allens.moya.result.Disposable
import com.allens.moya.request.*
import com.allens.moya.result.HttpResult
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

/**
 * @Describe get 请求
 * @param    [parameter] 请求地址,跟在BaseUrl 后面的
 * @return   @see [HttpResult]
 */
suspend inline fun <reified T : Any> Request.Builder.doGet(
    parameter: String,
): HttpResult<T> = execute(manager) { executeGet(parameter) }


inline fun <reified T : Any> Request.Builder.doGet(
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
    crossinline block: suspend (HttpResult<T>) -> Unit
): Disposable = executeDisable(viewModel, owner, manager, block) {
    executePut(parameter)
}


//=============================================================
// 下载
//=============================================================
//todo 这里 需要做一个协程的异常处理
suspend fun Request.Builder.doDownLoad(request: DownLoadRequest) {
    withContext(Dispatchers.IO) {
        val coroutinesDownLoadRequest = CoroutinesDownLoadRequest().also {
            it.coroutines = this
            it.url = request.url
            it.listener = request.listener
            it.name = request.name
            it.path = request.path
            it.manager = manager
        }
        val startDownLoad = DownLoadManager.startDownLoad(coroutinesDownLoadRequest)
        withContext(Dispatchers.Main){
            startDownLoad.observerState(owner!!) {
                onError = {
                    println("failed ${it.message}")
                }
                onPrepare = {
                    println("prepare")
                }
                onSuccess = {
                    println("success $it")
                }
                onProgress = {
                    println("progress:$it")
                }
            }
        }



    }
}
