package com.allens.moya.result

import com.allens.moya.livedata.DownLoadStatusLiveData

sealed class DownLoadResult<out T : Any> {

    object Prepare : DownLoadResult<Nothing>()
    object Cancel : DownLoadResult<Nothing>()
    object Pause : DownLoadResult<Nothing>()
    data class Success<T : Any>(val data: T) : DownLoadResult<T>()
    data class Error(val throwable: Throwable) : DownLoadResult<Nothing>()
    data class Progress(
        val progress: Int,
        val read: Long,
        val count: Long,
        val done: Boolean
    ) : DownLoadResult<Nothing>()

}

data class DownLoadData<T : Disposable>(
    var liveData: DownLoadStatusLiveData<String>? = null,
    var disposable: T? = null
)