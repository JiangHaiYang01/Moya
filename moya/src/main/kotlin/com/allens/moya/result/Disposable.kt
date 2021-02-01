package com.allens.moya.result

interface Disposable {
    val isDisposed: Boolean
    fun dispose()
}

abstract class DownLoadDisposable : Disposable
