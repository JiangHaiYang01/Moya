package com.allens.moya.livedata

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.allens.moya.tools.MoyaLogTool

//解决LiveData 粘性问题
//参考 @see https://github.com/Flywith24/WrapperLiveData
typealias EventLiveData<T> = LiveData<T>

//此方法可以解决LiveData粘性问题，确保了事件一定会被change
@MainThread
inline fun <T> EventLiveData<T>.observeEvent(
    owner: LifecycleOwner,
    crossinline onChanged: (T) -> Unit
): Observer<T> {
    val observer = Observer<T> { t ->
        onChanged.invoke(t)
    }
    observe(owner, observer)
    return observer
}

//此方法可以解决LiveData粘性问题，确保了事件一定会被change
@MainThread
inline fun <T> EventLiveData<T>.observeForeverEvent(
    crossinline onChanged: (T) -> Unit
): Observer<T> {
    val observer = Observer<T> { t ->
        onChanged.invoke(t)
    }
    observeForever(observer)
    return observer
}
