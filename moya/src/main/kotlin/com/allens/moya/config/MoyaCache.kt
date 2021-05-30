package com.allens.moya.config

import com.allens.moya.dslMarker.CacheTagMarker
import com.allens.moya.enums.HttpCacheType
import com.allens.moya.enums.HttpNetWorkType



@CacheTagMarker
data class MoyaCache(
    var cacheNetWorkType: HttpNetWorkType = HttpNetWorkType.NONE,
    //无网时候的缓存策略 默认无缓存策略
    var cacheNoNewWorkType: HttpCacheType = HttpCacheType.NONE,
    //有网时:特定时间之后请求数据；（比如：特定时间为20s） 默认20
    var cacheNetworkTimeOut: Int = DefaultConfig.DEFAULT_CACHE_NETWORK_TIMEOUT,
    //无网时:特定时间之前请求有网请求好的数据；（（比如：特定时间为30天） 默认30 天  单位（秒）
    var cacheNoNetworkTimeOut: Int = DefaultConfig.DEFAULT_NO_CACHE_NETWORK_TIMEOUT,
    //缓存大小  10M
    var cacheSize: Int = DefaultConfig.DEFAULT_CACHE_SIZE,
    //缓存位置
    var cachePath: String = DefaultConfig.DEFAULT_CACHE_PATH,
)