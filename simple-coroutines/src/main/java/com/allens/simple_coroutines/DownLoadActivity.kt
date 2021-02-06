package com.allens.simple_coroutines

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.allens.moya.impl.OnDownLoadListener
import com.allens.moya.request.DownLoadRequest
import com.allens.moya.tools.toKB
import com.allens.moya_coroutines.request.doDownLoad
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class DownLoadViewModel : ViewModel() {

}

class DownLoadActivity : BaseActivity(), MyAdapter.OnBtnClickListener, OnDownLoadListener {
    private var data: MutableList<DownLoadInfo> = mutableListOf()
    private lateinit var myAdapter: MyAdapter

    private val downLoadViewModel by viewModels<DownLoadViewModel>()

    companion object {
        const val TAG = "TAG"
    }

    override fun doCreate() {
        val mRecyclerView = RecyclerView(this)
        linear.addView(mRecyclerView)


        val downloadUrl = listOf(
            "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-33-30.mp4",
            "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1714860920,1362844517&fm=26&gp=0.jpg",
            "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1516456790,1987034429&fm=26&gp=0.jpg"
        )

        for ((index, info) in downloadUrl.withIndex()) {
            data.add(
                DownLoadInfo(info, "$index", "key=$index")
            )
        }

        val mLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mRecyclerView.layoutManager = mLayoutManager
        myAdapter = MyAdapter(data, mRecyclerView)
        mRecyclerView.adapter = myAdapter
        myAdapter.setOnBtnClickListener(this)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (menu == null)
            return false
        menu.add(1, 1, 1, "全部开始")
        menu.add(1, 2, 2, "全部暂停")
        menu.add(1, 3, 2, "全部取消")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            1 -> {
//                for (info in data) {
//                    startDownLoad(info)
//                }
            }
            2 -> {
//                moya.create().doDownLoadPauseAll()
            }
            3 -> {
//                moya.create().doDownLoadCancelAll()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClickStart(info: DownLoadInfo) {
        launch {
            val request = DownLoadRequest.Builder()
                .name(info.saveName)
                .path(getBasePath())
                .tag(info.key)
                .listener(this@DownLoadActivity)
                .build(info.url)
            moya.create()
                //绑定LiveData 后台将不会在更新
//                .lifecycle(lifecycle = this@DownLoadActivity)
                .viewModel(downLoadViewModel)
                .doDownLoad(request)
        }
    }

    override fun onItemClickPause(downLoadInfo: DownLoadInfo) {
    }

    override fun onDownLoadPrepare(key: String) {
        Log.i(TAG, "准备下载 $key  thread ${Thread.currentThread().name}")
        myAdapter.setDownLoadPrepare(key)
    }


    override fun onDownLoadProgress(key: String, progress: Int) {
//        Log.i(TAG, "下载进度 $progress  thread ${Thread.currentThread().name}")

    }

    override fun onDownLoadError(key: String, throwable: Throwable) {
        Log.i(TAG, "下载失败 ${throwable.message}  thread ${Thread.currentThread().name}")
        myAdapter.setDownLoadError(key, throwable)
    }

    override fun onDownLoadSuccess(key: String, path: String) {
        Log.i(TAG, "下载成功 $key  thread ${Thread.currentThread().name}")
        myAdapter.setDownLoadSuccess(key, path)
    }

    override fun onDownLoadPause(key: String) {
        Log.i(TAG, "下载暂停 $key  thread ${Thread.currentThread().name}")
        myAdapter.setDownLoadPause(key)
    }

    override fun onDownLoadCancel(key: String) {
        Log.i(TAG, "下载取消 $key  thread ${Thread.currentThread().name}")
        myAdapter.setDownLoadCancel(key)
    }

    override fun onUpdate(key: String, progress: Int, read: Long, count: Long, done: Boolean) {
        Log.i(
            TAG,
            "下载进度 $key  progress:$progress read:$read  count:$count  done:$done thread:${Thread.currentThread().name}"
        )
        myAdapter.setDownLoadProgress(
            key,
            progress,
            read.toKB(),
            count.toKB(),
            done
        )
    }


}

data class DownLoadInfo(val url: String, val saveName: String, val key: String)