package com.allens.simple_coroutines

import com.allens.moya.Moya
import com.allens.moya.enums.CacheType
import com.allens.moya.enums.HttpLevel
import com.allens.moya.impl.OnCookieInterceptor
import com.allens.moya.impl.OnLogInterceptor
import com.allens.moya_coroutines.request.doBody
import com.allens.moya_coroutines.request.doGet
import com.allens.moya_coroutines.request.doPost
import java.util.concurrent.TimeUnit

class ConfigAct : BaseActivity() {
    override fun doCreate() {
        addButton("连接超时") {
            val moya = Moya.Builder()
                .debug(true)
                .baseUrl(BASEURL)
                .connectTimeout(1, TimeUnit.MILLISECONDS)
                .writeTimeout(2)
                .readTimeout(2)
                .build(this)
            todoTest(moya)
        }
        addButton("日志级别") {
            val moya = Moya.Builder()
                .debug(true)
                .baseUrl(BASEURL)
                .level(HttpLevel.BASIC)
                .build(this)
            todoTest(moya)
        }
        addButton("通用请求头") {
            val moya = Moya.Builder()
                .debug(true)
                .baseUrl(BASEURL)
                .head("type", "moya")
                .head("code", "allens")
                .build(this)
            todoTest(moya)
        }
        addButton("日志打印") {
            val moya = Moya.Builder()
                .baseUrl(BASEURL)
                //将库内的日志关闭。方便演示
                .debug(false)
                .logInterceptor(object : OnLogInterceptor {
                    override fun onLogInterceptorInfo(message: String) {
                        println("configAct->$message")
                    }
                })
                .build(this)
            todoTest(moya)
        }
        addButton("cookie") {
            val moya = Moya.Builder()
                .baseUrl(BASEURL)
                .debug(false)
                .level(HttpLevel.HEADERS)
                .cookieInterceptor(object : OnCookieInterceptor {
                    override fun isInterceptorAllRequest(): Boolean {
                        return false
                    }

                    override fun isInterceptorRequest(url: String): Boolean {
                        return url == "https://www.wanandroid.com/user/register"
                    }

                    override fun onCookies(cookie: HashSet<String>) {
                        println("打印所有的cookie")
                        cookie.forEach {
                            println(it)
                        }
                    }
                })
                .build(this)
            todoTest(moya)
        }
        addButton("缓存策略") {
            val moya = Moya.Builder()
                .baseUrl(BASEURL)
                .debug(true)
                .cacheType(CacheType.HAS_NETWORK_NOCACHE_AND_NO_NETWORK_NO_TIME)
                .build(this)
            todoTest(moya)
        }
    }

    private fun todoTest(moya: Moya) {
        moya.create()
            .parameter("username", "moya")
            .parameter("password", "123456")
            .parameter("repassword", "123456")
            .lifecycle(this)
            .doPost<String>("user/register") {
                onSuccess = {
                    log("success")
                    toast("success")
                }
                onError = {
                    log("error")
                    toast("error ${it.message}")
                }
                onComplete = {
                    log("complete")
                }
            }
    }
}