package com.allens.moya.result

import com.allens.moya.livedata.DownLoadStatusLiveData

sealed class DownLoadResult<out T : Any> {

    object Prepare : DownLoadResult<Nothing>()
    data class Success<T : Any>(val path: T) : DownLoadResult<T>()
    data class Error(val throwable: Throwable) : DownLoadResult<Nothing>()
    data class Progress(val progress: Int) : DownLoadResult<Nothing>()
    object Cancel : DownLoadResult<Nothing>()
}

data class DownLoadData(var liveData: DownLoadStatusLiveData<String>, var disposable: Disposable?)