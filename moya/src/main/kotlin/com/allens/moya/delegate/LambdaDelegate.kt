package com.allens.moya.delegate

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
class LambdaDelegate<T, R>(val data: T, private val block: (T, R) -> Unit) :
    ReadWriteProperty<Any, T.() -> Unit> {
    override fun getValue(thisRef: Any, property: KProperty<*>): T.() -> Unit {
        return {}
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T.() -> Unit) {
        data.apply {
            value(this)
            block(this, thisRef as R)
        }
    }
}