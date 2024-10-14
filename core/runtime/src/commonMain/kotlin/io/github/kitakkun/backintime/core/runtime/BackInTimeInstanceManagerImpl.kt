package io.github.kitakkun.backintime.core.runtime

/**
 * The default implementation of [BackInTimeInstanceManager].
 */
internal class BackInTimeInstanceManagerImpl : BackInTimeInstanceManager {
    private val mutableInstances = mutableMapOf<String, WeakReference<BackInTimeDebuggable>>()
    private val instances: Map<String, WeakReference<BackInTimeDebuggable>> = mutableInstances

    override fun register(instance: BackInTimeDebuggable) {
        mutableInstances[instance.backInTimeInstanceUUID] = weakReferenceOf(instance)
    }

    override fun unregister(id: String) {
        mutableInstances.remove(id)
    }

    override fun getInstanceById(id: String): BackInTimeDebuggable? {
        return instances[id]?.get()
    }

    override suspend fun cleanGarbageCollectedReferences() {
        mutableInstances
            .filterValues { it.get() == null }
            .keys
            .forEach(mutableInstances::remove)
    }
}
