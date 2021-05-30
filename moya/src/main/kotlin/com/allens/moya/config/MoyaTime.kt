package com.allens.moya.config

import com.allens.moya.dslMarker.TimeTagMarker

@TimeTagMarker
data class MoyaTime(
    var connect: Long = DefaultConfig.DEFAULT_TIME_OUT,
    var write: Long = DefaultConfig.DEFAULT_TIME_OUT,
    var read: Long = DefaultConfig.DEFAULT_TIME_OUT,
)
