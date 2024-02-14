package com.github.kitakkun.backintime.evaluation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.github.kitakkun.backintime.evaluation.flux.FluxTodoListPage
import com.github.kitakkun.backintime.evaluation.mvi.MVITodoListPage
import com.github.kitakkun.backintime.evaluation.mvvm.MVVMTodoListPage
import com.github.kitakkun.backintime.evaluation.ui.theme.BackintimefluxTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BackintimefluxTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    var selectedTabIndex by remember { mutableIntStateOf(0) }
                    Column {
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
        }
    }
}
