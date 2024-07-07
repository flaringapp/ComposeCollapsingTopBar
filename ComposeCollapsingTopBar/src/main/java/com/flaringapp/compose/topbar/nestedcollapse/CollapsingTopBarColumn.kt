/*
 * Copyright 2024 Flaringapp
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flaringapp.compose.topbar.nestedcollapse

import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import com.flaringapp.compose.topbar.CollapsingTopBarProgressListener
import com.flaringapp.compose.topbar.CollapsingTopBarScope
import com.flaringapp.compose.topbar.CollapsingTopBarState
import kotlin.math.min

@Composable
fun CollapsingTopBarScope.CollapsingTopBarColumn(
    state: CollapsingTopBarState,
    modifier: Modifier = Modifier,
    content: @Composable CollapsingTopBarColumnScope.() -> Unit,
) {
    val nestedCollapseState = rememberCollapsingTopBarNestedCollapseState()

    val measurePolicy = remember(state) {
        CollapsingTopBarColumnMeasurePolicy(
            state = state,
            nestedCollapseState = nestedCollapseState,
        )
    }

    Layout(
        modifier = modifier
            .nestedCollapse(nestedCollapseState),
        measurePolicy = measurePolicy,
        content = { CollapsingTopBarColumnScopeInstance.content() },
    )
}

private class CollapsingTopBarColumnMeasurePolicy(
    state: CollapsingTopBarState,
    private val nestedCollapseState: CollapsingTopBarNestedCollapseState,
) : MeasurePolicy {

    private val topBarHeightState by derivedStateOf {
        // Don't care about height changes outside column height
        state.layoutInfo.height.coerceAtMost(lastTotalHeight.toFloat())
    }

    private var lastTotalHeight: Int = Int.MAX_VALUE

    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): MeasureResult {
        if (measurables.isEmpty()) {
            return layout(0, 0) {}
        }

        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val placeables = measurables.map {
            it.measure(looseConstraints)
        }

        val width = placeables.maxOf { it.width }
        val totalHeight = placeables.sumOf { it.height }
        val minHeight = placeables.sumOf {
            if (it.columnParentData?.isNotCollapsible == true) it.height else 0
        }
        val collapsibleHeight = totalHeight - minHeight

        lastTotalHeight = totalHeight
        nestedCollapseState.minHeight = minHeight

        return layout(width, totalHeight) {
            // Can be larger than collapsible height when top bar is exiting completely
            val collapseOffset = (totalHeight - topBarHeightState).coerceAtLeast(0f)
            val collapseFraction = (collapseOffset / collapsibleHeight).coerceAtMost(1f)
            val expandFraction = 1f - collapseFraction

            var unhandledCollapseOffset = collapseOffset.toInt()
            var placementOffset = totalHeight
            var isPinned = false

            // Placing in reverse order - we're collapsing bottom up
            placeables.asReversed().forEach { placeable ->
                val itemOffset = placementOffset - placeable.height
                placementOffset = itemOffset

                val parentData = placeable.columnParentData

                // Pinned element
                if (parentData?.isNotCollapsible == true) {
                    // Already pinned
                    if (unhandledCollapseOffset > 0) {
                        parentData.progressListener?.onProgressUpdate(
                            totalProgress = expandFraction,
                            itemProgress = 1f,
                        )
                        placeable.place(0, itemOffset - unhandledCollapseOffset, 1f)
                        return@forEach
                    }

                    // View below are still collapsing
                    isPinned = true
                }

                if (isPinned) {
                    parentData?.progressListener?.onProgressUpdate(
                        totalProgress = expandFraction,
                        itemProgress = 1f,
                    )
                    placeable.place(0, itemOffset)
                    return@forEach
                }

                // Collapsible element
                val itemCollapseOffset = min(placeable.height, unhandledCollapseOffset)
                unhandledCollapseOffset =
                    (unhandledCollapseOffset - itemCollapseOffset).coerceAtLeast(0)

                parentData?.progressListener?.onProgressUpdate(
                    totalProgress = expandFraction,
                    itemProgress = 1f - itemCollapseOffset.toFloat() / placeable.height,
                )

                placeable.place(0, itemOffset - itemCollapseOffset)
            }
        }
    }
}

@LayoutScopeMarker
@Immutable
interface CollapsingTopBarColumnScope {

    fun Modifier.columnProgress(listener: CollapsingTopBarProgressListener): Modifier

    fun Modifier.notCollapsible(): Modifier
}

private object CollapsingTopBarColumnScopeInstance : CollapsingTopBarColumnScope {

    override fun Modifier.columnProgress(listener: CollapsingTopBarProgressListener): Modifier {
        return then(ProgressListenerModifier(listener))
    }

    override fun Modifier.notCollapsible(): Modifier {
        return then(NotCollapsibleModifier())
    }
}

private class ProgressListenerModifier(
    private val listener: CollapsingTopBarProgressListener,
) : CollapsingTopBarColumnParentDataModifier() {
    override fun modifyParentData(parentData: CollapsingTopBarColumnParentData) {
        parentData.progressListener = listener
    }
}

private class NotCollapsibleModifier : CollapsingTopBarColumnParentDataModifier() {
    override fun modifyParentData(parentData: CollapsingTopBarColumnParentData) {
        parentData.isNotCollapsible = true
    }
}

private abstract class CollapsingTopBarColumnParentDataModifier : ParentDataModifier {

    override fun Density.modifyParentData(parentData: Any?): Any? {
        val data = parentData as? CollapsingTopBarColumnParentData
            ?: CollapsingTopBarColumnParentData()
        this@CollapsingTopBarColumnParentDataModifier.modifyParentData(data)
        return data
    }

    protected abstract fun modifyParentData(parentData: CollapsingTopBarColumnParentData)
}

private data class CollapsingTopBarColumnParentData(
    var progressListener: CollapsingTopBarProgressListener? = null,
    var isNotCollapsible: Boolean = false,
)

private val Placeable.columnParentData: CollapsingTopBarColumnParentData?
    get() = parentData as? CollapsingTopBarColumnParentData
