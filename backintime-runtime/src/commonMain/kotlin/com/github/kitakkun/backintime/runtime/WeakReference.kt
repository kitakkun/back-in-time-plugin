package com.github.kitakkun.backintime.runtime

expect class WeakReference<T : Any>(value: T) {
    fun get(): T?
}