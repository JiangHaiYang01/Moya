package com.allens.moya.livedata

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.allens.moya.result.DownLoadResult
import com.allens.moya.result.DownLoadResultBuilder

typealias DownLoadStatusLiveData<T> = MutableLiveData<DownLoadResult<T>>


fun <T : Any> DownLoadStatusLiveData<T>.observerState(
    owner: LifecycleOwner,
    init: DownLoadResultBuilder<T>.() -> Unit
) {
    println("observerState in ${Thread.currentThread().name}")
    val result = DownLoadResultBuilder<T>().apply(init)
    observe(owner) { status ->
        when (status) {
            is DownLoadResult.Error -> {
                result.onError.invoke(status.throwable)
            }
            is DownLoadResult.Success -> {
                result.onSuccess.invoke(status.path)
            }
            is DownLoadResult.Prepare -> {
                result.onPrepare.invoke()
            }
            is DownLoadResult.Progress -> {
                result.onProgress.invoke(status.progress)
            }
        }
    }
}