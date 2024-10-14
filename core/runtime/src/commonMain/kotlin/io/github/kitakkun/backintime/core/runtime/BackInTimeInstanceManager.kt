package io.github.kitakkun.backintime.core.runtime

/**
 * For back-in-time debugging, we need to have references to the debug-target instances.
 * Classes which implements this interface are responsible for managing their references safely.
 *
 * When registering an instance, remember to wrap its reference with [WeakReference].
 * Do not directly hold its reference because it will result in memory leak.
 *
 * see [BackInTimeInstanceManagerImpl] for the actual implementation.
 */
internal interface BackInTimeInstanceManager {
    fun register(instance: BackInTimeDebuggable)
    fun unregister(id: String)
    fun getInstanceById(id: String): BackInTimeDebuggable?
    suspend fun cleanGarbageCollectedReferences()
}
