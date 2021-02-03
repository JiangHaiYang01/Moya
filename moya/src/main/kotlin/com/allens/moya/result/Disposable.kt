package com.allens.moya.result

interface Disposable {
    val isDisposed: Boolean
    fun dispose()
}


class DownLoadBuilder {
    var onPrepare: () -> Unit = {}
    var onProgress: (progress: Int) -> Unit = {}
    var onSuccess: (path: String) -> Unit = {}
    var onError: (Throwable) -> Unit = {}
    var onCancel: () -> Unit = {}
    var onPause: () -> Unit = {}

}

abstract class DownLoadDisposable : Disposable {

    abstract fun cancel()

    abstract fun pause()
}