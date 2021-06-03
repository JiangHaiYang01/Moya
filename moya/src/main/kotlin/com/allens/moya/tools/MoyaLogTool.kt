package com.allens.moya.tools

import android.util.Log
import com.allens.moya.Moya


object MoyaLogTool {

    fun i(info: String) {
        if (Moya.debug) {
            Log.i("moya-->", info)
        }
    }
}