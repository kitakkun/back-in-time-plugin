package com.kitakkun.backintime.evaluation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.kitakkun.backintime.evaluation.flux.FluxTodoListPage
import com.kitakkun.backintime.evaluation.mvi.MVITodoListPage
import com.kitakkun.backintime.evaluation.mvvm.MVVMTodoListPage
import com.kitakkun.backintime.evaluation.ui.theme.BackInTimeTheme

@Composable
fun App() {
    BackInTimeTheme {
        var selectedTabIndex by remember { mutableIntStateOf(0) }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .safeDrawingPadding()
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("MVVM") },
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Flux") },
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = { Text("MVI") },
                )
            }

            when (selectedTabIndex) {
                0 -> MVVMTodoListPage()
                1 -> FluxTodoListPage()
                2 -> MVITodoListPage()
            }
        }
    }
}
