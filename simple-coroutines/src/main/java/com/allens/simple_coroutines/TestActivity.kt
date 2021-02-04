package com.allens.simple_coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.allens.moya.Moya
import com.allens.moya_coroutines.request.doBody
import com.allens.moya_coroutines.request.doGet
import com.allens.moya_coroutines.request.doGetBlock
import com.allens.simple_coroutines.bean.TestBean
import kotlinx.coroutines.launch

class TestActivity : BaseActivity() {

    override fun doCreate() {
        addButton("Get 依赖外部协程") {
            launch {
                moya.create()
                    .parameter("k", "java")
                    .doGet<String>("wxarticle/chapters/json")
                    .doFailed {
                        log(it.message ?: "error")
                    }
                    .doComplete {
                        log("complete")
                    }
                    .doSuccess {
                        log("success")
                    }
            }
        }
        addButton("Get 绑定lifeCycle") {
            moya.create()
                .parameter("k", "java")
                //必须绑定lifecycle
                .lifecycle(this)
                .doGetBlock<String>("wxarticle/chapters/json") {
                    it.doSuccess { log("success") }
                    it.doFailed { log("error") }
                    it.doComplete { log("complete") }
                }

            moya.create()
                .parameter("k", "java")
                //必须绑定lifecycle
                .lifecycle(this)
                .doGet<String>("wxarticle/chapters/json") {
                    onSuccess = {

                    }
                    onError = {

                    }
                    onComplete = {

                    }
                }
        }

        val viewModel = createViewModel(this, MainViewModel::class.java)
        lifecycle.addObserver(viewModel)
        addButton("Get 绑定ViewModel") {
            viewModel.doGet(moya)
            finish()
        }
    }
}
