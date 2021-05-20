package com.allens.moya_coroutines.manager

import com.allens.moya.manager.DownLoadManagerImpl
import com.allens.moya.impl.OnDownLoadInterceptor
import com.allens.moya_coroutines.impl.ApiService
import com.allens.moya_coroutines.request.CoroutinesDisposable
import com.allens.moya_coroutines.request.CoroutinesDownLoadRequest
import com.allens.moya_coroutines.request.getServiceWithOutLogInterceptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

object CoroutinesDownLoadManager : DownLoadManagerImpl<CoroutinesDownLoadRequest, CoroutinesDisposable>() {
    override fun addInterceptor(): MutableSet<OnDownLoadInterceptor> {
        return mutableSetOf()
    }


    override fun startRequest(
        request: CoroutinesDownLoadRequest,
        currentLength: Long,
        block: (ResponseBody?) -> Unit
    ): CoroutinesDisposable {
        val job = request.coroutines.launch(Dispatchers.IO) {
            val response = getServiceWithOutLogInterceptor(request.manager)
                .downloadFile("bytes=$currentLength-", request.url)
            val responseBody = response.body()
            block(responseBody)
        }
        return CoroutinesDisposable(job)
    }

}