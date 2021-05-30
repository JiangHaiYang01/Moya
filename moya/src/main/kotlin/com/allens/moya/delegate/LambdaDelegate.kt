package com.allens.moya.delegate

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class LambdaDelegate<T>(val data: T) : ReadWriteProperty<Any, T.() -> Unit> {
    override fun getValue(thisRef: Any, property: KProperty<*>): T.() -> Unit {
        return {}
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T.() -> Unit) {
        data.apply { value(this) }
    }
}