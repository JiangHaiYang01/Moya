package com.allens.moya.tools

import androidx.lifecycle.ViewModel

class ClearedViewModel(val clear: () -> Unit) : ViewModel() {
    public override fun onCleared() {
        super.onCleared()
        clear()
    }
}

fun ClearedViewModel.sss(block: () -> Unit) {


}