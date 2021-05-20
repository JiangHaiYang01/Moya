package com.allens.moya.enums

enum class HttpNetWorkType {
    //不加入缓存的逻辑
    NONE,

    //有网络时候 实时加载
    NOCACHE,

    //特定时间之后请求数据；（比如：特定时间为20s）
    CACHE_TIME,
}
