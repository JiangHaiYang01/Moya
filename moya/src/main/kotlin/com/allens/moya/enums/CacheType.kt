package com.allens.moya.enums

enum class CacheType {
    //不加入缓存的逻辑
    NONE,

    //有网时:每次都请求实时数据； 无网时:无限时请求有网请求好的数据；
    HAS_NETWORK_NOCACHE_AND_NO_NETWORK_NO_TIME,

    //有网时:特定时间之后请求数据； 无网时:无限时请求有网请求好的数据；
    HAS_NETWORK_CACHE_TIME_AND_NO_NETWORK_NO_TIME,

    //有网时:每次都请求实时数据； 无网时:特定时间之前请求有网请求好的数据；
    HAS_NETWORK_NOCACHE_AND_NO_NETWORK_HAS_TIME,

    //有网时:特定时间之后请求数据； 无网时:特定时间之前请求有网请求好的数据；
    HAS_NETWORK_CACHE_TIME_AND_NO_NETWORK_HAS_TIME,
}