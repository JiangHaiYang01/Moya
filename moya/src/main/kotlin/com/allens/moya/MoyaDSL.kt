package com.allens.moya

import android.content.Context
import com.allens.moya.config.LoggerConfig
import com.allens.moya.config.NetWorkCache
import com.allens.moya.config.NetWorkTimeOut
import com.allens.moya.delegate.LambdaDelegate
import com.allens.moya.enums.LoggerLevel
import com.allens.moya.impl.OnCookieInterceptor
import retrofit2.CallAdapter
import retrofit2.Converter

/***
 * 此类是 moya 的拓展属性，方便使用dsl 实现接口配置
 * 不过因为名称不能和拓展方法相同。所以将参数简化
 * 例如 baseUrl --> url
 */

fun moya(context: Context, block: Moya.Builder.() -> Unit): Moya {
    return Moya.Builder().apply {
        block()
    }.build(context)
}


var Moya.Builder.url: String
    get() = httpConfig.baseUrl
    set(value) {
        baseUrl(value)
    }

var Moya.Builder.retry: Boolean
    get() = httpConfig.retryOnConnectionFailure
    set(value) {
        retryOnConnectionFailure(value)
    }

var Moya.Builder.level: LoggerLevel
    get() = httpConfig.level
    set(value) {
        logLevel(value)
    }

var Moya.Builder.debug: Boolean
    get() = Moya.debug
    set(value) {
        debug(value)
    }

// 配置网络超时时间等
var Moya.Builder.time: NetWorkTimeOut.() -> Unit by LambdaDelegate<NetWorkTimeOut, Moya.Builder>(
    NetWorkTimeOut()
) { netWorkTime, builder ->
    builder.connectTimeout(time = netWorkTime.connect)
    builder.readTimeout(time = netWorkTime.read)
    builder.writeTimeout(time = netWorkTime.write)
}

// 配置缓存等
var Moya.Builder.cache: NetWorkCache.() -> Unit by LambdaDelegate<NetWorkCache, Moya.Builder>(
    NetWorkCache()
) { cache, builder ->
    builder.cacheNetWorkTimeOut(cache.networkTimeOut)
    builder.cacheNoNetWorkTimeOut(cache.noNetworkTimeOut)
    builder.cacheSize(cache.size)
    builder.cachePath(cache.path)
    builder.cacheType(cache.type)
}

// 配置通用的请求头
var Moya.Builder.head: HashMap<String, String>.() -> Unit by LambdaDelegate<HashMap<String, String>, Moya.Builder>(
    HashMap()
) { map, builder ->
    map.forEach {
        builder.head(it.key, it.value)
    }
}

// 配置日志
var Moya.Builder.log: LoggerConfig.() -> Unit by LambdaDelegate<LoggerConfig, Moya.Builder>(
    LoggerConfig()
) { log, builder ->
    builder.logLevel(log.level)
    log.interceptors?.forEach {
        builder.logInterceptor(it)
    }
}

// 配置工厂
var Moya.Builder.adapter: MutableSet<CallAdapter.Factory>.() -> Unit by LambdaDelegate<MutableSet<CallAdapter.Factory>, Moya.Builder>(
    mutableSetOf()
) { set, builder ->
    set.forEach {
        builder.callAdapterFactory(it)
    }
}

// 配置工厂
var Moya.Builder.converter: MutableSet<Converter.Factory>.() -> Unit by LambdaDelegate<MutableSet<Converter.Factory>, Moya.Builder>(
    mutableSetOf()
) { set, builder ->
    set.forEach {
        builder.converterFactory(it)
    }
}

// 配置 cookie
var Moya.Builder.cookie: MutableSet<OnCookieInterceptor>.() -> Unit by LambdaDelegate<MutableSet<OnCookieInterceptor>, Moya.Builder>(
    mutableSetOf()
) { set, builder ->
    set.forEach {
        builder.cookieInterceptor(it)
    }
}






