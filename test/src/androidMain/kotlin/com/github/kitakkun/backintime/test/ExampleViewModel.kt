package com.github.kitakkun.backintime.test

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.kitakkun.backintime.annotations.DebuggableStateHolder

@DebuggableStateHolder
class ExampleViewModel : ViewModel() {
    private val mutableLiveData = MutableLiveData("")
    val liveData: LiveData<String> = mutableLiveData

    fun updateLiveData() {
        mutableLiveData.value = "Updated from <set-value>"
        mutableLiveData.setValue("Updated from setValue")
        mutableLiveData.postValue("Updated from postValue")
    }
}
