package com.allens.moya_coroutines.request

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.allens.moya.result.Disposable
import com.allens.moya.manager.HttpManager
import com.allens.moya.result.HttpBuilder
import com.allens.moya.result.HttpResult
import com.allens.moya_coroutines.impl.ApiService
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import retrofit2.Response

//=============================================================
// 公共方法
//=============================================================
internal fun getService(manager: HttpManager): ApiService {
    return manager.getService()
}

internal fun Response<ResponseBody>.string(): String? {
    return body()?.string()
}

suspend inline fun <reified T : Any> execute(
    manager: HttpManager,
    crossinline block: suspend () -> String?
): HttpResult<T> = withContext(Dispatchers.IO) {
    decode(manager) { block() }
}

inline fun <reified T : Any> executeDisable(
    viewModel: ViewModel?,
    lifecycle: LifecycleOwner?,
    manager: HttpManager,
    crossinline block: suspend (HttpResult<T>) -> Unit,
    crossinline action: suspend () -> String?
): Disposable {
    val job = executeRequest(viewModel = viewModel, lifecycleOwner = lifecycle) {
        val result = decode<T>(manager) { action() }
        withContext(Dispatchers.Main) {
            block(result)
        }
    }
    return CoroutinesDisposable(job)
}


inline fun <reified T : Any> executeDisable(
    viewModel: ViewModel?,
    lifecycle: LifecycleOwner?,
    manager: HttpManager,
    crossinline init: HttpBuilder<T>.() -> Unit,
    crossinline action: suspend () -> String?
): Disposable {
    val job = executeRequest(viewModel = viewModel, lifecycleOwner = lifecycle) {
        val result = decode<T>(manager) { action() }
        withContext(Dispatchers.Main) {
            val apply = HttpBuilder<T>().apply(init)
            if (result is HttpResult.Success) {
                apply.onSuccess(result.data)
            } else if (result is HttpResult.Error) {
                apply.onError(result.throwable)
            }
            apply.onComplete()
        }
    }
    return CoroutinesDisposable(job)
}


inline fun executeRequest(
    viewModel: ViewModel?,
    lifecycleOwner: LifecycleOwner?,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    crossinline block: suspend CoroutineScope.() -> Unit
): Job = when {
    viewModel != null -> viewModel.viewModelScope
    lifecycleOwner != null -> lifecycleOwner.lifecycleScope
    else -> {
        throw Throwable("must bind viewModel or lifecycleOwner please use lifecycle() or viewModel() to bind")
    }
}.launch(Dispatchers.IO, start) {
    block()
}

inline fun <reified T : Any> decode(
    manager: HttpManager,
    block: () -> String?,
): HttpResult<T> {
    return try {
        val name1 = T::class.java.name
        val name2 = String::class.java.name
        if (name1 == name2) {
            HttpResult.Success(block() as T)
        } else {
            HttpResult.Success(manager.gson.fromJson(block(), T::class.java))
        }
    } catch (t: Throwable) {
        HttpResult.Error(t)
    }
}


