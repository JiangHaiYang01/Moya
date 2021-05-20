package com.allens.moya.interceptor

import com.allens.moya.tools.MoyaLogTool
import okhttp3.Interceptor
import okhttp3.Request

//请求头
object HeardInterceptor {
    fun register(map: Map<String, String>): Interceptor {
        return Interceptor { chain: Interceptor.Chain ->
            val request = chain.request()
            val builder: Request.Builder = request.newBuilder()
            for ((key, value) in map.entries) {
                MoyaLogTool.i( "add heard [key]:$key [value]:$value ")
                builder.addHeader(key, value)
            }
            chain.proceed(builder.build())
        }
    }
}

