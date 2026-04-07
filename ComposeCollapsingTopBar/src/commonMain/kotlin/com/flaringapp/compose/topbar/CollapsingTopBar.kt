/*
 * Copyright 2026 Flaringapp
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

package com.flaringapp.compose.topbar

import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import com.flaringapp.compose.topbar.nestedcollapse.CollapsingTopBarNestedCollapseElement
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * A basic container for collapsing content. Places its children like a Box, on top of each other.
 * Top bar always occupies maximum (expanded) height while hoisting variable collapsing height in
 * [state]. Minimum (collapsed) height is always equal to the smallest child or
 * [CollapsingTopBarScope.nestedCollapse] height.
 *
 * Visual collapsing is achieved by [clipToBounds], although additional layout mechanism is likely
 * to be handy, e.g. [com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffold].
 *
 * Each child may actively participate in collapsing process with [CollapsingTopBarScope] modifiers.
 *
 * Advanced collapsing techniques can be achieved using:
 * - transformations based on [CollapsingTopBarState.layoutInfo]. When exact bounds matter, use
 * [CollapsingTopBarState.hasMeasured] to distinguish placeholder values from actual measurements.
 * - custom layout logic with [CollapsingTopBarNestedCollapseElement]
 *
 * @param modifier the [Modifier] to be applied to this top bar.
 * @param state the state that manages this top bar.
 * @param clipToBounds the flag whether or not to automatically clip top bar [content] to the
 * actual collapse height.
 *
 * @see CollapsingTopBarScope
 * @see com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffold
 * @see com.flaringapp.compose.topbar.nestedcollapse.CollapsingTopBarColumn
 */
@Composable
public fun CollapsingTopBar(
    modifier: Modifier = Modifier,
    state: CollapsingTopBarState = rememberCollapsingTopBarState(),
    clipToBounds: Boolean = true,
    content: @Composable CollapsingTopBarScope.() -> Unit,
) {
    val measurePolicy = remember(state) {
        CollapsingTopBarMeasurePolicy(state)
    }

    val clipToBoundsModifier = if (clipToBounds) {
        Modifier.graphicsLayer {
            clip = true
            shape = CollapseBoundsShape(
                collapseHeight = state.layoutInfo.collapseHeightDelta,
            )
        }
    } else {
        Modifier
    }

    Layout(
        modifier = modifier
            .then(clipToBoundsModifier),
        measurePolicy = measurePolicy,
        content = { CollapsingTopBarScopeInstance.content() },
    )
}

private class CollapsingTopBarMeasurePolicy(
    private val state: CollapsingTopBarState,
) : MeasurePolicy {

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

        // Update state layout info
        val measuredLayoutInfo = run {
            val collapsedHeight = resolveCollapsedHeight(placeables)
                .let { constraints.constrainHeight(it) }

            val expandedHeight = placeables.maxOf { it.height }
                .let { constraints.constrainHeight(it) }

            state.applyMeasureResult(
                collapsedHeight = collapsedHeight,
                expandedHeight = expandedHeight,
            )
        }

        val width = placeables.maxOf { it.width }
            .let { constraints.constrainWidth(it) }

        val topBarSize = IntSize(width, measuredLayoutInfo.expandedHeight)

        return layout(topBarSize.width, topBarSize.height) {
            val layoutInfo = state.layoutInfo

            placeables.forEach { placeable ->
                val offset = processPlaceable(
                    placeable = placeable,
                    layoutInfo = layoutInfo,
                    topBarSize = topBarSize,
                    layoutDirection = layoutDirection,
                )
                placeable.place(offset)
            }
        }
    }
}

private fun resolveCollapsedHeight(placeables: List<Placeable>): Int {
    var nestedCollapseMinHeight = Int.MAX_VALUE
    var regularCollapseMinHeight = Int.MAX_VALUE

    placeables.forEach { placeable ->
        val parentData = placeable.topBarParentData

        parentData?.nestedCollapseElement?.minHeight?.let {
            nestedCollapseMinHeight = min(nestedCollapseMinHeight, it)
            return@forEach
        }

        if (parentData?.isFloating == true) {
            return@forEach
        }

        regularCollapseMinHeight = min(regularCollapseMinHeight, placeable.height)
    }

    if (nestedCollapseMinHeight == Int.MAX_VALUE) {
        nestedCollapseMinHeight = 0
    }
    if (regularCollapseMinHeight == Int.MAX_VALUE) {
        regularCollapseMinHeight = 0
    }

    if (nestedCollapseMinHeight > 0) {
        return nestedCollapseMinHeight
    }

    return regularCollapseMinHeight
}

