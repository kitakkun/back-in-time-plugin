package com.github.kitakkun.backintime.runtime

actual class WeakReference<T : Any> actual constructor(value: T) {
    private val ref = java.lang.ref.WeakReference(value)
    actual fun get(): T? = ref.get()
}