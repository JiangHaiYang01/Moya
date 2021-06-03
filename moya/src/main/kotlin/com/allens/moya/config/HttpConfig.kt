package com.allens.moya.config

import com.allens.moya.enums.NoNetWorkCacheType
import com.allens.moya.enums.LoggerLevel
import com.allens.moya.enums.NetWorkCacheType
import com.allens.moya.impl.OnCookieInterceptor
import com.allens.moya.impl.OnLogInterceptor
import okhttp3.Interceptor
import retrofit2.CallAdapter
import retrofit2.Converter
import java.util.concurrent.TimeUnit


/***
 * Moya 网络配置
 *
 */
data class HttpConfig(

    // baseUrl
    var baseUrl: String = DefaultConfig.DEFAULT_BASE_URL,

    // 连接超时等
    var connectTime: Long = DefaultConfig.DEFAULT_TIME_OUT,
    var connectTimeTimeUnit: TimeUnit = TimeUnit.SECONDS,

    // 写超时
    var readTime: Long = DefaultConfig.DEFAULT_TIME_OUT,
    var readTimeTimeUnit: TimeUnit = TimeUnit.SECONDS,

    // 读超时
    var writeTime: Long = DefaultConfig.DEFAULT_TIME_OUT,
    var writeTimeTimeUnit: TimeUnit = TimeUnit.SECONDS,

    // 是否重试
    var retryOnConnectionFailure: Boolean = DefaultConfig.DEFAULT_RETRY,

    // 日志的级别
    var level: LoggerLevel = DefaultConfig.DEFAULT_LEVEL,

    // 日志的拦截器
    var logSet: MutableSet<OnLogInterceptor> = mutableSetOf(),

    // cookie的拦截器
    var cookieSet: MutableSet<OnCookieInterceptor> = mutableSetOf(),

    // 自定义的拦截器
    var interceptor: MutableSet<Interceptor> = mutableSetOf(),

    // 配置通用的请求头
    var heardMap: HashMap<String, String> = HashMap(),

    // 添加自定的转换器
    var converterFactorySet: MutableSet<Converter.Factory> = mutableSetOf(),

    // 添加自定义的适配器
    var callAdapterFactorySet: MutableSet<CallAdapter.Factory> = mutableSetOf(),

    // 有网时候的缓存策略 默认无缓存策略
    var cacheNetWorkType: NetWorkCacheType = NetWorkCacheType.NONE,

    // 无网时候的缓存策略 默认无缓存策略
    var cacheNoNewWorkType: NoNetWorkCacheType = NoNetWorkCacheType.NONE,

    // 有网时:特定时间之后请求数据；（比如：特定时间为20s） 默认20
    var cacheNetworkTimeOut: Int = DefaultConfig.DEFAULT_CACHE_NETWORK_TIMEOUT,

    // 无网时:特定时间之前请求有网请求好的数据；（（比如：特定时间为30天） 默认30 天  单位（秒）
    var cacheNoNetworkTimeOut: Int = DefaultConfig.DEFAULT_NO_CACHE_NETWORK_TIMEOUT,

    // 缓存大小  10M
    var cacheSize: Int = DefaultConfig.DEFAULT_CACHE_SIZE,

    // 缓存位置
    var cachePath: String = DefaultConfig.DEFAULT_CACHE_PATH,
)





