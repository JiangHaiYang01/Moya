package com.allens.moya.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.lang.reflect.Field


//使用 反射的方式 将所有的的变化全部返回出去。
//因为livedata 导致的状态被刷新掉 而不能够接收到完整的进度  90% 就成功 但是下载进度不对
class EventLiveData<T> : MutableLiveData<T>() {

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, observer)
        hook(observer)
    }

    private fun hook(observer: Observer<in T>) {
        //get wrapper's version
        val classLiveData = LiveData::class.java
        val fieldObservers: Field = classLiveData.getDeclaredField("mObservers")
        fieldObservers.isAccessible = true
        val objectObservers = fieldObservers.get(this)
        val classObservers: Class<*> = objectObservers.javaClass
        val methodGet = classObservers.getDeclaredMethod("get", Any::class.java)
        methodGet.isAccessible = true
        val objectWrapperEntry = methodGet.invoke(objectObservers, observer)
        var objectWrapper: Any? = null
        if (objectWrapperEntry is Map.Entry<*, *>) {
            objectWrapper = objectWrapperEntry.value
        }
        if (objectWrapper == null) {
            println("Wrapper can not be bull!")
            return
        }
        val classObserverWrapper: Class<*> = objectWrapper.javaClass.superclass
        val fieldLastVersion: Field = classObserverWrapper.getDeclaredField("mLastVersion")
        fieldLastVersion.isAccessible = true
        //get livedata's version
        val fieldVersion: Field = classLiveData.getDeclaredField("mVersion")
        fieldVersion.isAccessible = true
        val objectVersion = fieldVersion.get(this)
        //set wrapper's version
        fieldLastVersion.set(objectWrapper, objectVersion)
    }
}


fun <T> EventLiveData<T>.observeEvent(owner: LifecycleOwner, observer: Observer<in T>) {
    observe(owner, observer)
}

fun <T> EventLiveData<T>.observeForeverEvent(observer: Observer<in T>) {
    observeForever(observer)
}