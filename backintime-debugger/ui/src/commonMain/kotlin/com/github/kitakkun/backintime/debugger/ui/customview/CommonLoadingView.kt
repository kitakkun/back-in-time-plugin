package com.github.kitakkun.backintime.debugger.ui.customview

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.kitakkun.backintime.ui.generated.resources.Res
import com.github.kitakkun.backintime.ui.generated.resources.loading
import org.jetbrains.compose.resources.stringResource

@Composable
fun CommonLoadingView(
    modifier: Modifier = Modifier,
    message: String = stringResource(Res.string.loading),
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) {
        Text(message)
        CircularProgressIndicator()
    }
}

@Preview
@Composable
private fun CommonLoadingViewPreview() {
    CommonLoadingView()
}
