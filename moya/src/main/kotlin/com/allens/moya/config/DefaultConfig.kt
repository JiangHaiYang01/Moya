package com.allens.moya.config

import com.allens.moya.enums.HttpLevel

/**
 * 默认的配置参数
 */
internal class DefaultConfig {

    companion object {
        const val DEFAULT_CACHE_PATH = "cacheHttp"
        const val DEFAULT_BASE_URL = ""
        const val DEFAULT_CACHE_NETWORK_TIMEOUT = 20
        const val DEFAULT_NO_CACHE_NETWORK_TIMEOUT = 30 * 24 * 60 * 60
        const val DEFAULT_CACHE_SIZE = 10 * 1024 * 1024
        const val DEFAULT_TIME_OUT = 10L
        const val DEFAULT_RETRY = true
        val DEFAULT_LEVEL = HttpLevel.BODY
    }
}