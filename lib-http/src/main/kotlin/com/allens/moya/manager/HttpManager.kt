package com.allens.moya.manager

import android.content.Context
import android.os.Environment
import android.os.Handler
import android.os.Looper
import com.allens.moya.interceptor.CacheNetworkInterceptor
import com.allens.moya.config.DefaultConfig
import com.allens.moya.config.HttpConfig
import com.allens.moya.enums.HttpNetWorkType
import com.allens.moya.interceptor.*
import com.allens.moya.tools.PrefTools
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.google.gson.Gson
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.io.File


class HttpManager {


    var handler = Handler(Looper.getMainLooper())
    val gson = Gson()
    lateinit var retrofit: Retrofit
    lateinit var retrofitDownLoad: Retrofit
    private lateinit var context: Context
    private lateinit var config: HttpConfig

    fun createManager(config: HttpConfig, context: Context): HttpManager  = apply{
        this.config = config
        this.context = context.applicationContext

        //初始化 MMKV
        PrefTools.init(context)

        val interceptor = LogInterceptor.register(config, handler)

        val okHttpBuilder = buildOkHttp(interceptor)
        retrofit = createRetrofit(config, okHttpBuilder)
        //下载和网络请求使用不同的retrofit 防止@Steaming 不起作用
        okHttpBuilder.interceptors().remove(interceptor)
        retrofitDownLoad = createRetrofit(config, okHttpBuilder)
    }


    private fun buildOkHttp(logInterceptor: Interceptor): OkHttpClient.Builder {
        val okHttpBuilder = OkHttpClient.Builder()

        val cookieJar =
            PersistentCookieJar(
                SetCookieCache(),
                SharedPrefsCookiePersistor(context)
            )
        //第三方库 管理 cookie
        okHttpBuilder.cookieJar(cookieJar)
        okHttpBuilder.connectTimeout(config.connectTime, config.connectTimeTimeUnit)
        okHttpBuilder.readTimeout(config.readTime, config.readTimeTimeUnit)
        okHttpBuilder.writeTimeout(config.writeTime, config.writeTimeTimeUnit)
        okHttpBuilder.retryOnConnectionFailure(config.retryOnConnectionFailure)


        //动态替换BaseURL
        okHttpBuilder.addInterceptor(DynamicBaseUrlInterceptor)
        //动态替换连接超时
        okHttpBuilder.addInterceptor(DynamicTimeoutInterceptor)
        //添加日志拦截器
        okHttpBuilder.addInterceptor(logInterceptor)
        val map = config.heardMap

        //添加请求头
        if (!map.isNullOrEmpty()) {
            okHttpBuilder.addInterceptor(HeardInterceptor.register(map))
        }

        //cookie 拦截器
        config.cookieSet.forEach {
            okHttpBuilder.addInterceptor(ReceivedCookieInterceptor.register(it))
        }

        //cache 缓存
        val cacheSize = config.cacheSize // 10 MiB
        val cache = Cache(
            File(
                if (config.cachePath.isEmpty()) {
                    getBasePath(context) + File.separator + DefaultConfig.DEFAULT_CACHE_PATH
                } else {
                    config.cachePath
                }
            ), cacheSize.toLong()
        )

        //添加自定义的拦截器
        config.interceptor.forEach {
            okHttpBuilder.addInterceptor(it)
        }

        //设置缓存
        if (config.cacheNetWorkType != HttpNetWorkType.NONE) {
            okHttpBuilder
                .addInterceptor(CacheInterceptor(context, config))
                .addNetworkInterceptor(CacheNetworkInterceptor(config))
                .cache(cache)
        }

        return okHttpBuilder
    }

    private fun createRetrofit(config: HttpConfig, okHttpBuilder: OkHttpClient.Builder): Retrofit {
        val retrofitBuilder = Retrofit.Builder()
        val client = retrofitBuilder
            .client(okHttpBuilder.build())

        this.config.callAdapterFactorySet.forEach {
            client.addCallAdapterFactory(it)
        }


        this.config.converterFactorySet.forEach {
            client.addConverterFactory(it)
        }
        return client
            .baseUrl(config.baseUrl)
            .build()
    }


    inline fun <reified T> getService(): T {
        return retrofit.create(T::class.java)
    }

    inline fun <reified T> getServiceWithOutLogInterceptor(): T {
        return retrofitDownLoad.create(T::class.java)
    }


    //获取更路径
    private fun getBasePath(context: Context): String {
        var p: String = Environment.getExternalStorageState()
        val f: File? = context.getExternalFilesDir(null)
        if (null != f) {
            p = f.absolutePath
        }
        return p
    }

}