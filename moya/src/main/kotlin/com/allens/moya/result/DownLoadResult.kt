package com.allens.moya.result

import com.allens.moya.livedata.DownLoadStatusLiveData

sealed class DownLoadResult {

    object Prepare : DownLoadResult()
    object Cancel : DownLoadResult()
    object Pause : DownLoadResult()
    data class Success(val data: String) : DownLoadResult()
    data class Error(val throwable: Throwable) : DownLoadResult()
    data class Progress(
        val progress: Int,
        val read: Long,
        val count: Long,
        val done: Boolean
    ) : DownLoadResult()

}

data class DownLoadData<T : Disposable>(
    var liveData: DownLoadStatusLiveData? = null,
    var disposable: T? = null
)