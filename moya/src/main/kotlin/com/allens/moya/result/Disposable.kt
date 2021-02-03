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

    fun doProgress(block: (Int) -> Unit) = apply {
        onProgress = block
    }

    fun doTest() = apply {
        println("doTest =====")
    }
}

fun main() {

    val test = Test()
    test.doTest1{
            println("haha =====")
        }
    Thread.sleep(1000)
    test.test.invoke()
}

fun fuck(){
    println("fuck")
}

class Test{

    var test:()->Unit = {}

    fun doTest1(block: () -> Unit) = apply {
        println("doTest1 =====")
        test = block
    }
}

