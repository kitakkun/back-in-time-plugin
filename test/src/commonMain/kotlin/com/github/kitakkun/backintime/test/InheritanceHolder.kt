package com.github.kitakkun.backintime.test

import com.github.kitakkun.backintime.annotations.DebuggableStateHolder

@DebuggableStateHolder
open class SuperClass {
    var superProperty: String = "super"
}

@DebuggableStateHolder
class SubClass : SuperClass() {
    var subProperty: String = "sub"
}
