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

