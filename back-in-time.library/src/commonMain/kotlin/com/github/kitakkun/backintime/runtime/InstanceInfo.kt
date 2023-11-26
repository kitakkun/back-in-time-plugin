package com.github.kitakkun.backintime.runtime

import java.util.UUID

/**
 * additional information for the instance registration
 * @param uuid: UUID of the instance
 * @param type: FqName of the class of the instance
 * @param properties: Map of property name and its type fqname
 * @param registeredAt: Time when the instance is registered(millis)
 */
data class InstanceInfo(
    val type: String,
    val properties: List<PropertyInfo>,
    val uuid: String = UUID.randomUUID().toString(),
    val registeredAt: Long = System.currentTimeMillis(),
)

data class PropertyInfo(
    val name: String,
    val debuggable: Boolean,
    val propertyType: String,
    val valueType: String,
)
