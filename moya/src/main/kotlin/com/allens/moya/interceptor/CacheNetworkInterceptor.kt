package com.allens.moya.interceptor

import com.allens.moya.config.MoyaConfig
import com.allens.moya.enums.NetWorkCacheType
import com.allens.moya.tools.MoyaLogTool
import okhttp3.Interceptor
import okhttp3.Response

internal class CacheNetworkInterceptor(private val config: MoyaConfig) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val time = when (config.cacheNetWorkType) {
            NetWorkCacheType.NOCACHE -> {
                0
            }
            NetWorkCacheType.CACHE_TIME -> {
                config.cacheNetworkTimeOut
            }
            else -> {
                0
            }
        }
        MoyaLogTool.i(
            "--> 缓存配置(有网络连接):" + if (time == 0) {
                "每次都请求实时数据"
            } else {
                "$time 秒之后请求数据"
            }
        )
        return chain.proceed(chain.request()).newBuilder()
            // 清除头信息 因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
            .removeHeader("Pragma")
            .addHeader("Cache-Control", "max-age=$time")
            .build()
    }

}
