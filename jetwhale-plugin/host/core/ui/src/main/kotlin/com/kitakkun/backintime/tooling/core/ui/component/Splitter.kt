package com.kitakkun.backintime.tooling.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import java.awt.Cursor
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.SplitPaneScope

/**
 * A split pane draws nothing between its panes by default, so the seam is invisible and the drag
 * target is a mystery. These give it a hairline in the divider colour plus a grab strip wider than
 * the line itself — visible without being heavy, and still easy to hit.
 *
 * The two functions differ only in axis; each is named after the pane it belongs inside, because
 * `HorizontalSplitPane` is the one with a *vertical* divider and picking the wrong one is otherwise
 * an easy mistake to make.
 */

/** Splitter for a [org.jetbrains.compose.splitpane.HorizontalSplitPane] (panes side by side). */
@OptIn(ExperimentalSplitPaneApi::class)
fun SplitPaneScope.horizontalSplitter() {
    splitter {
        visiblePart {
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.outlineVariant),
            )
        }
        handle {
            Box(
                modifier = Modifier
                    .markAsHandle()
                    .pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))
                    .width(HandleThickness)
                    .fillMaxHeight(),
            )
        }
    }
}

/** Splitter for a [org.jetbrains.compose.splitpane.VerticalSplitPane] (panes stacked). */
@OptIn(ExperimentalSplitPaneApi::class)
fun SplitPaneScope.verticalSplitter() {
    splitter {
        visiblePart {
            Box(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.outlineVariant),
            )
        }
        handle {
            Box(
                modifier = Modifier
                    .markAsHandle()
                    .pointerHoverIcon(PointerIcon(Cursor(Cursor.N_RESIZE_CURSOR)))
                    .height(HandleThickness)
                    .fillMaxWidth(),
            )
        }
    }
}

/** Wide enough to grab without aiming, narrow enough not to eat clicks meant for the panes. */
private val HandleThickness = 8.dp
