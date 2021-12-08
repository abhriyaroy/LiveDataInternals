package com.example.livedatainternals

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

class MyLiveData<T> {
    private var dataHolder : T?= null
    private val hashMapOfObservers : HashMap<(T?) -> Unit, LifeCycleObserverWrapper> = HashMap()

    fun postValue(value : T){
        dataHolder = value
        hashMapOfObservers.values.forEach {
            if(it.lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)){
                it.observer.invoke(dataHolder)
            }
        }
    }

    fun getValue() = dataHolder

    fun addObserver(lifecycleOwner: LifecycleOwner, observer : (T?) -> Unit){
        LifeCycleObserverWrapper(lifecycleOwner, observer)
            .apply {
                this.lifecycleOwner.lifecycle.addObserver(this)
                hashMapOfObservers[observer] = this
            }
    }

    fun removeObserver(observer: (T?) -> Unit){
        hashMapOfObservers[observer]?.run {
            this.lifecycleOwner.lifecycle.removeObserver(this)
        }
    }

    fun updateValue(observer: (T?) -> Unit){
        observer.invoke(dataHolder)
    }

    private inner class LifeCycleObserverWrapper(
        val lifecycleOwner: LifecycleOwner,
        val observer : (T?) -> Unit
    ) : LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun doOnStart(){
            updateValue(observer)
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun doOnResume(){
            updateValue(observer)
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun doOnDestroy(){
            removeObserver(observer)
        }
    }
}