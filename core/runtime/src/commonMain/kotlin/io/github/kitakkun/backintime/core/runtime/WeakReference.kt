package io.github.kitakkun.backintime.core.runtime

expect fun <T : Any> weakReferenceOf(value: T): WeakReference<T>

interface WeakReference<T : Any> {
    fun get(): T?
}
