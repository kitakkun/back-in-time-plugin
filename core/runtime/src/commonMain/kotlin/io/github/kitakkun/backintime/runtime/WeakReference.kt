package io.github.kitakkun.backintime.runtime

expect fun <T : Any> weakReferenceOf(value: T): WeakReference<T>

interface WeakReference<T : Any> {
    fun get(): T?
}
