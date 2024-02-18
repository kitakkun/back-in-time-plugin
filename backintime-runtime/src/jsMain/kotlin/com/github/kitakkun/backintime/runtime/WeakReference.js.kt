package com.github.kitakkun.backintime.runtime

actual class WeakReference<T : Any> actual constructor(value: T) {
    private val ref = js("WeakRef")(value)
    actual fun get(): T? = ref.deref() as? T
}