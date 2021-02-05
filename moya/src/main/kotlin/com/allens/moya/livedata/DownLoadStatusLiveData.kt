package com.allens.moya.livedata

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.allens.moya.request.DownLoadRequest
import com.allens.moya.result.DownLoadBuilder
import com.allens.moya.result.DownLoadResult
import com.allens.moya.tools.MoyaLogTool

typealias DownLoadStatusLiveData<T> = EventLiveData<DownLoadResult<T>>

fun <T : Any> DownLoadStatusLiveData<T>.observerState(
    owner: LifecycleOwner? = null,
    viewModel: ViewModel? = null,
    request: DownLoadRequest,
    init: (DownLoadBuilder.() -> Unit)? = null
) {

    val result: DownLoadBuilder? = if (init != null) {
        DownLoadBuilder().apply(init)
    } else {
        null
    }
    val observer: (t: DownLoadResult<T>) -> Unit = { status ->
        when (status) {
            is DownLoadResult.Error -> {
                changeFromError(result, status, request)
            }
            is DownLoadResult.Success -> {
                changeFromSuccess(result, status, request)
            }
            is DownLoadResult.Prepare -> {
                changeFromPrepare(result, request)
            }
            is DownLoadResult.Progress -> {
                changeFromProgress(result, status, request)
            }
            is DownLoadResult.Cancel -> {
                changeFromCancel(result, request)
            }
            is DownLoadResult.Pause -> {
                changeFromPause(result, request)
            }
        }
    }

    when {
        //如果传入了 lifecycle 就交给lifecycle控制，缺点是在后台的时候，不会在change变化
        owner != null -> {
            observeEvent(owner, observer)
        }
        viewModel != null -> {
//        observe(viewModel, observer)
        }
        else -> {
            //如果没有lifecycle 就需要自己在合适的实际 remove observer
            //好处就是可以在后台也刷新。不过没意义。因为一般的刷新进度都是给用户看的。
            //在后台了用户就看不到了
            observeForeverEvent(observer)
        }
    }
}

private fun changeFromPause(
    result: DownLoadBuilder?,
    request: DownLoadRequest
) {
    result?.onPause?.invoke()
    request.listener?.onDownLoadPause(key = request.tag ?: request.url)
}

private fun changeFromCancel(
    result: DownLoadBuilder?,
    request: DownLoadRequest
) {
    result?.onCancel?.invoke()
    request.listener?.onDownLoadCancel(key = request.tag ?: request.url)
}

private fun changeFromProgress(
    result: DownLoadBuilder?,
    status: DownLoadResult.Progress,
    request: DownLoadRequest
) {
    result?.onProgress?.invoke(status.progress)
    request.listener?.onDownLoadProgress(request.tag ?: request.url, status.progress)
    result?.onUpdate?.invoke(
        status.progress,
        status.read,
        status.count,
        status.done
    )
    request.listener?.onUpdate(
        request.tag ?: request.url,
        status.progress,
        status.read,
        status.count,
        status.done
    )
}


private fun changeFromPrepare(
    result: DownLoadBuilder?,
    request: DownLoadRequest
) {
    result?.onPrepare?.invoke()
    request.listener?.onDownLoadPrepare(request.tag ?: request.url)
}

private fun <T : Any> changeFromSuccess(
    result: DownLoadBuilder?,
    status: DownLoadResult.Success<T>,
    request: DownLoadRequest
) {
    result?.onSuccess?.invoke(status.data as String)
    request.listener?.onDownLoadSuccess(
        key = request.tag ?: request.url,
        path = status.data as String
    )
}

private fun changeFromError(
    result: DownLoadBuilder?,
    status: DownLoadResult.Error,
    request: DownLoadRequest
) {
    result?.onError?.invoke(status.throwable)
    request.listener?.onDownLoadError(request.tag ?: request.url, status.throwable)
}