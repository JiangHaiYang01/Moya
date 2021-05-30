package com.allens.moya.result

interface Disposable {
    val isDisposed: Boolean
    fun dispose()
}


class DownLoadBuilder {
    lateinit var onPrepare: (tag: String) -> Unit
    lateinit var onProgress: (tag: String, progress: Int) -> Unit
    lateinit var onUpdate: (
        tag: String,
        progress: Int,
        read: Long,
        count: Long,
        done: Boolean
    ) -> Unit
    lateinit var onSuccess: (tag: String, path: String) -> Unit
    lateinit var onError: (tag: String, Throwable) -> Unit
    lateinit var onCancel: (tag: String) -> Unit
    lateinit var onPause: (tag: String) -> Unit

}
