package com.allens.moya_coroutines.manager

import com.allens.moya.impl.DownLoadManagerImpl
import com.allens.moya.impl.OnDownLoadInterceptor
import com.allens.moya.result.Disposable
import com.allens.moya_coroutines.impl.ApiService
import com.allens.moya_coroutines.request.CoroutinesDisposable
import com.allens.moya_coroutines.request.CoroutinesDownLoadRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

object DownLoadManager : DownLoadManagerImpl<CoroutinesDownLoadRequest>() {
    override fun addInterceptor(): MutableSet<OnDownLoadInterceptor> {
        return mutableSetOf()
    }

    override fun stopSave(request: CoroutinesDownLoadRequest): Boolean {
        return !request.coroutines.isActive
    }

    override fun startRequest(
        request: CoroutinesDownLoadRequest,
        currentLength: Long,
        block: (ResponseBody?) -> Unit
    ): Disposable {
        val job = request.coroutines.launch(Dispatchers.IO) {
            val response = request.manager.getServiceWithOutLogInterceptor<ApiService>()
                .downloadFile("bytes=$currentLength-", request.url)
            val responseBody = response.body()
            block(responseBody)
        }
        return CoroutinesDisposable(job)
    }

}