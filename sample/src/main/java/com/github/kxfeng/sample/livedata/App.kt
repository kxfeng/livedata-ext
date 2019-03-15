package com.github.kxfeng.sample.livedata

import androidx.lifecycle.MutableLiveData
import java.text.SimpleDateFormat
import java.util.*

class App {
    companion object {
        val LIVE_DATA = MutableLiveData<String>()

        const val LOG_TAG = "LiveDataExt"

        fun currentTime(): String {
            return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        }
    }
}

