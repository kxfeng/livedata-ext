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

/**
 * Observe freshly, which means the observer will only receive new value after now, previous value won't dispatch to it.
 *
 * When you observe your observer by this method, if you want to manually remove the observer from this LiveData, you must
 * use [removeObserverFreshly] instead of using [LiveData.removeObserver] directly. Or you can let LiveData automatically
 * remove your observer when the LifecycleOwner destroy.
 *
 * @param owner The LifecycleOwner which controls the observer
 * @param observer The observer that will receive the events
 * @param skipPendingValue Skip value which has not arrived the LiveData, but the value was triggered before. For example
 * a MediatorLiveData returned from [androidx.lifecycle.Transformations.map], when it has none active observers, change
 * value of it's source LiveData, the value of this MediatorLiveData will still be it's previous value, the mapped new
 * value will arrive the MediatorLiveData until it has a active observer. This method treat this kind of new value as not
 * fresh value by default, but you can set this parameter to false to treat it as fresh value.
 */
@JvmOverloads
fun <T> LiveData<T>.observeFreshly(owner: LifecycleOwner, observer: Observer<in T>, skipPendingValue: Boolean = true) {
    if (skipPendingValue && this.javaClass !in SIMPLE_LIVE_DATA_TYPES && !hasActiveObservers()) {
        this.observeForever(FETCH_LATEST_VERSION_OBSERVER)
    }
    val sinceVersion = this.version()
    this.observe(owner, FreshObserver<T>(observer, FreshVersionPredicate(this, sinceVersion)))
}

/**
 * Observe forever freshly, which means the observer will only receive new value after now, previous value won't dispatch to it.
 *
 * When you observe your observer by this method, if you want to remove the observer from this LiveData, you must use
 * [removeObserverFreshly] instead of using [LiveData.removeObserver] directly.
 *
 * @param observer The observer that will receive the events
 * @param skipPendingValue See doc in [observeFreshly]
 *
 */
@JvmOverloads
fun <T> LiveData<T>.observeForeverFreshly(observer: Observer<in T>, skipPendingValue: Boolean = true) {
    if (skipPendingValue && this.javaClass !in SIMPLE_LIVE_DATA_TYPES && !hasActiveObservers()) {
        this.observeForever(FETCH_LATEST_VERSION_OBSERVER)
    }
    val sinceVersion = this.version()
    this.observeForever(FreshObserver<T>(observer, FreshVersionPredicate(this, sinceVersion)))
}

/**
 * Removes the observer which has been previously observed by [observeFreshly] or [observeForeverFreshly].
 */
fun <T> LiveData<T>.removeObserverFreshly(observer: Observer<in T>) {
    this.removeObserver(FreshObserver<T>(observer, TRUE_PREDICATE))
}

internal class FreshVersionPredicate(private val liveData: LiveData<*>, private val sinceVersion: Int) : FreshPredicate {
    override fun isFresh(): Boolean {
        return liveData.version() > sinceVersion
    }
}