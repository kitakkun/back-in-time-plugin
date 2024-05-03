package com.github.kitakkun.backintime.debugger.featurecommon.lifecycle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner

/**
 * Currently, trying to generate viewModel with [androidx.lifecycle.viewmodel.compose.viewModel], it will fail with the following runtime error:
 *
 * exception in thread "main" java.lang.UnsupportedOperationException: `Factory.create(String, CreationExtras)` is not implemented. You may need to override the method and provide a custom implementation. Note that using `Factory.create(String)` is not supported and considered an error.
 * 	at androidx.lifecycle.viewmodel.internal.ViewModelProviders.unsupportedCreateViewModel$lifecycle_viewmodel(ViewModelProviders.kt:51)
 * 	at androidx.lifecycle.ViewModelProvider$Factory.create(ViewModelProvider.desktop.kt:44)
 *
 * I don't know exactly why this error occurs, but I think it's related to the `LocalViewModelStoreOwner` that is provided via NavHost(I mean [androidx.navigation.NavBackStackEntry])
 * To avoid this, we create a custom `LocalViewModelStoreOwner` and provide it via `CompositionLocalProvider` manually.
 *
 * Easy way to reproduce this issue:
 * 1. generate a ViewModel instance inside `composable` function which can be invoked inside NavGraphBuilder.
 * 2. run the app and it will crash with the error above.
 * 3. change the code to generate ViewModel instance inside `LocalViewModelStoreOwnerProvider` and it will work.
 */
@Composable
fun LocalViewModelStoreOwnerProvider(content: @Composable () -> Unit) {
    val viewModelStoreOwner = remember { DisposableViewModelStoreOwner() }

    DisposableEffect(viewModelStoreOwner) {
        onDispose {
            viewModelStoreOwner.dispose()
        }
    }

    CompositionLocalProvider(LocalViewModelStoreOwner provides viewModelStoreOwner) {
        content()
    }
}

class DisposableViewModelStoreOwner : ViewModelStoreOwner {
    override val viewModelStore: ViewModelStore = ViewModelStore()

    fun dispose() {
        viewModelStore.clear()
    }
}
