package com.allens.moya.interceptor

import android.os.Handler
import com.allens.moya.config.HttpConfig
import com.allens.moya.enums.HttpLevel
import com.allens.moya.tools.MoyaLogTool
import okhttp3.logging.HttpLoggingInterceptor

/**
 *
 * @Description:
 * @Author:         Allens
 * @CreateDate:     2019-11-22 14:02
 * @Version:        1.0
 */

//日志拦截器
object LogInterceptor {
    fun register(config: HttpConfig, handler: Handler): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                MoyaLogTool.i(message)
                handler.post {
                    config.logSet.forEach {
                        it.onLogInterceptorInfo(message)
                    }
                }
            }
        })
        interceptor.level = HttpLevel.conversion(config.level)
        return interceptor
    }
}




