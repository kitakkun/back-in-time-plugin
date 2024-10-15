package io.github.kitakkun.backintime.debugger.feature.instance.component.list

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import backintime.debug_tool.feature.instance.generated.resources.Res
import backintime.debug_tool.feature.instance.generated.resources.msg_no_instance_registered
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SessionInstanceEmptyView(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Text(stringResource(Res.string.msg_no_instance_registered))
    }
}

@Preview
@Composable
private fun SessionInstanceEmptyViewPreview() {
    SessionInstanceEmptyView()
}
