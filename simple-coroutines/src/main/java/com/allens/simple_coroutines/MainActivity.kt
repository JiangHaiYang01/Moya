package com.allens.simple_coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.allens.moya_coroutines.request.doBody
import com.allens.moya_coroutines.request.doGet
import com.allens.simple_coroutines.bean.TestBean
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {

    override fun doCreate() {
        addButton("Get") {
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
    }
}