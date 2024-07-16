package io.github.kitakkun.backintime.test

import androidx.lifecycle.MutableLiveData
import io.github.kitakkun.backintime.annotations.BackInTime

@BackInTime
class MutableLiveDataViewModel {
    private val mutableLiveData = MutableLiveData(0)

    fun mutateMutableLiveData() {
        mutableLiveData.value = 1
        mutableLiveData.setValue(2)
        mutableLiveData.postValue(3)
    }
}
