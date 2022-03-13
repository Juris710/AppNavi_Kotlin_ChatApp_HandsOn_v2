package com.example.handsonchatapp

import android.view.View

class AdapterUtil {
    interface ListListener<T> {
        fun onClickItem(tappedView: View, messageItem: T)
    }
}