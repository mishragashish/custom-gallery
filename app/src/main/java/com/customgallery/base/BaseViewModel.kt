package com.customgallery.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.lang.Exception

open class BaseViewModel: ViewModel() {
    val errObserve = MutableLiveData<Int>()
    val errMsgObserve = MutableLiveData<String>()

    fun catchException(exception: Exception) {
        errMsgObserve.postValue(exception.message.toString())
    }
}