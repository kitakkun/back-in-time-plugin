package com.github.kitakkun.backintime.debugger.feature.log.view.session_log

import androidx.lifecycle.ViewModel
import com.github.kitakkun.backintime.debugger.feature.log.view.session_log.content.LogItemBindModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionLogViewModel : ViewModel() {
    private val mutableSelectedLogItem = MutableStateFlow<LogItemBindModel?>(null)
    val selectedLogItem = mutableSelectedLogItem.asStateFlow()

    fun selectLogItem(logItem: LogItemBindModel) {
        mutableSelectedLogItem.value = logItem
    }

    fun clearSelectedLogItem() {
        mutableSelectedLogItem.value = null
    }
}
