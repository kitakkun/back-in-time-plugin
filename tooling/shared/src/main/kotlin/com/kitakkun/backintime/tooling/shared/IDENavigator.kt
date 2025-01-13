package com.kitakkun.backintime.tooling.shared

/**
 * responsible for navigating to specific declarations in the IntelliJ IDEA.
 */
interface IDENavigator {
    /**
     * @param classSignature kotlin-based class signature. ex) com/example/MyClass, com/example/MyClass.Nested
     */
    fun navigateToClass(classSignature: String)

    /**
     * @param propertySignature kotlin-based member property signature. ex) com/example/MyClass.prop
     */
    fun navigateToMemberProperty(propertySignature: String)

    /**
     * @param functionSignature kotlin-based function signature. ex) com/example/MyExtensionReceiver com/example/MyClass.myFunction(kotlin/Int):kotlin/Unit
     */
    fun navigateToMemberFunction(functionSignature: String)
}
