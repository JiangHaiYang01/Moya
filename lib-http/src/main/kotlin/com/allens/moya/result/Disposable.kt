package com.allens.moya.result

interface Disposable {
    val isDisposed: Boolean
    fun dispose()
}


class DownLoadBuilder {
    var onPrepare: () -> Unit = {}
    var onProgress: (progress: Int) -> Unit = {}
    var onUpdate: (
        progress: Int,
        read: Long,
        count: Long,
        done: Boolean
    ) -> Unit = { _, _, _, _ -> }
    var onSuccess: (path: String) -> Unit = {}
    var onError: (Throwable) -> Unit = {}
    var onCancel: () -> Unit = {}
    var onPause: () -> Unit = {}

}
