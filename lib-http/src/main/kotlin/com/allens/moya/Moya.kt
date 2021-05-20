package com.allens.moya

import android.content.Context
import android.os.Debug
import com.allens.moya.config.HttpConfig
import com.allens.moya.enums.CacheType
import com.allens.moya.enums.HttpCacheType
import com.allens.moya.enums.HttpLevel
import com.allens.moya.enums.HttpNetWorkType
import com.allens.moya.impl.OnCookieInterceptor
import com.allens.moya.impl.OnLogInterceptor
import com.allens.moya.manager.HttpManager
import com.allens.moya.request.Request
import retrofit2.CallAdapter
import retrofit2.Converter
import java.util.concurrent.TimeUnit


class                                                                                                                                                                                                                                                           Moya {

    private lateinit var manager: HttpManager

    //创建一个新的请求
    fun create(): Request.Builder {
        return Request.Builder(manager)
    }

    class Builder {


        private val httpConfig = HttpConfig()

        /***
         * 配置通用的 超时时间
         * [time] 单位秒
         */
        fun writeTimeout(time: Long, timeUnit: TimeUnit = TimeUnit.SECONDS) = apply {
            httpConfig.writeTime = time
            httpConfig.writeTimeTimeUnit = timeUnit
        }

        /***
         * 配置通用的 超时时间
         * [time] 单位秒
         */
        fun readTimeout(time: Long, timeUnit: TimeUnit = TimeUnit.SECONDS) = apply {
            httpConfig.readTime = time
            httpConfig.readTimeTimeUnit = timeUnit

        }

        /***
         * 配置通用的 超时时间
         * [time] 单位秒
         */
        fun connectTimeout(time: Long, timeUnit: TimeUnit = TimeUnit.SECONDS) = apply {
            httpConfig.connectTime = time
            httpConfig.connectTimeTimeUnit = timeUnit

        }

        //是否重试
        fun retryOnConnectionFailure(retryOnConnectionFailure: Boolean) = apply {
            httpConfig.retryOnConnectionFailure = retryOnConnectionFailure

        }


        //日志级别
        fun level(level: HttpLevel) = apply {
            httpConfig.level = level

        }


        //添加日志组件，会在[OnLogInterceptorListener] 接口返回框架日志信息
        fun logInterceptor(logListener: OnLogInterceptor) = apply {
            httpConfig.logSet.add(logListener)

        }


        /***
         * 添加自定义的[Converter.Factory]
         */
        fun converterFactory(factory: Converter.Factory) = apply {
            httpConfig.converterFactorySet.add(factory)

        }

        /***
         * 添加自定义的[CallAdapter.Factory]
         */
        fun callAdapterFactory(factory: CallAdapter.Factory) = apply {
            httpConfig.callAdapterFactorySet.add(factory)

        }


        /***
         * 为所有请求都添加heard
         */
        fun head(key: String, value: String) = apply {
            httpConfig.heardMap[key] = value

        }

        //添加 cookie 拦截
        fun cookieInterceptor(
            onCookieInterceptor: OnCookieInterceptor
        ) = apply {
            httpConfig.cookieSet.add(onCookieInterceptor)

        }

        /***
         * 设置网络的请求的url,如果需要对某一个请求单独配置其他的url ,
         */
        fun baseUrl(url: String) = apply {
            httpConfig.baseUrl = url

        }


        /***
         *
         * 有网时:[time]秒之后请求数据  默认20秒
         * 缓存策略 选择
         * [CacheType.HAS_NETWORK_CACHE_TIME_AND_NO_NETWORK_NO_TIME]
         * [CacheType.HAS_NETWORK_CACHE_TIME_AND_NO_NETWORK_HAS_TIME]
         * 时候生效
         *
         * 实际上只是在请求的时候带上请求头 [max-age=time] max-age:最大缓存时间
         */
        fun cacheNetWorkTimeOut(time: Int) = apply {
            httpConfig.cacheNetworkTimeOut = time

        }

        /***
         * 无网时 特定时间之前  会将 有网时候请求到的数据 返回
         *
         * 默认是30天。
         * [time] 单位 秒
         */
        fun cacheNoNetWorkTimeOut(time: Int) = apply {
            httpConfig.cacheNoNetworkTimeOut = time

        }

        /***
         * 缓存的大小，默认 10M
         * [size] 单位 byte
         */
        fun cacheSize(size: Int) = apply {
            httpConfig.cacheSize = size

        }

        /**
         * 网络缓存的位置
         * 默认位置 沙盒位置/cacheHttp
         */
        fun cachePath(path: String) = apply {
            httpConfig.cachePath = path

        }

        /***
         * 缓存策略 默认无缓存策略
         */
        fun cacheType(type: CacheType) = apply {
            when (type) {
                CacheType.HAS_NETWORK_NOCACHE_AND_NO_NETWORK_NO_TIME -> {
                    httpConfig.cacheNetWorkType = HttpNetWorkType.NOCACHE
                    httpConfig.cacheNoNewWorkType = HttpCacheType.NO_TIMEOUT
                }
                CacheType.HAS_NETWORK_CACHE_TIME_AND_NO_NETWORK_NO_TIME -> {
                    httpConfig.cacheNetWorkType = HttpNetWorkType.CACHE_TIME
                    httpConfig.cacheNoNewWorkType = HttpCacheType.NO_TIMEOUT
                }
                CacheType.HAS_NETWORK_NOCACHE_AND_NO_NETWORK_HAS_TIME -> {
                    httpConfig.cacheNetWorkType = HttpNetWorkType.NOCACHE
                    httpConfig.cacheNoNewWorkType = HttpCacheType.HAS_TIMEOUT
                }
                CacheType.HAS_NETWORK_CACHE_TIME_AND_NO_NETWORK_HAS_TIME -> {
                    httpConfig.cacheNetWorkType = HttpNetWorkType.CACHE_TIME
                    httpConfig.cacheNoNewWorkType = HttpCacheType.HAS_TIMEOUT
                }
                CacheType.NONE -> {
                    httpConfig.cacheNetWorkType = HttpNetWorkType.NONE
                    httpConfig.cacheNoNewWorkType = HttpCacheType.NONE
                }
            }
        }

        //是否是debug. debug 会显示日志
        //这里需要注意 如果项目中只有一个 Moya 无所谓
        //由多个的化 需要对每个moya 都单独配置一下。因为debug 是一个静态属性
        fun debug(debug: Boolean) = apply {
            HttpConfig.DEBUG = debug
        }

        fun build(context: Context): Moya {
            return Moya().apply {
                manager = HttpManager().createManager(httpConfig, context)
            }
        }
    }
}