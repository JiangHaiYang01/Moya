package com.allens.moya.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.allens.moya.impl.OnDownLoadListener
import com.allens.moya.request.DownLoadRequest
import com.allens.moya.result.DownLoadDisposable
import com.allens.moya.result.DownLoadResult

typealias DownLoadStatusLiveData<T> = MutableLiveData<DownLoadResult<T>>


fun <T : Any> DownLoadStatusLiveData<T>.observerState(
    owner: LifecycleOwner,
    disposable: DownLoadDisposable,
    request: DownLoadRequest,
) {
    observe(owner) { status ->
        when (status) {
            is DownLoadResult.Error -> {
                disposable.onError(status.throwable)
                request.listener?.onDownLoadError(request.tag ?: request.url, status.throwable)
            }
            is DownLoadResult.Success -> {
                disposable.onSuccess(status.path as String)
                request.listener?.onDownLoadSuccess(
                    key = request.tag ?: request.url,
                    path = status.path as String
                )
            }
            is DownLoadResult.Prepare -> {
                disposable.onPrepare()
                request.listener?.onDownLoadPrepare(request.tag ?: request.url)
            }
            is DownLoadResult.Progress -> {
                disposable.onProgress(status.progress)
                request.listener?.onDownLoadProgress(request.tag ?: request.url, status.progress)
            }
            is DownLoadResult.Cancel -> {

            }
        }
    }
}