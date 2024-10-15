package io.github.kitakkun.backintime.debugger.core.model

data class ClassInfo(
    val name: String,
    val properties: List<PropertyInfo>,
    val superClass: ClassInfo?,
) {
    val allPropertiesWithOwnerClassName: List<Pair<String, PropertyInfo>> = mutableListOf<Pair<String, PropertyInfo>>().apply {
        addAll(properties.map { name to it })
        addAll(superClass?.allPropertiesWithOwnerClassName.orEmpty())
    }
    val superProperties: List<PropertyInfo>
        get() {
            val superClassInfo = superClass ?: return emptyList()
            return superClass.properties + superClass.superProperties
        }
}
