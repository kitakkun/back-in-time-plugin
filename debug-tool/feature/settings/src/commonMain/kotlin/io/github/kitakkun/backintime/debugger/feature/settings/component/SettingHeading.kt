package io.github.kitakkun.backintime.debugger.feature.settings.component

import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingHeading(
    labelRes: StringResource,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(labelRes),
        style = MaterialTheme.typography.headlineMedium,
        modifier = modifier,
    )
}
