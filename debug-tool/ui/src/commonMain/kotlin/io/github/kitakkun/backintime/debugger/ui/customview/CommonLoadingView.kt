package io.github.kitakkun.backintime.debugger.ui.customview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CommonLoadingView(
    modifier: Modifier = Modifier,
    message: String = "loading...",// =stringResource(Res.string.loading),
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

//@Preview
@Composable
private fun CommonLoadingViewPreview() {
    CommonLoadingView()
}
