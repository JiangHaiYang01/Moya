package com.allens.simple_coroutines

import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.allens.moya.impl.OnDownLoadListener
import com.allens.moya.request.DownLoadRequest
import com.allens.moya.tools.toKB
import com.allens.moya_coroutines.request.*
import kotlinx.coroutines.launch


class DownLoadActivity : BaseActivity(), MyAdapter.OnBtnClickListener, OnDownLoadListener {
    private var data: MutableList<DownLoadRequest> = mutableListOf()
    private lateinit var myAdapter: MyAdapter

//    private val downLoadViewModel by viewModels<DownLoadViewModel>()

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
                DownLoadRequest.Builder()
                    .name(".$index")
                    .path(getBasePath())
                    .tag("tag->$index")
                    //可以选择不用 listener 参考下面方式2
//                    .listener(this@DownLoadActivity)
                    .build(info)
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
                for (info in data) {
                    onItemClickStart(info)
                }
            }
            2 -> {
                moya.create().doDownLoadPauseAll()
            }
            3 -> {
                moya.create().doDownLoadCancelAll()
            }

        }
        return super.onOptionsItemSelected(item)
    }


    //方式1
    private fun type1(request: DownLoadRequest) {
        launch {
            moya.create()
                //绑定LiveData 后台将不会在更新
                .lifecycle(lifecycle = this@DownLoadActivity)
                .doDownLoad(request)
        }
    }

    private fun type2(request: DownLoadRequest) {
        launch {
            val tag = request.tag ?: request.url
            moya.create()
                .lifecycle(this@DownLoadActivity)
                .doDownLoad(request) {
                    onSuccess = {
                        myAdapter.setDownLoadSuccess(tag, it)
                    }
                    onCancel = {
                        myAdapter.setDownLoadCancel(tag)
                    }
                    onPause = {
                        myAdapter.setDownLoadPause(tag)
                    }
                    onUpdate = { progress, read, count, done ->
                        myAdapter.setDownLoadProgress(
                            tag,
                            progress,
                            read.toKB(),
                            count.toKB(),
                            done
                        )
                    }
                    onError = {
                        myAdapter.setDownLoadError(tag, it)
                    }
                    onPrepare = {
                        myAdapter.setDownLoadPrepare(tag)
                    }

                }
        }
    }

    override fun onItemClickStart(request: DownLoadRequest) {
//        type1(request)
        type2(request)
    }

    override fun onItemClickPause(request: DownLoadRequest) {
        moya.create().doPauseDownLoad(request)
    }

    override fun onItemClickCancel(request: DownLoadRequest) {
        moya.create().doCancelDownLoad(request)
    }

    override fun onDownLoadPrepare(key: String) {
        myAdapter.setDownLoadPrepare(key)
    }


    override fun onDownLoadProgress(key: String, progress: Int) {

    }

    override fun onDownLoadError(key: String, throwable: Throwable) {
        myAdapter.setDownLoadError(key, throwable)
    }

    override fun onDownLoadSuccess(key: String, path: String) {
        myAdapter.setDownLoadSuccess(key, path)
    }

    override fun onDownLoadPause(key: String) {
        myAdapter.setDownLoadPause(key)
    }

    override fun onDownLoadCancel(key: String) {
        myAdapter.setDownLoadCancel(key)
    }

    override fun onUpdate(key: String, progress: Int, read: Long, count: Long, done: Boolean) {
        myAdapter.setDownLoadProgress(
            key,
            progress,
            read.toKB(),
            count.toKB(),
            done
        )
    }


}
