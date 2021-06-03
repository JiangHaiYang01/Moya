package com.allens.moya.config

import com.allens.moya.dslMarker.CacheTagMarker
import com.allens.moya.enums.CacheType
import com.allens.moya.enums.NetWorkCacheType
import com.allens.moya.enums.NoNetWorkCacheType


@CacheTagMarker
data class NetWorkCache(

    // 缓存策略 默认不加入缓存的逻辑
    var type :CacheType = CacheType.NONE,

    // 有网时:特定时间之后请求数据；（比如：特定时间为20s） 默认20
    var networkTimeOut: Int = DefaultConfig.DEFAULT_CACHE_NETWORK_TIMEOUT,

    // 无网时:特定时间之前请求有网请求好的数据；（（比如：特定时间为30天） 默认30 天  单位（秒）
    var noNetworkTimeOut: Int = DefaultConfig.DEFAULT_NO_CACHE_NETWORK_TIMEOUT,

    // 缓存大小  10M
    var size: Int = DefaultConfig.DEFAULT_CACHE_SIZE,

    // 缓存位置
    var path: String = DefaultConfig.DEFAULT_CACHE_PATH,
)