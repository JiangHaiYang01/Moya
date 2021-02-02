package com.allens.simple_coroutines

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.allens.moya.Moya
import com.allens.moya_coroutines.request.doBody
import com.allens.moya_coroutines.request.doGet
import com.allens.simple_coroutines.bean.TestBean
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {

    override fun doCreate() {
        addButton("常规使用") {
            startActivity(Intent(this, TestActivity::class.java))
        }
        addButton("下载") {
            startActivity(Intent(this, DownLoadActivity::class.java))
        }
    }
}