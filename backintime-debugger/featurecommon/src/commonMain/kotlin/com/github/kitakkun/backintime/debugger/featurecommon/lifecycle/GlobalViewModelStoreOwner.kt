package com.github.kitakkun.backintime.debugger.featurecommon.lifecycle

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

object GlobalViewModelStoreOwner : ViewModelStoreOwner {
    override val viewModelStore: ViewModelStore = ViewModelStore()
}
