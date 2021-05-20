package com.allens.moya.result


class UpLoadBuilder<T : Any> : HttpBuilder<T>() {
    var onProgress: (progress: Int, current: Long, length: Long) -> Unit = { _, _, _ -> }
}


data class UpLoadData<T : Disposable, R : Any>(
    var disposable: T? = null,
    var builder: UpLoadBuilder<R>
)