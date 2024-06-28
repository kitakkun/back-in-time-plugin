package com.github.kitakkun.backintime.debugger.feature.instance.view.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.kitakkun.backintime.debugger.feature.instance.view.list.component.InstanceBindModel
import com.github.kitakkun.backintime.debugger.feature.instance.view.list.component.InstanceItemView
import com.github.kitakkun.backintime.debugger.feature.instance.view.list.component.PropertyBindModel
import com.github.kitakkun.backintime.debugger.ui.customview.CommonLoadingView
import com.github.kitakkun.backintime.instance.generated.resources.Res
import com.github.kitakkun.backintime.instance.generated.resources.loading_instances
import com.github.kitakkun.backintime.instance.generated.resources.msg_no_instance_registered
import org.jetbrains.compose.resources.stringResource

@Composable
fun SessionInstanceView(
    bindModel: SessionInstanceBindModel,
    onTogglePropertiesExpanded: (InstanceBindModel) -> Unit,
    onClickProperty: (InstanceBindModel, PropertyBindModel) -> Unit,
    onClickHistory: (InstanceBindModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (bindModel) {
        is SessionInstanceBindModel.Loading -> CommonLoadingView(modifier, stringResource(Res.string.loading_instances))
        is SessionInstanceBindModel.InstancesAvailable -> LoadedView(
            instances = bindModel.instanceBindModels,
            onTogglePropertiesExpanded = onTogglePropertiesExpanded,
            onClickProperty = onClickProperty,
            onClickHistory = onClickHistory,
            modifier = modifier,
        )

        is SessionInstanceBindModel.NoInstanceRegistered -> NoInstanceRegisteredView()
    }
}

@Composable
private fun LoadedView(
    instances: List<InstanceBindModel>,
    onTogglePropertiesExpanded: (InstanceBindModel) -> Unit,
    onClickProperty: (InstanceBindModel, PropertyBindModel) -> Unit,
    onClickHistory: (InstanceBindModel) -> Unit,
    modifier: Modifier,
) {
    LazyColumn(modifier.fillMaxSize()) {
        items(instances) { bindModel ->
            InstanceItemView(
                bindModel = bindModel,
                onClickExpand = { onTogglePropertiesExpanded(bindModel) },
                onClickProperty = { propertyBindModel -> onClickProperty(bindModel, propertyBindModel) },
                onClickHistory = { onClickHistory(bindModel) },
            )
        }
    }
}

@Composable
private fun NoInstanceRegisteredView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) {
        Text(stringResource(Res.string.msg_no_instance_registered))
    }
}
