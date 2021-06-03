package com.allens.moya.result

import androidx.annotation.MainThread


open class HttpBuilder<T : Any> {
    var onSuccess: (data: T) -> Unit = {}
    var onError: (Throwable) -> Unit = {}
    var onComplete: () -> Unit = {}
}


sealed class HttpResult<out T : Any> {


    data class Success<out T : Any>(val data: T) : HttpResult<T>()
    data class Error(val throwable: Throwable) : HttpResult<Nothing>()

    private var complete: (() -> Unit)? = null

    private var isComplete = false

    // toString 返回结果String
    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success-> $data"
            is Error -> "Error-> ${throwable.message}"
        }
    }


    // 返回结果
    fun get(): T? {
        if (this is Success) {
            return data
        }
        return null
    }

    // 请求成功
    @MainThread
    fun doSuccess(block: (T) -> Unit) = apply {
        if (this is Success) {
            block(data)
            complete?.invoke()
            isComplete = true
        }
    }

    // 请求失败
    @MainThread
    fun doFailed(block: (Throwable) -> Unit) = apply {
        if (this is Error) {
            block(throwable)
            complete?.invoke()
            isComplete = true
        }
    }

    // 请求完成
    @MainThread
    fun doComplete(block: () -> Unit) = apply {
        if (isComplete) {
            block()
        } else {
            complete = block
        }
    }
}