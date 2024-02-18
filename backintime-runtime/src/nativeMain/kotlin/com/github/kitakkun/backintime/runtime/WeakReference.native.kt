package com.github.kitakkun.backintime.runtime

import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalNativeApi::class)
actual class WeakReference<T : Any> actual constructor(value: T) {
    private val ref = kotlin.native.ref.WeakReference(value)
    actual fun get(): T? = ref.value
}