package com.github.kitakkun.backintime.runtime

import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalNativeApi::class)
class WeakReferenceImpl<T : Any>(value: T) : WeakReference<T> {
    private val ref = kotlin.native.ref.WeakReference(value)
    override fun get(): T? = ref.get()
}

actual fun <T : Any> weakReferenceOf(value: T): WeakReference<T> {
    return WeakReferenceImpl(value)
}
