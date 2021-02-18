package com.allens.moya.config

internal class DefaultConfig {

    companion object {
        const val DEFAULT_CACHE_PATH = "cacheHttp"
        const val DEFAULT_CACHE_NETWORK_TIMEOUT = 20
        const val DEFAULT_NO_CACHE_NETWORK_TIMEOUT = 30 * 24 * 60 * 60
        const val DEFAULT_CACHE_SIZE = 10 * 1024 * 1024
        const val DEFAULT_TIME_OUT = 10L
    }
}