package com.allens.moya.result

interface Disposable {
    val isDisposed: Boolean
    fun dispose()
}

abstract class DownLoadDisposable : Disposable {

    internal var onPrepare: () -> Unit = {}
    internal var onProgress: (progress: Int) -> Unit = {}
    internal var onSuccess: (path: String) -> Unit = {}
    internal var onError: (Throwable) -> Unit = {}

    fun doSuccess(block: (String) -> Unit) = apply {
        onSuccess = block
    }

    fun doFail(block: (Throwable) -> Unit) = apply {
        onError = block
    }

    fun doPrepare(block: () -> Unit) = apply {
        onPrepare = block
    }

    fun onProgress(block: (Int) -> Unit) = apply {
        onProgress = block
    }
}


suspend fun main() {
    val test = Test()



    test.progress {
        println("progress:$it")
    }



    repeat(10) {
        Thread.sleep(1000)
        test.doProgress.invoke(it)
    }


}


class Test() {


    var doProgress: (Int) -> Unit = {}

    fun progress(block: (Int) -> Unit) {
        doProgress = block
    }
}
