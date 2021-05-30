package com.allens.moya

import android.content.Context
import android.util.Log
import com.allens.moya.config.MoyaCache
import com.allens.moya.config.MoyaTime
import com.allens.moya.delegate.LambdaDelegate
import com.allens.moya.enums.HttpLevel
import com.allens.moya.impl.OnLogInterceptor
import java.util.logging.Logger

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


var Moya.Builder.write: Long
    get() = httpConfig.writeTime
    set(value) {
        writeTimeout(value)
    }


var Moya.Builder.read: Long
    get() = httpConfig.readTime
    set(value) {
        readTimeout(value)
    }

var Moya.Builder.connect: Long
    get() = httpConfig.connectTime
    set(value) {
        connectTimeout(value)
    }


var Moya.Builder.time: MoyaTime.() -> Unit
    get() = { MoyaTime(connect, read, write) }
    set(value) {
        Log.i("TAG","value:$value")
        Log.i("TAG","connect:$connect")
//        value.invoke(this)
//        readTimeout(value)

    }

var Moya.Builder.retry: Boolean
    get() = httpConfig.retryOnConnectionFailure
    set(value) {
        retryOnConnectionFailure(value)
    }

var Moya.Builder.level: HttpLevel
    get() = httpConfig.level
    set(value) {
        logLevel(value)
    }


