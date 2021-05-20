package com.allens.moya_coroutines.request

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.allens.moya.manager.HttpManager
import com.allens.moya.request.Request
import com.allens.moya.result.*
import com.allens.moya.tools.MoyaLogTool
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

internal fun getServiceWithOutLogInterceptor(manager: HttpManager): ApiService {
    return manager.getServiceWithOutLogInterceptor()
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
    config: Request.Builder.Config,
    manager: HttpManager,
    crossinline block: suspend (HttpResult<T>) -> Unit,
    crossinline action: suspend () -> String?
): Disposable {
    val job = executeRequest(viewModel = config.viewModel, lifecycleOwner = config.owner) {
        val result = decode<T>(manager) { action() }
        withContext(Dispatchers.Main) {
            block(result)
        }
    }
    return CoroutinesDisposable(job)
}

inline fun <reified T : Any> executeDisable(
    config: Request.Builder.Config,
    manager: HttpManager,
    crossinline init: HttpBuilder<T>.() -> Unit,
    crossinline action: suspend () -> String?
): Disposable {
    val job = executeRequest(viewModel = config.viewModel, lifecycleOwner = config.owner) {
        val result = decode<T>(manager) { action() }
        val apply = HttpBuilder<T>().apply(init)
        withContext(Dispatchers.Main) {
            if (result is HttpResult.Success) {
                MoyaLogTool.i("请求成功")
                apply.onSuccess(result.data)
            } else if (result is HttpResult.Error) {
                MoyaLogTool.i("请求失败:${result.throwable.message}")
                apply.onError(result.throwable)
            }
            apply.onComplete()
        }
    }
    return CoroutinesDisposable(job)
}

inline fun <reified T : Any> executeUpLoadDisable(
    viewModel: ViewModel?,
    lifecycle: LifecycleOwner?,
    manager: HttpManager,
    builder: UpLoadBuilder<T>,
    crossinline action: suspend () -> String?
): Disposable {
    val job = executeRequest(viewModel = viewModel, lifecycleOwner = lifecycle) {
        val result = decode<T>(manager) { action() }

        withContext(Dispatchers.Main) {
            if (result is HttpResult.Success) {
                MoyaLogTool.i("上传请求成功")
                builder.onSuccess(result.data)
            } else if (result is HttpResult.Error) {
                MoyaLogTool.i("上传请求失败:${result.throwable.message}")
                builder.onError(result.throwable)
            }
            builder.onComplete()
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


