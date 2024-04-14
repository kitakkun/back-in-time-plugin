package com.github.kitakkun.backintime.debugger

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.github.kitakkun.backintime.debugger.root.RootScreen
import com.github.kitakkun.backintime.debugger.ui.primitive.BackInTimeDebuggerTheme

@Composable
fun App() {
    BackInTimeDebuggerTheme {
        Navigator(RootScreen)
    }
}
