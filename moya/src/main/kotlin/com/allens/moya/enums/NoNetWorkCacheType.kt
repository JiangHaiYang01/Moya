package com.allens.moya.enums

enum class NoNetWorkCacheType {
    // 不加入缓存的逻辑
    NONE,

    // 无限时请求有网请求好的数据；
    NO_TIMEOUT,

    // 特定时间之前请求有网请求好的数据；（（比如：特定时间为20s））
    HAS_TIMEOUT,
}
