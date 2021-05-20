package com.allens.tea

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.allens.moya.Moya
import com.allens.moya_coroutines.request.doGet

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}