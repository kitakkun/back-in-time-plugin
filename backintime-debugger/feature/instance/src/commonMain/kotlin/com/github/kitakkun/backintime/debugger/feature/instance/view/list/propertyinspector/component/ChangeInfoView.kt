package com.github.kitakkun.backintime.debugger.feature.instance.view.list.propertyinspector.component

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.LineBreak
import com.github.kitakkun.backintime.debugger.featurecommon.util.formatEpochSecondsToDateTimeText
import com.github.kitakkun.backintime.debugger.ui.theme.DebuggerTheme

data class ChangeInfoBindModel(
    val time: Long,
    val methodCallId: String,
    val newValue: String,
) {
    val formattedTime = formatEpochSecondsToDateTimeText(time)
}

@Composable
fun ChangeInfoView(
    bindModel: ChangeInfoBindModel,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
    ) {
        CompositionLocalProvider(LocalTextStyle provides DebuggerTheme.typography.labelMedium) {
            Text(text = bindModel.formattedTime)
            Text(
                text = bindModel.newValue,
                style = LocalTextStyle.current.copy(
                    lineBreak = LineBreak.Simple,
                ),
            )
        }
    }
}

@Preview
@Composable
private fun ChangeInfoViewPreview() {
    ChangeInfoView(
        bindModel = ChangeInfoBindModel(
            time = 0,
            methodCallId = "123456",
            newValue = "new value",
        ),
    )
}
