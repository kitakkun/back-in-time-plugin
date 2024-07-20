package io.github.kitakkun.backintime.debugger.core.usecase

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.github.kitakkun.backintime.debugger.core.data.ValueChangeRepository
import io.github.kitakkun.backintime.debugger.core.model.ValueChange
import org.koin.compose.koinInject

@Composable
fun valueChangesByProperty(
    sessionId: String,
    instanceId: String,
    propertyId: String,
): List<ValueChange> {
    val valueChangeRepository: ValueChangeRepository = koinInject()
    val valueChanges by valueChangeRepository.selectByPropertyAsFlow(
        instanceId = instanceId,
        sessionId = sessionId,
        propertyId = propertyId,
    ).collectAsState(emptyList())
    return valueChanges.map {
        ValueChange(
            propertyOwnerClassName = it.propertyOwnerClassName,
            propertyName = it.propertyName,
            newValue = it.newValue,
        )
    }
}
