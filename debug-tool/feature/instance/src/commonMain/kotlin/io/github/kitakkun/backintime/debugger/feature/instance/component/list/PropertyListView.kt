package io.github.kitakkun.backintime.debugger.feature.instance.component.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PropertyListView(
    properties: List<PropertyUiState>,
    onClickProperty: (PropertyUiState) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        properties.forEachIndexed { index, property ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 32.dp),
            ) {
                VerticalDivider(
                    modifier = Modifier
                        .height(
                            if (index == properties.size - 1) {
                                25.dp
                            } else {
                                50.dp
                            },
                        )
                        .align(Alignment.Top),
                )
                HorizontalDivider(Modifier.width(20.dp))
                PropertyItemView(
                    uiState = property,
                    modifier = Modifier
                        .height(50.dp)
                        .then(
                            if (property.isSelected) {
                                Modifier.background(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = MaterialTheme.shapes.small,
                                )
                            } else {
                                Modifier
                            }
                        )
                        .clickable(onClick = { onClickProperty(property) }),
                )
            }
        }
    }
}
