package com.allens.moya.impl

interface OnCookieInterceptor {

    /***
     * 是否拦截所有方法的cookie
     */
    fun isInterceptorAllRequest(): Boolean {
        return false
    }

    /**
     * 拦截哪一个方法
     * [url] 会将url 返回作为鉴权的条件。开发者自行做逻辑示例
     */
    fun interceptorRequestWithUrl(url: String): Boolean

    /**
     * 拦截返回 cookie
     */
    fun onCookies(cookie: HashSet<String>)
}