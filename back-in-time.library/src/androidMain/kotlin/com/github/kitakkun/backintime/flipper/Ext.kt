package com.github.kitakkun.backintime.flipper

import com.facebook.flipper.core.FlipperArray
import com.facebook.flipper.core.FlipperObject
import com.github.kitakkun.backintime.runtime.InstanceInfo
import com.github.kitakkun.backintime.runtime.PropertyInfo

fun InstanceInfo.toFlipperObject() = FlipperObject.Builder()
    .put("uuid", uuid)
    .put("type", type)
    .put("properties", FlipperArray.Builder().apply {
        properties.forEach { put(it.toFlipperObject()) }
    })
    .put("registeredAt", registeredAt)
    .build()

fun PropertyInfo.toFlipperObject() = FlipperObject.Builder()
    .put("name", name)
    .put("debuggable", debuggable)
    .put("type", type)
    .build()
