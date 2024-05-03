package com.github.kitakkun.backintime.debugger.featurecommon.view.sessiontab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SessionTabSharedViewModel : ViewModel() {
    private val mutableOpenedSessionIdsFlow = MutableSharedFlow<List<String>>()
    val openedSessionIdsFlow = mutableOpenedSessionIdsFlow.asSharedFlow()

    fun openSessions(sessionIds: List<String>) {
        viewModelScope.launch {
            mutableOpenedSessionIdsFlow.emit(sessionIds)
        }
    }
}
