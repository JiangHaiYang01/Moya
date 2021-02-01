package com.allens.moya_coroutines.request

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.allens.moya.impl.Disposable
import com.allens.moya.manager.HttpManager
import com.allens.moya.request.HttpResult
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
        block(decode(manager) { action() })
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
): HttpResult<T> = try {
    HttpResult.Success(manager.gson.fromJson(block(), T::class.java))
} catch (t: Throwable) {
    HttpResult.Error(t)
}


