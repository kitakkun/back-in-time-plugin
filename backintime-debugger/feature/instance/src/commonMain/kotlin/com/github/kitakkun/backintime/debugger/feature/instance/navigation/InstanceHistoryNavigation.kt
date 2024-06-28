package com.github.kitakkun.backintime.debugger.feature.instance.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.github.kitakkun.backintime.debugger.feature.instance.view.history.HistoryPage

internal const val INSTANCE_HISTORY_ROUTE = "history"
internal const val SESSION_ID_ARGUMENT = "sessionId"
internal const val INSTANCE_ID_ARGUMENT = "instanceId"

internal fun NavGraphBuilder.instanceHistory(navController: NavController) {
    composable(
        route = "$INSTANCE_HISTORY_ROUTE/{$SESSION_ID_ARGUMENT}/{$INSTANCE_ID_ARGUMENT}",
        arguments = listOf(
            navArgument(SESSION_ID_ARGUMENT) { type = NavType.StringType },
            navArgument(INSTANCE_ID_ARGUMENT) { type = NavType.StringType },
        ),
    ) {
        val sessionId = it.arguments?.getString(SESSION_ID_ARGUMENT) ?: ""
        val instanceId = it.arguments?.getString(INSTANCE_ID_ARGUMENT) ?: ""
        HistoryPage(
            sessionId = sessionId,
            instanceId = instanceId,
            navController = navController,
        )
    }
}

fun NavController.navigateToInstanceHistory(
    sessionId: String,
    instanceId: String,
) {
    navigate("$INSTANCE_HISTORY_ROUTE/$sessionId/$instanceId")
}
