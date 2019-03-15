package com.github.kxfeng.livedata

import androidx.lifecycle.Observer

/**
 * A wrapped observer which will only send changed data to delegate when predicate is true.
 * If two [delegate] are equal, their wrapped FreshObserver are ensured to be equal.
 */
internal class FreshObserver<T>(
    private val delegate: Observer<in T>,
    private val predicate: FreshPredicate
) : Observer<T> {

    override fun onChanged(t: T) {
        if (predicate.isFresh()) {
            delegate.onChanged(t)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as FreshObserver<*>
        if (delegate != other.delegate) return false
        return true
    }

    override fun hashCode(): Int {
        return delegate.hashCode()
    }
}

internal interface FreshPredicate {
    fun isFresh(): Boolean
}