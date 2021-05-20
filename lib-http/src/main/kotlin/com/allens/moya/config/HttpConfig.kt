package com.allens.moya.config

import android.util.Log
import com.allens.moya.enums.HttpCacheType
import com.allens.moya.enums.HttpLevel
import com.allens.moya.enums.HttpNetWorkType
import com.allens.moya.impl.OnCookieInterceptor
import com.allens.moya.impl.OnLogInterceptor
import okhttp3.Interceptor
import retrofit2.CallAdapter
import retrofit2.Converter
import java.util.concurrent.TimeUnit


//RxHttp 配置
class HttpConfig {
    companion object{
        var DEBUG = true
    }
    //baseUrl
    var baseUrl: String = ""
    //连接超时等
    var connectTime: Long = DefaultConfig.DEFAULT_TIME_OUT
    var connectTimeTimeUnit = TimeUnit.SECONDS
    var readTime: Long = DefaultConfig.DEFAULT_TIME_OUT
    var readTimeTimeUnit = TimeUnit.SECONDS
    var writeTime: Long = DefaultConfig.DEFAULT_TIME_OUT
    var writeTimeTimeUnit = TimeUnit.SECONDS
    //是否重试
    var retryOnConnectionFailure: Boolean = true
    //日志的级别
    var level: HttpLevel = HttpLevel.BODY
    //日志的拦截器
    var logSet = mutableSetOf<OnLogInterceptor>()
    //cookie的拦截器
    var cookieSet = mutableSetOf<OnCookieInterceptor>()
    //自定义的拦截器
    var interceptor = mutableSetOf<Interceptor>()
    //配置通用的请求头
    var heardMap: HashMap<String, String> = HashMap()
    //添加自定的转换器
    var converterFactorySet = mutableSetOf<Converter.Factory>()
    //添加自定义的适配器
    var callAdapterFactorySet = mutableSetOf<CallAdapter.Factory>()
    //有网时候的缓存策略 默认无缓存策略
    var cacheNetWorkType = HttpNetWorkType.NONE
    //无网时候的缓存策略 默认无缓存策略
    var cacheNoNewWorkType = HttpCacheType.NONE
    //有网时:特定时间之后请求数据；（比如：特定时间为20s） 默认20
    var cacheNetworkTimeOut = DefaultConfig.DEFAULT_CACHE_NETWORK_TIMEOUT
    //无网时:特定时间之前请求有网请求好的数据；（（比如：特定时间为30天） 默认30 天  单位（秒）
    var cacheNoNetworkTimeOut = DefaultConfig.DEFAULT_NO_CACHE_NETWORK_TIMEOUT
    //缓存大小  10M
    var cacheSize = DefaultConfig.DEFAULT_CACHE_SIZE
    //缓存位置
    var cachePath = DefaultConfig.DEFAULT_CACHE_PATH
}





