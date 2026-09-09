package com.kitakkun.jetwhale.plugins.backintime

/**
 * The seam between the JetWhale agent plugin and the back-in-time runtime.
 *
 * The runtime's instance registry is `internal` to `core:runtime`, so the app supplies this
 * adapter rather than the plugin reaching into it. Both operations run on whatever thread the host
 * request arrives on and must not block.
 */
public interface JetWhaleBackInTimeService {
    /**
     * Reports, for each requested UUID, whether the runtime still holds a live reference to that
     * instance. UUIDs the runtime does not know map to `false` rather than being omitted, so the
     * host can distinguish "collected" from "never registered" only by its own records.
     */
    public fun getInstanceAliveness(instanceUUIDs: List<String>): Map<String, Boolean>

    /**
     * Assigns [jsonValue] to the given property of a live instance.
     *
     * @return `false` when the instance is no longer alive, so the host can tell a rewind that did
     *   nothing from one that took effect.
     */
    public fun forceSetInstancePropertyValue(
        instanceUUID: String,
        propertySignature: String,
        jsonValue: String,
    ): Boolean
}
