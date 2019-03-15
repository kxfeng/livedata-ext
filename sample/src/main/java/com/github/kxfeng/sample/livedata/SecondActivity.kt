package com.github.kxfeng.sample.livedata

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.github.kxfeng.livedata.observeFreshly
import kotlinx.android.synthetic.main.activity_second.*

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        App.LIVE_DATA.observe(this, Observer {
            val text = "SecondActivity receive $it by normal"
            Log.d(App.LOG_TAG, text)
            tv_info.append("$text\n")
        })

        App.LIVE_DATA.observeFreshly(this, Observer {
            val text = "SecondActivity receive $it by fresh"
            Log.d(App.LOG_TAG, text)
            tv_info.append("$text\n")
        })

        btn_change_data.setOnClickListener {
            App.LIVE_DATA.value = App.currentTime()
        }
    }
}