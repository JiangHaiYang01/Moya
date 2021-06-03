package com.allens.moya.enums

import okhttp3.logging.HttpLoggingInterceptor

/**
 * 日志等级
 */
enum class LoggerLevel {
    NONE,
    BASIC,
    HEADERS,
    BODY;

    companion object {
        fun conversion(level: LoggerLevel): HttpLoggingInterceptor.Level {
            return when (level) {
                BODY -> HttpLoggingInterceptor.Level.BODY
                NONE -> HttpLoggingInterceptor.Level.NONE
                BASIC -> HttpLoggingInterceptor.Level.BASIC
                HEADERS -> HttpLoggingInterceptor.Level.HEADERS
            }
        }
    }
}
