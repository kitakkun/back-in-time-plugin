package com.kitakkun.backintime.tooling.core.ui.component

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import org.jetbrains.jewel.foundation.theme.LocalTextStyle
import org.jetbrains.jewel.ui.component.Text

@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val fontSizeDp = with(LocalDensity.current) { LocalTextStyle.current.fontSize.toDp() }

    Box(
        modifier = modifier
            .background(Color.LightGray)
            .width(fontSizeDp * 4)
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
            ),
    ) {
        if (checked) {
            Text(
                text = "ON",
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterStart)
                    .background(Color(83, 104, 79))
                    .fillMaxWidth(0.6f)
                    .padding(2.dp),
            )
        } else {
            Text(
                text = "OFF",
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterEnd)
                    .background(Color.DarkGray)
                    .fillMaxWidth(0.6f)
                    .padding(2.dp),
            )
        }
    }
}

@Preview
@Composable
private fun SwitchPreview_Checked() {
    PreviewContainer {
        Switch(
            checked = true,
            onCheckedChange = {},
        )
    }
}

@Preview
@Composable
private fun SwitchPreview_Unchecked() {
    PreviewContainer {
        Switch(
            checked = false,
            onCheckedChange = {},
        )
    }
}
