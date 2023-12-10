package com.github.kitakkun.backintime.demo

import com.github.kitakkun.backintime.annotations.Capture
import com.github.kitakkun.backintime.annotations.Getter
import com.github.kitakkun.backintime.annotations.Setter

class ValueContainer<T>(@Getter @Setter @Capture var value: T) {
    @Capture
    fun update(newValue: T) {
        value = newValue
    }
}
