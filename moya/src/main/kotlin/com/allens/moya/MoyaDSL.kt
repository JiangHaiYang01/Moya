package com.allens.moya

import android.content.Context
import com.allens.moya.config.NetCacheCache
import com.allens.moya.config.NetWorkTimeOut
import com.allens.moya.delegate.LambdaDelegate
import com.allens.moya.enums.LoggerLevel

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

// 配置网络超时时间等
var Moya.Builder.time: NetWorkTimeOut.() -> Unit by LambdaDelegate<NetWorkTimeOut, Moya.Builder>(
    NetWorkTimeOut()
) { netWorkTime, builder ->
    builder.connectTimeout(time = netWorkTime.connect)
    builder.readTimeout(time = netWorkTime.read)
    builder.writeTimeout(time = netWorkTime.write)
}

// 配置缓存等
var Moya.Builder.cache: NetCacheCache.() -> Unit by LambdaDelegate<NetCacheCache, Moya.Builder>(
    NetCacheCache()
) { cache, builder ->
    builder.cacheNetWorkTimeOut(cache.networkTimeOut)
    builder.cacheNoNetWorkTimeOut(cache.noNetworkTimeOut)
    builder.cacheSize(cache.size)
    builder.cachePath(cache.path)
    builder.cacheType(cache.type)
}





