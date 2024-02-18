package com.github.kitakkun.backintime.evaluation.di

import androidx.room.Room
import com.github.kitakkun.backintime.evaluation.data.TodoDao
import com.github.kitakkun.backintime.evaluation.data.TodoDatabase
import com.github.kitakkun.backintime.evaluation.flux.FluxTodoListStore
import com.github.kitakkun.backintime.evaluation.flux.TodoListActionCreator
import com.github.kitakkun.backintime.evaluation.flux.architecture.Dispatcher
import com.github.kitakkun.backintime.evaluation.mvi.MVITodoListStore
import com.github.kitakkun.backintime.evaluation.mvvm.MVVMTodoListViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    // Shared
    single<TodoDatabase> {
        Room.databaseBuilder(get(), TodoDatabase::class.java, "todo-database").build()
    }
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
