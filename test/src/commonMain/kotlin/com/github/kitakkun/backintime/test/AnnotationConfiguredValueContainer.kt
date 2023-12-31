package com.github.kitakkun.backintime.test

import com.github.kitakkun.backintime.annotations.Capture
import com.github.kitakkun.backintime.annotations.Getter
import com.github.kitakkun.backintime.annotations.Setter
import com.github.kitakkun.backintime.annotations.ValueContainer

@ValueContainer
class AnnotationConfiguredValueContainer<T>(@Getter @Setter @Capture var value: T) {
    @Capture
    fun update(newValue: T) {
        value = newValue
    }
}
