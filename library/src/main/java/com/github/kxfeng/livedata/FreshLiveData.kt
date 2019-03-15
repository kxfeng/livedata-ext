package com.github.kxfeng.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataHiddenApi.version
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

private val SIMPLE_LIVE_DATA_TYPES = arrayOf(LiveData::class.java, MutableLiveData::class.java)
private val FETCH_LATEST_VERSION_OBSERVER = Observer<Any?> { }
private val TRUE_PREDICATE = object : FreshPredicate {
    override fun isFresh(): Boolean {
        return true
    }
}

fun <T> LiveData<T>.observeFreshly(owner: LifecycleOwner, observer: Observer<in T>) {
    if (this.javaClass !in SIMPLE_LIVE_DATA_TYPES && !hasActiveObservers()) {
        // For some LiveData class, for example MediatorLiveData, when it has none active observers, it's current version
        // may be less than the version it should be. After observeForever a observer, the latest version it should be
        // will be available.
        this.observeForever(FETCH_LATEST_VERSION_OBSERVER)
    }
    val sinceVersion = this.version()
    this.observe(owner, FreshObserver<T>(observer, FreshVersionPredicate(this, sinceVersion)))
}

fun <T> LiveData<T>.observeForeverFreshly(observer: Observer<in T>) {
    if (this.javaClass !in SIMPLE_LIVE_DATA_TYPES && !hasActiveObservers()) {
        this.observeForever(FETCH_LATEST_VERSION_OBSERVER)
    }
    val sinceVersion = this.version()
    this.observeForever(FreshObserver<T>(observer, FreshVersionPredicate(this, sinceVersion)))
}

fun <T> LiveData<T>.removeObserverFreshly(observer: Observer<in T>) {
    this.removeObserver(FreshObserver<T>(observer, TRUE_PREDICATE))
}

internal class FreshVersionPredicate(private val liveData: LiveData<*>, private val sinceVersion: Int) : FreshPredicate {
    override fun isFresh(): Boolean {
        return liveData.version() > sinceVersion
    }
}