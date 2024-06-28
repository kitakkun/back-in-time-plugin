package com.github.kitakkun.backintime.debugger.feature.instance.view.list.propertyinspector

import com.github.kitakkun.backintime.debugger.feature.instance.view.list.propertyinspector.component.ChangeInfoBindModel
import com.github.kitakkun.backintime.debugger.feature.instance.view.list.propertyinspector.component.InstanceInfoBindModel
import com.github.kitakkun.backintime.debugger.feature.instance.view.list.propertyinspector.component.PropertyInfoBindModel

sealed class PropertyInspectorBindModel {
    data object Loading : PropertyInspectorBindModel()
    data class Error(val message: String) : PropertyInspectorBindModel()
    data class Loaded(
        val instanceInfo: InstanceInfoBindModel,
        val propertyInfo: PropertyInfoBindModel,
        val changesInfo: List<ChangeInfoBindModel>,
        private val sortRule: SortRule,
    ) : PropertyInspectorBindModel() {
        val isSortWithValueActive: Boolean get() = sortRule == SortRule.VALUE_ASC || sortRule == SortRule.VALUE_DESC
        val isSortWithTimeActive: Boolean get() = sortRule == SortRule.CREATED_AT_ASC || sortRule == SortRule.CREATED_AT_DESC
        val isSortWithValueAscending: Boolean get() = sortRule == SortRule.VALUE_ASC
        val isSortWithTimeAscending: Boolean get() = sortRule == SortRule.CREATED_AT_ASC
    }
}
