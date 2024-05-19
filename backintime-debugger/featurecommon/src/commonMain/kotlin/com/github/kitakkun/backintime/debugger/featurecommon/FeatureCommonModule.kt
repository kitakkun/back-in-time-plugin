package com.github.kitakkun.backintime.debugger.featurecommon

import com.github.kitakkun.backintime.debugger.featurecommon.view.sessionselect.SessionSelectViewModel
import com.github.kitakkun.backintime.debugger.featurecommon.view.sessiontab.SessionTabViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val featureCommonModule = module {
    singleOf(::SessionTabViewModel)
    viewModel { (openedSessionIds: List<String>) ->
        SessionSelectViewModel(openedSessionIds = openedSessionIds)
    }
}
