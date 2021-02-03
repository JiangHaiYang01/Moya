package com.allens.moya.livedata

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.allens.moya.request.DownLoadRequest
import com.allens.moya.result.DownLoadBuilder
import com.allens.moya.result.DownLoadDisposable
import com.allens.moya.result.DownLoadResult

typealias DownLoadStatusLiveData<T> = MutableLiveData<DownLoadResult<T>>

@MainThread
fun <T : Any> DownLoadStatusLiveData<T>.observerState(
    owner: LifecycleOwner,
    request: DownLoadRequest,
    init: (DownLoadBuilder.() -> Unit)? = null
) {

    val result: DownLoadBuilder? = if (init != null) {
        DownLoadBuilder().apply(init)
    } else {
        null
    }
    observe(owner) { status ->
        when (status) {
            is DownLoadResult.Error -> {
                result?.onError?.invoke(status.throwable)
                request.listener?.onDownLoadError(request.tag ?: request.url, status.throwable)
            }
            is DownLoadResult.Success -> {
                result?.onSuccess?.invoke(status.data as String)
                request.listener?.onDownLoadSuccess(
                    key = request.tag ?: request.url,
                    path = status.data as String
                )
            }
            is DownLoadResult.Prepare -> {
                result?.onPrepare?.invoke()
                request.listener?.onDownLoadPrepare(request.tag ?: request.url)
            }
            is DownLoadResult.Progress -> {
                result?.onProgress?.invoke(status.progress)
                request.listener?.onDownLoadProgress(request.tag ?: request.url, status.progress)
            }
            is DownLoadResult.Cancel -> {
                result?.onCancel?.invoke()
                request.listener?.onDownLoadCancel(key = request.tag ?: request.url)
            }
            is DownLoadResult.Pause -> {
                result?.onPause?.invoke()
                request.listener?.onDownLoadPause(key = request.tag ?: request.url)
            }
        }
    }
}