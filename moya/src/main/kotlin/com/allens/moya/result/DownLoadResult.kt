package com.allens.moya.result

import com.allens.moya.livedata.DownLoadStatusLiveData

sealed class DownLoadResult<out T : Any> {

    object Prepare : DownLoadResult<Nothing>()
    data class Success<T : Any>(val path: T) : DownLoadResult<T>()
    data class Error(val throwable: Throwable) : DownLoadResult<Nothing>()
    data class Progress(val progress: Int) : DownLoadResult<Nothing>()
}


//class DownLoadResultBuilder<T>() {
//    var onPrepare: () -> Unit = {}
//    var onProgress: (progress: Int) -> Unit = {}
//    var onSuccess: (data: T) -> Unit = {}
//    var onError: (Throwable) -> Unit = {}
//}

data class DownLoadData(var liveData: DownLoadStatusLiveData<String>, var disposable: Disposable?)