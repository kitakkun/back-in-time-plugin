@file:Suppress("UNUSED")
@file:JsModule("flipper-plugin")
@file:JsNonModule

package com.kitakkun.backintime.tooling.flipper

external interface Draft<T>

external interface ReadOnlyAtom<T> {
    fun get(): T
    fun subscribe(listener: (value: T, prevValue: T) -> Unit): () -> Unit
    fun unsubscribe(listener: (value: T, prevValue: T) -> Unit)
}

external interface Atom<T> : ReadOnlyAtom<T> {
    fun set(newValue: T)
    fun update(recipe: (draft: Draft<T>) -> Unit)
    fun <X : T> update(recipe: (draft: X) -> Unit)
}

external interface StateOptions {
    val persist: String?
    val persistToLocalStorage: Boolean
}

external fun <T> createState(initialValue: T, options: StateOptions?): Atom<T>
