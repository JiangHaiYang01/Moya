package com.allens.moya.request

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.allens.moya.enums.DynamicHeard
import com.allens.moya.manager.HttpManager
import com.allens.moya.tools.MoyaLogTool
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.HashMap
import java.util.concurrent.TimeUnit

class Request {
    class Builder constructor(val manager: HttpManager) {
        val heard = HashMap<String, String>()
        val map = HashMap<String, Any>()
        val files = HashMap<String, ProgressRequestBody>()
        var owner: LifecycleOwner? = null
        var viewModel: ViewModel? = null

        //添加请求头
        fun heard(key: String, value: String) = apply {
            heard[key] = value
        }

        //添加请求参数
        fun parameter(key: String, value: Any) = apply {
            map[key] = value
        }

        fun lifecycle(lifecycle: LifecycleOwner?) = apply {
            this.owner = lifecycle
        }

        fun viewModel(viewModel: ViewModel) = apply {
            this.viewModel = viewModel
        }

        //动态切换请求的地址
        fun baseUrl(url: String) = apply {
            heard(DynamicHeard.DYNAMIC_URL, url)
        }

        //动态切换connect time
        fun connectTimeOut(timeout: Int, timeUnit: TimeUnit = TimeUnit.MILLISECONDS) = apply {
            heard(DynamicHeard.DYNAMIC_CONNECT_TIME_OUT, timeout.toString())
            heard(
                DynamicHeard.DYNAMIC_CONNECT_TIME_OUT_TimeUnit,
                DynamicHeard.timeUnitConvert(timeUnit).info
            )
        }

        //动态切换write time
        fun writeTimeOut(timeout: Int, timeUnit: TimeUnit = TimeUnit.MILLISECONDS) = apply {
            heard(DynamicHeard.DYNAMIC_WRITE_TIME_OUT, timeout.toString())
            heard(
                DynamicHeard.DYNAMIC_WRITE_TIME_OUT_TimeUnit,
                DynamicHeard.timeUnitConvert(timeUnit).info
            )
        }

        //动态切换read time
        fun readTimeOut(timeout: Int, timeUnit: TimeUnit = TimeUnit.MILLISECONDS) = apply {
            heard(DynamicHeard.DYNAMIC_READ_TIME_OUT, timeout.toString())
            heard(
                DynamicHeard.DYNAMIC_READ_TIME_OUT_TimeUnit,
                DynamicHeard.timeUnitConvert(timeUnit).info
            )
        }

        //添加上传的文件
        fun file(key: String, file: File) = apply {
            val fileBody: RequestBody =
                file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            files[key] = ProgressRequestBody(fileBody)
        }
    }
}