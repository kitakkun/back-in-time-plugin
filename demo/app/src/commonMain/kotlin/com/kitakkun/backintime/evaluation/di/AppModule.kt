package com.kitakkun.backintime.evaluation.di

import com.kitakkun.backintime.evaluation.data.TodoDao
import com.kitakkun.backintime.evaluation.data.TodoDatabase
import com.kitakkun.backintime.evaluation.flux.FluxTodoListStore
import com.kitakkun.backintime.evaluation.flux.TodoListActionCreator
import com.kitakkun.backintime.evaluation.flux.architecture.Dispatcher
import com.kitakkun.backintime.evaluation.mvi.MVITodoListStore
import com.kitakkun.backintime.evaluation.mvvm.MVVMTodoListViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val commonAppModule = module {
    single<TodoDao> { get<TodoDatabase>().todoDao() }
    // Flux
    singleOf(::Dispatcher)
    viewModelOf(::FluxTodoListStore)
    factoryOf(::TodoListActionCreator)
    // MVVM
    viewModelOf(::MVVMTodoListViewModel)
    // MVI
    viewModelOf(::MVITodoListStore)
}
