package io.github.kitakkun.backintime.debugger.feature.instance.component.inspector

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import backintime.debug_tool.feature.instance.generated.resources.Res
import backintime.debug_tool.feature.instance.generated.resources.no_property_to_show
import org.jetbrains.compose.resources.stringResource

@Composable
fun NoPropertyView(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = stringResource(Res.string.no_property_to_show))
    }
}

@Preview
@Composable
private fun NoPropertyViewPreview() {
    NoPropertyView()
}
