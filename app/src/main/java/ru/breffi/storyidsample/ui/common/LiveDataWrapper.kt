package ru.breffi.storyidsample.ui.common

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

open class LiveDataWrapper<T : Any>(val liveData: LiveData<T?>) {

    private var value: T? = null

    fun observeFirstNonNull(owner: LifecycleOwner, onChanged: (T) -> Unit) {
        liveData.observe(owner, Observer {
            if (value == null && it != null) {
                value = it
                onChanged.invoke(it)
            }
        })
    }
}
