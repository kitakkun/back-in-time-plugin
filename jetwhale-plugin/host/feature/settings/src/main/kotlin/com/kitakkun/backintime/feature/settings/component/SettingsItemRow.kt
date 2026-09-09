package com.kitakkun.backintime.feature.settings.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kitakkun.jetwhale.host.ui.JwMetrics
import com.kitakkun.jetwhale.host.ui.JwSpacing
import com.kitakkun.jetwhale.host.ui.JwText

/**
 * One setting: its name on the left, the control that changes it on the right.
 *
 * The row is [JwMetrics.controlHeight] tall whether or not it holds a control, so a page of them
 * keeps an even rhythm.
 */
@Composable
fun SettingsItemRow(
    label: @Composable () -> Unit,
    settingComponent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().heightIn(min = JwMetrics.controlHeight),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(JwSpacing.large, Alignment.Start),
    ) {
        label()
        Spacer(Modifier.weight(1f))
        settingComponent()
    }
}

/**
 * The name of a single setting. Kept a step down from the section heading so a settings page reads
 * as a list of small choices rather than a stack of titles.
 */
@Composable
fun SettingLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    JwText(
        text = text,
        modifier = modifier,
    )
}
