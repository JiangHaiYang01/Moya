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
        addButton("测试") {
            startActivity(Intent(this, TestActivity::class.java))
        }
    }
}