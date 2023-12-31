package com.github.kitakkun.backintime.test

import com.github.kitakkun.backintime.annotations.DebuggableStateHolder

@DebuggableStateHolder
class MutableCollectionsHolder {
    private val mutableList = mutableListOf<String>()
    private val mutableSet = mutableSetOf<String>()
    private val mutableMap = mutableMapOf<String, String>()

    fun mutableListTest() {
        mutableList.add("Hello")
        mutableList.addAll(listOf("World", "!"))
        mutableList.removeAt(1)
        mutableList.remove("!")
        mutableList.clear()
    }

    fun mutableSetTest() {
        mutableSet.add("Hello")
        mutableSet.addAll(listOf("World", "!"))
        mutableSet.remove("!")
        mutableSet.clear()
    }

    fun mutableMapTest() {
        mutableMap["Hello"] = "World"
        mutableMap["World"] = "!"
        mutableMap.remove("!")
        mutableMap.clear()
    }
}