private fun processPlaceable(
    placeable: Placeable,
    layoutInfo: CollapsingTopBarLayoutInfo,
    topBarSize: IntSize,
    layoutDirection: LayoutDirection,
): IntOffset {
    val currentTopBarHeight = layoutInfo.height.roundToInt()

    val parentData = placeable.topBarParentData

    val alignmentOffset = (parentData?.alignment ?: Alignment.TopStart).align(
        size = IntSize(placeable.width, placeable.height),
        space = topBarSize,
        layoutDirection = layoutDirection,
    )

    val parallaxOffsetY = parentData?.parallaxRatio?.let { parallaxRatio ->
        -(layoutInfo.collapseHeightDelta * parallaxRatio).roundToInt()
    } ?: 0

    val pinOffsetY = parentData?.pin?.let { pin ->
        val baseY = alignmentOffset.y + parallaxOffsetY
        val maxAllowedY = currentTopBarHeight - placeable.height

        var pinnedY = baseY.coerceAtMost(maxAllowedY)
        if (pin.stopAtTop) {
            pinnedY = pinnedY.coerceAtLeast(0)
        }

        pinnedY - baseY
    } ?: 0

    val placementOffsetY = parallaxOffsetY + pinOffsetY

    val collapsibleSegmentStart = alignmentOffset.y.coerceAtLeast(layoutInfo.collapsedHeight)
    val collapsibleSegmentEnd = alignmentOffset.y + placeable.height
    val placeableCollapsibleDistance =
        (collapsibleSegmentEnd - collapsibleSegmentStart).coerceAtLeast(0)

    val placeableProgress = if (placeableCollapsibleDistance == 0) {
        1f
    } else {
        val visibleCollapsibleSegmentStart = (collapsibleSegmentStart + placementOffsetY)
            .coerceIn(layoutInfo.collapsedHeight, currentTopBarHeight)
        val visibleCollapsibleSegmentEnd = (collapsibleSegmentEnd + placementOffsetY)
            .coerceIn(layoutInfo.collapsedHeight, currentTopBarHeight)
        val visibleCollapsibleDistance =
            (visibleCollapsibleSegmentEnd - visibleCollapsibleSegmentStart).coerceAtLeast(0)

        visibleCollapsibleDistance.toFloat() / placeableCollapsibleDistance
    }

    parentData?.progressListener?.onProgressUpdate(
        totalProgress = layoutInfo.collapseProgress,
        itemProgress = placeableProgress,
    )

    return IntOffset(alignmentOffset.x, alignmentOffset.y + placementOffsetY)
}

/**
 * Scope for the children of [CollapsingTopBar].
 */
@LayoutScopeMarker
@Immutable
public interface CollapsingTopBarScope {

    /**
     * Align the element within the bounds of [CollapsingTopBar].
     * Aligned elements still contribute to resolving minimum (collapsed) height among all
     * elements. To exclude an aligned element from minimum height resolution and keep overlay-like
     * behavior, also apply [floating].
     * Only the last modifier in chain takes effect.
     *
     * @param alignment the alignment of the element inside the top bar.
     */
    public fun Modifier.align(alignment: Alignment): Modifier

    /**
     * Position the element dynamically while collapsing by offsetting up by [ratio] as a fraction
     * of collapsible height. Value 0f means there is no parallax and the element simply sits in
     * place while top bar is collapsing, whereas 1f will make the element follow the collapse
     * motion.
     */
    public fun Modifier.parallax(ratio: Float): Modifier

    /**
     * Make the element ride with top bar collapse after its bottom touches current top bar bottom.
     *
     * By default, pinned content continues riding even after crossing the top edge. Set
     * [stopAtTop] to true to stop the element at y = 0 instead.
     *
     * This modifier affects item placement only and does not participate in resolving minimum
     * (collapsed) height.
     */
    public fun Modifier.pin(stopAtTop: Boolean = false): Modifier

    /**
     * Exclude the element from resolving minimum height among all elements. Useful for 'floating'
     * elements with custom motion on collapse.
     */
    public fun Modifier.floating(): Modifier

