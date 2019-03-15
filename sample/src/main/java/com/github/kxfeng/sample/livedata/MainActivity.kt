package com.github.kxfeng.sample.livedata

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.github.kxfeng.livedata.observeFreshly
import com.github.kxfeng.livedata.removeObserverFreshly
import com.github.kxfeng.sample.livedata.App.Companion.LOG_TAG
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val freshObserver = Observer<String> {
        val text = "MainActivity receive $it by fresh"
        Log.d(LOG_TAG, text)
        tv_info.append("$text\n")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        App.LIVE_DATA.observe(this, Observer {
            val text = "MainActivity receive $it by normal"
            Log.d(LOG_TAG, text)
            tv_info.append("$text\n")
        })

        App.LIVE_DATA.observeFreshly(this, freshObserver)

        btn_change_data.setOnClickListener {
            App.LIVE_DATA.value = App.currentTime()
        }

        btn_start_activity.setOnClickListener {
            startActivity(Intent(this, SecondActivity::class.java))
        }

        btn_observe.setOnClickListener {
            App.LIVE_DATA.observeFreshly(this, freshObserver)
        }

        btn_remove_observer.setOnClickListener {
            App.LIVE_DATA.removeObserverFreshly(freshObserver)
        }
    }
}
