package org.koin.androidx.viewmodel.dsl

import androidx.lifecycle.ViewModel
import org.koin.core.definition.Definition
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier

/**
 * FIXME: This is a workaround for the issue that koin-ksp-compiler doesn't support KMP version of viewModel DSL.
 *  This can be removed when koin-ksp-compiler supports KMP version of viewModel DSL.
 *  see this issue: [ViewModel KMP support](https://github.com/InsertKoinIO/koin-annotations/issues/130)
 */
inline fun <reified T : ViewModel> Module.viewModel(
    qualifier: Qualifier? = null,
    noinline definition: Definition<T>,
): KoinDefinition<T> {
    return factory(qualifier, definition)
}
