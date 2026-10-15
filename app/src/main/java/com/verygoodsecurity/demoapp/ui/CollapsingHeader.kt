package com.verygoodsecurity.demoapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.layout
import kotlin.math.roundToInt

/**
 * "Exit until collapsed" behaviour: scrolling content up first shrinks the part of the header marked with
 * [collapsible], scrolling back down expands it once the content is at the top again.
 *
 * Attach [nestedScrollConnection] to a common parent of the header and the scrollable content.
 */
@Stable
class CollapsingHeaderState {

    private var offset by mutableFloatStateOf(0f)

    /** Natural height of the collapsible part, measured by [collapsible]. */
    internal var range = 0f

    /** 0 = fully expanded, 1 = fully collapsed. */
    val fraction: Float get() = if (range > 0f) (offset / range).coerceIn(0f, 1f) else 0f

    internal val collapsedHeight: (Int) -> Int get() = { natural -> (natural - offset).coerceAtLeast(0f).roundToInt() }

    val nestedScrollConnection = object : NestedScrollConnection {

        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            if (available.y >= 0f) return Offset.Zero
            val new = (offset - available.y).coerceIn(0f, range)
            val consumed = new - offset
            offset = new
            return Offset(0f, -consumed)
        }

        override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
            if (available.y <= 0f) return Offset.Zero
            val new = (offset - available.y).coerceIn(0f, range)
            val used = offset - new
            offset = new
            return Offset(0f, used)
        }
    }
}

@Composable
fun rememberCollapsingHeaderState() = remember { CollapsingHeaderState() }

/** Marks header content that shrinks and fades out as [state] collapses. */
fun Modifier.collapsible(state: CollapsingHeaderState): Modifier = this
    .layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        state.range = placeable.height.toFloat()
        layout(placeable.width, state.collapsedHeight(placeable.height)) { placeable.place(0, 0) }
    }
    .clipToBounds()
    .graphicsLayer { alpha = 1f - state.fraction }
