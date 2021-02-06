package com.allens.simple_coroutines

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.allens.moya.request.DownLoadRequest


class MyAdapter(
    private val mData: List<DownLoadRequest>,
    private val mRecyclerView: RecyclerView
) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    override fun getItemCount(): Int {
        return mData.size
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.btnStart.setOnClickListener {
            if (holder.btnStart.text == "开始")
                onBtnClickListener?.onItemClickStart(mData[position])
            else
                onBtnClickListener?.onItemClickPause(mData[position])
        }

        holder.btnCancel.setOnClickListener {
            onBtnClickListener?.onItemClickCancel(mData[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_download, parent, false)
        return MyViewHolder(view)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tvStatus: TextView = itemView.findViewById(R.id.tv_waiting)
        var btnStart: Button = itemView.findViewById(R.id.bt_pause)
        var btnCancel: Button = itemView.findViewById(R.id.bt_cancel)

    }


    private var onBtnClickListener: OnBtnClickListener? = null

    interface OnBtnClickListener {
        fun onItemClickStart(request: DownLoadRequest)
        fun onItemClickPause(request: DownLoadRequest)
        fun onItemClickCancel(request: DownLoadRequest)
    }

    fun setOnBtnClickListener(listener: OnBtnClickListener) {
        onBtnClickListener = listener
    }

    fun setDownLoadError(key: String, throwable: Throwable) {
        getChildAt(key)?.findViewById<TextView>(R.id.tv_waiting)?.text = "下载失败"
        getChildAt(key)?.findViewById<TextView>(R.id.bt_pause)?.text = "开始"
    }


    private fun getChildAt(key: String): View? {
        for ((index, data) in mData.withIndex()) {
            if (data.tag ?: data.url == key) {
                return mRecyclerView.getChildAt(index)
            }
        }
        return null
    }

    fun setDownLoadSuccess(key: String, path: String) {
        getChildAt(key)?.findViewById<TextView>(R.id.tv_waiting)?.text = "下载成功"
        getChildAt(key)?.findViewById<TextView>(R.id.bt_pause)?.text = "开始"
    }

    @SuppressLint("SetTextI18n")
    fun setDownLoadProgress(
        key: String,
        progress: Int,
        read: String,
        count: String,
        done: Boolean
    ) {
        getChildAt(key)?.findViewById<TextView>(R.id.bt_pause)?.text = "暂停"
        getChildAt(key)?.findViewById<TextView>(R.id.tv_size)?.text = "$read/$count"
        getChildAt(key)?.findViewById<TextView>(R.id.tv_waiting)?.text = "正在下载"
        getChildAt(key)?.findViewById<TextView>(R.id.tv_progress)?.text = "$progress%"
        getChildAt(key)?.findViewById<ProgressBar>(R.id.progress_bar)?.progress = progress

    }

    fun setDownLoadPause(key: String) {
        getChildAt(key)?.findViewById<TextView>(R.id.tv_waiting)?.text = "已暂停"
        getChildAt(key)?.findViewById<TextView>(R.id.bt_pause)?.text = "开始"
    }

    fun setDownLoadCancel(key: String) {
        getChildAt(key)?.findViewById<TextView>(R.id.tv_waiting)?.text = "已取消"
        getChildAt(key)?.findViewById<TextView>(R.id.bt_pause)?.text = "开始"
    }

    fun setDownLoadPrepare(key: String) {
        getChildAt(key)?.findViewById<TextView>(R.id.tv_waiting)?.text = "等待中..."
        getChildAt(key)?.findViewById<TextView>(R.id.bt_pause)?.text = "暂停"
    }
}