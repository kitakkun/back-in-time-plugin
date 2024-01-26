package com.github.kitakkun.backintime.runtime

import kotlinx.serialization.Serializable

/**
 * additional information for the instance registration
 * @param uuid: UUID of the instance
 * @param type: FqName of the class of the instance
 * @param superType: FqName of the super class of the instance
 * @param properties: Map of property name and its type fqname
 * @param registeredAt: Time when the instance is registered(millis)
 */
@Serializable
data class InstanceInfo(
    val type: String,
    val superType: String,
    val properties: List<PropertyInfo>,
    val uuid: String,
    val registeredAt: Long = System.currentTimeMillis(),
)

@Serializable
data class PropertyInfo(
    val name: String,
    val debuggable: Boolean,
    val isDebuggableStateHolder: Boolean,
    val propertyType: String,
    val valueType: String,
)