    /**
     * Registers a progress listener to be notified every time top bar collapse height changes.
     * Only the last modifier in chain takes effect.
     *
     * @param listener The listener that gets notified of every collapse progress update.
     *
     * @see CollapsingTopBarProgressListener
     */
    public fun Modifier.progress(listener: CollapsingTopBarProgressListener): Modifier

    /**
     * Define an explicit minimum (collapsed) height nested collapse connection between the top bar
     * and this element. The element is responsible for dispatching its own minimum height using
     * [element] handle. This value is read by the top bar to calculate total minimum height
     * among all children.
     *
     * @see [com.flaringapp.compose.topbar.nestedcollapse.CollapsingTopBarColumn]
     */
    public fun Modifier.nestedCollapse(element: CollapsingTopBarNestedCollapseElement): Modifier
}

private object CollapsingTopBarScopeInstance : CollapsingTopBarScope {

    override fun Modifier.align(alignment: Alignment): Modifier {
        return then(AlignmentModifier(alignment))
    }

    override fun Modifier.parallax(ratio: Float): Modifier {
        return then(ParallaxModifier(ratio))
    }

    override fun Modifier.pin(stopAtTop: Boolean): Modifier {
        return then(PinModifier(stopAtTop))
    }

    override fun Modifier.floating(): Modifier {
        return then(FloatingModifier())
    }

    override fun Modifier.progress(listener: CollapsingTopBarProgressListener): Modifier {
        return then(ProgressListenerModifier(listener))
    }

    override fun Modifier.nestedCollapse(
        element: CollapsingTopBarNestedCollapseElement,
    ): Modifier {
        return then(NestedCollapseModifier(element))
    }
}

private class AlignmentModifier(
    private val alignment: Alignment,
) : CollapsingTopBarParentDataModifier() {
    override fun modifyParentData(parentData: CollapsingTopBarParentData) {
        parentData.alignment = alignment
    }
}

private class ParallaxModifier(
    private val ratio: Float,
) : CollapsingTopBarParentDataModifier() {
    override fun modifyParentData(parentData: CollapsingTopBarParentData) {
        parentData.parallaxRatio = ratio
    }
}

private class PinModifier(
    private val stopAtTop: Boolean,
) : CollapsingTopBarParentDataModifier() {
    override fun modifyParentData(parentData: CollapsingTopBarParentData) {
        parentData.pin = CollapsingTopBarParentData.Pin(
            stopAtTop = stopAtTop,
        )
    }
}

private class FloatingModifier : CollapsingTopBarParentDataModifier() {
    override fun modifyParentData(parentData: CollapsingTopBarParentData) {
        parentData.isFloating = true
    }
}

private class ProgressListenerModifier(
    private val listener: CollapsingTopBarProgressListener,
) : CollapsingTopBarParentDataModifier() {
    override fun modifyParentData(parentData: CollapsingTopBarParentData) {
        parentData.progressListener = listener
    }
}

private class NestedCollapseModifier(
    private val element: CollapsingTopBarNestedCollapseElement,
) : CollapsingTopBarParentDataModifier() {
    override fun modifyParentData(parentData: CollapsingTopBarParentData) {
        parentData.nestedCollapseElement = element
    }
}

private abstract class CollapsingTopBarParentDataModifier : ParentDataModifier {

    override fun Density.modifyParentData(parentData: Any?): Any {
        val data = parentData as? CollapsingTopBarParentData ?: CollapsingTopBarParentData()
        this@CollapsingTopBarParentDataModifier.modifyParentData(data)
        return data
    }

    protected abstract fun modifyParentData(parentData: CollapsingTopBarParentData)
}

private data class CollapsingTopBarParentData(
    var alignment: Alignment? = null,
    var parallaxRatio: Float? = null,
    var pin: Pin? = null,
    var isFloating: Boolean = false,
    var progressListener: CollapsingTopBarProgressListener? = null,
    var nestedCollapseElement: CollapsingTopBarNestedCollapseElement? = null,
) {

    data class Pin(
        val stopAtTop: Boolean,
    )
}

private val Placeable.topBarParentData: CollapsingTopBarParentData?
    get() = parentData as? CollapsingTopBarParentData

private data class CollapseBoundsShape(
    private val collapseHeight: Float,
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val collapseRect = size.toRect().run {
            copy(bottom = maxOf(top, bottom - collapseHeight))
        }
        return Outline.Rectangle(collapseRect)
    }
}
