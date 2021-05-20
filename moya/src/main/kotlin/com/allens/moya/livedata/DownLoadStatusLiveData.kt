package com.allens.moya.livedata

import androidx.annotation.MainThread
import androidx.lifecycle.*
import com.allens.moya.manager.DownLoadManagerImpl
import com.allens.moya.request.BasicDownLoadRequest
import com.allens.moya.request.DownLoadRequest
import com.allens.moya.request.getKey
import com.allens.moya.result.Disposable
import com.allens.moya.result.DownLoadBuilder
import com.allens.moya.result.DownLoadResult
import com.allens.moya.tools.MoyaLogTool

typealias DownLoadStatusLiveData = MutableLiveData<DownLoadResult>


@MainThread
fun <T : BasicDownLoadRequest, R : Disposable> DownLoadStatusLiveData.observerState(
    manager: DownLoadManagerImpl<T, R>,
    owner: LifecycleOwner? = null,
    request: DownLoadRequest,
    init: (DownLoadBuilder.() -> Unit)? = null
) {

    val result: DownLoadBuilder? = if (init != null) {
        DownLoadBuilder().apply(init)
    } else {
        null
    }
    val function: (t: DownLoadResult) -> Unit = { status ->
        when (status) {
            is DownLoadResult.Error -> {
                MoyaLogTool.i("下载失败 ${status.throwable.message}")
                changeFromError(result, status, request)
            }
            is DownLoadResult.Success -> {
                MoyaLogTool.i("下载成功 保存位置 ${status.data}")
                changeFromSuccess(result, status, request)
            }
            is DownLoadResult.Prepare -> {
                MoyaLogTool.i("准备下载请求")
                changeFromPrepare(result, request)
            }
            is DownLoadResult.Progress -> {
                MoyaLogTool.i("下载进度 ${status.progress}")
                changeFromProgress(result, status, request)
            }
            is DownLoadResult.Cancel -> {
                MoyaLogTool.i("下载取消")
                changeFromCancel(result, request)
            }
            is DownLoadResult.Pause -> {
                MoyaLogTool.i("下载暂停")
                changeFromPause(result, request)
            }
        }
    }
    val observer = Observer(function)
    when {
        owner != null -> {
            //如果传入了 lifecycle 就交给lifecycle控制，缺点是在后台的时候，不会在change变化
            observe(owner, observer)
            owner.lifecycle.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        manager.observer.remove(request.getKey())
                    }
                }
            })
        }
        else -> {
            //如果没有lifecycle 就需要自己在合适的实际 remove observer
            //好处就是可以在后台也刷新。不过没意义。因为一般的刷新进度都是给用户看的。
            //在后台了用户就看不到了 需要用户自行去remove
            observeForever(observer)
            manager.observer[request.getKey()] = Pair(this, observer)
        }
    }
}

private fun changeFromPause(
    result: DownLoadBuilder?,
    request: DownLoadRequest
) {
    result?.onPause?.invoke()
    request.listener?.onDownLoadPause(request.getKey())
}

private fun changeFromCancel(
    result: DownLoadBuilder?,
    request: DownLoadRequest
) {
    result?.onCancel?.invoke()
    request.listener?.onDownLoadCancel(request.getKey())
}

private fun changeFromProgress(
    result: DownLoadBuilder?,
    status: DownLoadResult.Progress,
    request: DownLoadRequest
) {
    result?.onProgress?.invoke(status.progress)
    request.listener?.onDownLoadProgress(request.getKey(), status.progress)
    result?.onUpdate?.invoke(
        status.progress,
        status.read,
        status.count,
        status.done
    )
    request.listener?.onUpdate(
        request.getKey(),
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
    request.listener?.onDownLoadPrepare(request.getKey())
}

private fun changeFromSuccess(
    result: DownLoadBuilder?,
    status: DownLoadResult.Success,
    request: DownLoadRequest
) {
    result?.onSuccess?.invoke(status.data as String)
    request.listener?.onDownLoadSuccess(
        key = request.getKey(),
        path = status.data as String
    )
}

private fun changeFromError(
    result: DownLoadBuilder?,
    status: DownLoadResult.Error,
    request: DownLoadRequest
) {
    result?.onError?.invoke(status.throwable)
    request.listener?.onDownLoadError(request.getKey(), status.throwable)
}