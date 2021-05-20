package com.allens.moya.enums

import okhttp3.logging.HttpLoggingInterceptor


enum class HttpLevel {
    NONE,
    BASIC,
    HEADERS,
    BODY;

    companion object {
        fun conversion(level: HttpLevel): HttpLoggingInterceptor.Level {
            return when (level) {
                BODY -> HttpLoggingInterceptor.Level.BODY
                NONE -> HttpLoggingInterceptor.Level.NONE
                BASIC -> HttpLoggingInterceptor.Level.BASIC
                HEADERS -> HttpLoggingInterceptor.Level.HEADERS
            }
        }
    }
}
