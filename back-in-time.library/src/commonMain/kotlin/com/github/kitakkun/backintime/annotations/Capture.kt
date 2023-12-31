package com.github.kitakkun.backintime.annotations

@Target(AnnotationTarget.CLASS)
annotation class ValueContainer

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
annotation class Capture

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
annotation class Getter

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
annotation class Setter
