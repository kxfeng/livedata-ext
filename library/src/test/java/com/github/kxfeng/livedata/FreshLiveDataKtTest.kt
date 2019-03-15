package com.github.kxfeng.livedata

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.arch.core.util.Function
import androidx.lifecycle.*
import androidx.lifecycle.LiveDataHiddenApi.version
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class FreshLiveDataKtTest : LifecycleOwner {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private lateinit var lifecycleRegistry: LifecycleRegistry

    override fun getLifecycle(): Lifecycle = lifecycleRegistry

    @Before
    fun setup() {
        lifecycleRegistry = LifecycleRegistry(this)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    @After
    fun teardown() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }

    @Test
    fun observeFreshly() {
        val liveData = MutableLiveData<Int>()

        val normalResult = mutableListOf<Int>()
        val freshResult = mutableListOf<Int>()

        liveData.value = 1

        liveData.observe(this, Observer<Int> {
            normalResult.add(it)
        })

        val freshObserver = Observer<Int> {
            freshResult.add(it)
        }

        liveData.observeFreshly(this, freshObserver)

        assertEquals(listOf(1), normalResult)
        assertEquals(emptyList<Int>(), freshResult)

        liveData.value = 2

        assertEquals(listOf(1, 2), normalResult)
        assertEquals(listOf(2), freshResult)

        liveData.removeObserverFreshly(freshObserver)

        liveData.value = 3

        assertEquals(listOf(1, 2, 3), normalResult)
        assertEquals(listOf(2), freshResult)
    }

    @Test
    fun observeForeverFreshly() {
        val liveData = MutableLiveData<Int>()

        val normalResult = mutableListOf<Int>()
        val freshResult = mutableListOf<Int>()

        liveData.value = 1

        liveData.observeForever {
            normalResult.add(it)
        }

        val freshObserver = Observer<Int> {
            freshResult.add(it)
        }

        liveData.observeForeverFreshly(freshObserver)

        assertEquals(listOf(1), normalResult)
        assertEquals(emptyList<Int>(), freshResult)

        liveData.value = 2

        assertEquals(listOf(1, 2), normalResult)
        assertEquals(listOf(2), freshResult)

        liveData.removeObserverFreshly(freshObserver)

        liveData.value = 3

        assertEquals(listOf(1, 2, 3), normalResult)
        assertEquals(listOf(2), freshResult)
    }

    @Test
    fun observeMediatorLiveData() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)

        val sourceLiveData = MutableLiveData<Int>()
        val mapLiveData = Transformations.map<Int, Int>(sourceLiveData, Function {
            return@Function -it
        })


        val normalResult = mutableListOf<Int>()
        val freshResult = mutableListOf<Int>()

        mapLiveData.observe(this, Observer {
            normalResult.add(it)
        })

        sourceLiveData.value = 1

        assertEquals(emptyList<Int>(), normalResult)

        val version1 = mapLiveData.version()

        val freshObserver = Observer<Int> {
            freshResult.add(it)
        }

        mapLiveData.observeFreshly(this, freshObserver)

        val version2 = mapLiveData.version()

        assertNotEquals(version2, version1)

        assertEquals(emptyList<Int>(), freshResult)

        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        assertEquals(listOf(-1), normalResult)
        assertEquals(emptyList<Int>(), freshResult)

        sourceLiveData.value = 2

        assertEquals(listOf(-1, -2), normalResult)
        assertEquals(listOf(-2), freshResult)

        mapLiveData.removeObserverFreshly(freshObserver)

        sourceLiveData.value = 3
        assertEquals(listOf(-1, -2, -3), normalResult)
        assertEquals(listOf(-2), freshResult)
    }
}