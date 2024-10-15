package io.github.kitakkun.backintime.debugger.feature.instance

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import io.github.kitakkun.backintime.debugger.feature.instance.component.history.ChangeInfo
import io.github.kitakkun.backintime.debugger.feature.instance.component.inspector.InstanceDetail
import io.github.kitakkun.backintime.debugger.feature.instance.component.inspector.NoPropertyView
import io.github.kitakkun.backintime.debugger.feature.instance.component.inspector.PropertyDetail
import io.github.kitakkun.backintime.debugger.feature.instance.component.inspector.PropertyInspectErrorView
import io.github.kitakkun.backintime.debugger.feature.instance.component.inspector.PropertyInspectorLoadedView
import io.github.kitakkun.backintime.debugger.feature.instance.model.SortRule
import io.github.kitakkun.backintime.debugger.featurecommon.architecture.EventEmitter
import io.github.kitakkun.backintime.debugger.featurecommon.architecture.rememberEventEmitter

data class PropertyInspectorScreenRoute(
    val sessionId: String,
    val instanceId: String,
    val propertyName: String,
    val propertyOwnerClassName: String,
)

fun NavGraphBuilder.propertyInspectorScreen() {
    composable<PropertyInspectorScreenRoute> {
        PropertyInspectorScreen(
            route = it.toRoute()
        )
    }
}

fun NavController.navigateToPropertyInspectorScreen(
    sessionId: String,
    instanceId: String,
    propertyName: String,
    propertyOwnerClassName: String,
) {
    navigate(
        PropertyInspectorScreenRoute(
            sessionId = sessionId,
            instanceId = instanceId,
            propertyName = propertyName,
            propertyOwnerClassName = propertyOwnerClassName,
        )
    )
}

@Composable
fun PropertyInspectorScreen(
    route: PropertyInspectorScreenRoute,
    eventEmitter: EventEmitter<PropertyInspectorScreenEvent> = rememberEventEmitter(),
    uiState: PropertyInspectorScreenUiState = propertyInspectorScreenPresenter(eventEmitter, route),
) {
    PropertyInspectorScreen(
        uiState = uiState,
        onToggleSortWithTime = {},
        onToggleSortWithValue = {},
    )
}

sealed class PropertyInspectorScreenUiState {
    data object NoneSelected : PropertyInspectorScreenUiState()
    data class Error(val message: String) : PropertyInspectorScreenUiState()
    data class Loaded(
        val instanceInfo: InstanceDetail,
        val propertyInfo: PropertyDetail,
        val changesInfo: List<ChangeInfo>,
        private val sortRule: SortRule,
    ) : PropertyInspectorScreenUiState() {
        val isSortWithValueActive: Boolean get() = sortRule == SortRule.VALUE_ASC || sortRule == SortRule.VALUE_DESC
        val isSortWithTimeActive: Boolean get() = sortRule == SortRule.CREATED_AT_ASC || sortRule == SortRule.CREATED_AT_DESC
        val isSortWithValueAscending: Boolean get() = sortRule == SortRule.VALUE_ASC
        val isSortWithTimeAscending: Boolean get() = sortRule == SortRule.CREATED_AT_ASC
    }
}

@Composable
fun PropertyInspectorScreen(
    uiState: PropertyInspectorScreenUiState,
    onToggleSortWithTime: () -> Unit,
    onToggleSortWithValue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        is PropertyInspectorScreenUiState.NoneSelected -> NoPropertyView(modifier)
        is PropertyInspectorScreenUiState.Error -> PropertyInspectErrorView(uiState, modifier)
        is PropertyInspectorScreenUiState.Loaded -> PropertyInspectorLoadedView(
            uiState = uiState,
            onToggleSortWithTime = onToggleSortWithTime,
            onToggleSortWithValue = onToggleSortWithValue,
            modifier = modifier,
        )
    }
}
