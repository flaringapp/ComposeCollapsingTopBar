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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ParentDataModifierNode
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import com.flaringapp.compose.topbar.CollapsingTopBar
import com.flaringapp.compose.topbar.CollapsingTopBarProgressListener
import com.flaringapp.compose.topbar.CollapsingTopBarScope
import com.flaringapp.compose.topbar.CollapsingTopBarState
import kotlin.math.min

/**
 * A nested collapse container to be used inside [CollapsingTopBar]. Places its children just
 * like [androidx.compose.foundation.layout.Column], but also implements staggered collapsing
 * mechanism.
 *
 * Collapsing is performed bottom up: as soon as column starts collapsing, it pins the last child
 * and pushes up by its height under the second last. The same logic is applied to all subsequent
 * children. It also supports not collapsible elements with
 * [CollapsingTopBarColumnScope.notCollapsible] modifier. Such elements remain pinned till the end
 * of collapsing, and never collapse. If there are other collapsible elements above, they are
 * simply pinned together with not collapsible until collapsed.
 *
 * The minimum (collapsed) height of the column is equal to sum of all not collapsible elements.
 *
 * @param state the state that manages this top bar column.
 * @param modifier the [Modifier] to be applied to this top bar column.
 * @param content the content of this top bar column.
 */
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
                    if (parentData?.isNotCollapsible != true) {
                        parentData?.clipToCollapseHeightListener?.invoke(0)
                    }
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
                parentData?.clipToCollapseHeightListener?.invoke(itemCollapseOffset)

                placeable.place(0, itemOffset - itemCollapseOffset)
            }
        }
    }
}

/**
 * Scope for the children of [CollapsingTopBarColumn].
 */
@LayoutScopeMarker
@Immutable
interface CollapsingTopBarColumnScope {

    /**
     * Registers a progress listener to be notified every time top bar column collapse height
     * changes. Only the last modifier in chain takes effect.
     *
     * @param listener the listener that gets notified of every collapse progress update.
     *
     * @see CollapsingTopBarProgressListener
     */
    fun Modifier.columnProgress(listener: CollapsingTopBarProgressListener): Modifier

    /**
     * Prevent the element from collapsing and make it pin to the bottom of column as it collapses.
     * The height of all not collapsible elements form a total minimum (collapsed) height of column.
     */
    fun Modifier.notCollapsible(): Modifier

    /**
     * Clip element draw area as it collapses so that it does not draw underneath the element above.
     *
     * **Has no effect if applied together with [notCollapsible].**
     */
    fun Modifier.clipToCollapse(): Modifier
}

private object CollapsingTopBarColumnScopeInstance : CollapsingTopBarColumnScope {

    override fun Modifier.columnProgress(listener: CollapsingTopBarProgressListener): Modifier {
        return then(ProgressListenerModifier(listener))
    }

    override fun Modifier.notCollapsible(): Modifier {
        return then(NotCollapsibleModifier())
    }

    override fun Modifier.clipToCollapse(): Modifier {
        return then(ClipToCollapseElement)
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

private data object ClipToCollapseElement : ModifierNodeElement<ClipToCollapseNode>() {

    override fun create(): ClipToCollapseNode = ClipToCollapseNode()
    override fun update(node: ClipToCollapseNode) = Unit
    override fun InspectorInfo.inspectableProperties() = Unit
}

private class ClipToCollapseNode :
    Modifier.Node(),
    ParentDataModifierNode,
    LayoutModifierNode {

    private var elementCollapseHeightState = mutableIntStateOf(0)

    override fun Density.modifyParentData(parentData: Any?): Any {
        val data = parentData as? CollapsingTopBarColumnParentData
            ?: CollapsingTopBarColumnParentData()
        data.clipToCollapseHeightListener = {
            elementCollapseHeightState.intValue = it
        }
        return data
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            placeable.placeWithLayer(IntOffset.Zero) {
                clip = true
                shape = CollapseBoundsShape(
                    collapseHeight = elementCollapseHeightState.intValue,
                )
            }
        }
    }

    private data class CollapseBoundsShape(
        private val collapseHeight: Int,
    ) : Shape {

        override fun createOutline(
            size: Size,
            layoutDirection: LayoutDirection,
            density: Density,
        ): Outline {
            val collapseRect = size.toRect().run {
                copy(top = min(top + collapseHeight, bottom))
            }
            return Outline.Rectangle(collapseRect)
        }
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
    var clipToCollapseHeightListener: ((Int) -> Unit)? = null,
)

private val Placeable.columnParentData: CollapsingTopBarColumnParentData?
    get() = parentData as? CollapsingTopBarColumnParentData
