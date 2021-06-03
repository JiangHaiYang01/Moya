package com.allens.moya.interceptor

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import com.allens.moya.config.MoyaConfig
import com.allens.moya.enums.NoNetWorkCacheType
import com.allens.moya.tools.MoyaLogTool
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.TimeUnit


internal class CacheInterceptor(private val context: Context, private val httpConfig: MoyaConfig) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val resp: Response
        val req: Request = if (isNetworkAvailable(context)) {
            chain.request()
                .newBuilder()
                .build()
        } else {
            // 无网络,检查*天内的缓存,即使是过期的缓存
            val time = when (httpConfig.cacheNoNewWorkType) {
                NoNetWorkCacheType.NO_TIMEOUT -> {
                    Integer.MAX_VALUE
                }
                NoNetWorkCacheType.HAS_TIMEOUT -> {
                    httpConfig.cacheNoNetworkTimeOut
                }
                else -> {
                    0
                }
            }
            MoyaLogTool.i(
                "--> 缓存配置(无网络连接):" + if (time != httpConfig.cacheNoNetworkTimeOut) {
                    "无限时请求有网请求好的数据"
                } else {
                    "$time 秒请求有网请求好的数据"
                }
            )
            chain.request().newBuilder()
                .cacheControl(
                    CacheControl.Builder()
                        .onlyIfCached()
                        .maxStale(time, TimeUnit.SECONDS)
                        .build()
                )
                .build()
        }
        resp = chain.proceed(req)
        return resp.newBuilder().build()
    }
}


// 判断是否连接
private fun isNetworkAvailable(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        isNetWorkAvailableM(context)
    } else {
        isNetWorkAvailableL(context)
    }
}


// 获取 ConnectivityManager
private fun getManager(context: Context): ConnectivityManager {
    return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
}

// 23以下 判断是否连接
private fun isNetWorkAvailableL(context: Context): Boolean {
    val manger = getManager(context)
    val info = manger.activeNetworkInfo
    return info != null && info.isConnected
}

// 23以上 判断是否连接
@RequiresApi(Build.VERSION_CODES.M)
private fun isNetWorkAvailableM(context: Context): Boolean {
    val connectivityManager = getManager(context)
    val networkCapabilities = connectivityManager.activeNetwork
    val actNw =
        connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
    return when {
        // wifi网络
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        // 蜂窝网络
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        // 以太网
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}