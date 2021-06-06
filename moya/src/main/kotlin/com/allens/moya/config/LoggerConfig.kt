package com.allens.moya.config

import com.allens.moya.dslMarker.LoggerMarker
import com.allens.moya.enums.LoggerLevel
import com.allens.moya.impl.OnLogInterceptor


@LoggerMarker
data class LoggerConfig(
    var level: LoggerLevel = DefaultConfig.DEFAULT_LEVEL,
    var debug: Boolean = DefaultConfig.DEFAULT_DEBUG,
    var interceptors: Set<OnLogInterceptor>? = null

)
