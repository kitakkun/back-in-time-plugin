package io.github.kitakkun.backintime.core.runtime

class WeakReferenceImpl<T : Any>(value: T) : WeakReference<T> {
    private val ref = java.lang.ref.WeakReference(value)
    override fun get(): T? = ref.get()
}

actual fun <T : Any> weakReferenceOf(value: T): WeakReference<T> {
    return WeakReferenceImpl(value)
}
