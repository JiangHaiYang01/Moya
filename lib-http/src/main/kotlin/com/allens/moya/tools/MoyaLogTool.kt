package com.allens.moya.tools

import android.util.Log
import com.allens.moya.config.HttpConfig


object MoyaLogTool {

    fun i(info: String) {
        if (HttpConfig.DEBUG)
            Log.i("moya-->", info)
    }
}