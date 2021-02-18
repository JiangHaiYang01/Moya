package com.allens.simple_coroutines

import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.allens.moya.tools.FileTool
import com.allens.moya_coroutines.request.doDownLoadPauseAll
import com.allens.moya_coroutines.request.doUpLoad
import com.allens.simple_coroutines.bean.TestBean
import java.io.File

class UploadAct : BaseActivity(R.layout.activity_upload), UploadAdapter.OnBtnClickListener {

    private lateinit var myAdapter: UploadAdapter
    private lateinit var mRecyclerView: RecyclerView
    private val upLoadList = mutableListOf<UpLoadInfo>()

    override fun doCreate() {
        mRecyclerView = findViewById(R.id.recycler)
        myAdapter = UploadAdapter(upLoadList, mRecyclerView)
        mRecyclerView.adapter = myAdapter
        findViewById<View>(R.id.btn_select_files).setOnClickListener {
            upLoadList.clear()
            File(getBasePath())
                .walk()
                .maxDepth(1)
                .filter { it.isFile }
                .forEach {
                    println("当前选择的文件 file name ${it.name}")
                    upLoadList.add(
                        UpLoadInfo(
                            it.absolutePath,
                            it.absolutePath + "_" + "taskId"
                        )
                    )
                }
            myAdapter.notifyDataSetChanged()
            myAdapter.setOnBtnClickListener(this)
        }
    }



    override fun onItemClickStart(info: UpLoadInfo) {
        moya.create()
            .file("file", File(info.path))
            .lifecycle(this)
            .baseUrl("https://imgkr.com/")
            .heard("Referer","https://imgkr.com/")
            .doUpLoad<String>("api/v2/files/upload") {
                onSuccess = {
                    println("上传成功 $it")
                    myAdapter.uploadSuccess(info.taskId, it)
                }
                onError = { myAdapter.uploadFailed(info.taskId, it) }
                onProgress = { progress: Int, current: Long, length: Long ->
                    myAdapter.uploadProgress(
                        info.taskId,
                        progress,
                        FileTool.bytes2kb(current),
                        FileTool.bytes2kb(length)
                    )
                }
            }
    }

    override fun onItemClickCancel(info: UpLoadInfo) {
    }
}

data class UpLoadInfo(val path: String, val taskId: String)