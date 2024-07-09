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

package com.flaringapp.compose.topbar

import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import com.flaringapp.compose.topbar.nestedcollapse.CollapsingTopBarNestedCollapseElement
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * A basic container for collapsing content. Places its children like a Box, on top of each other.
 * Top bar always occupies maximum (expanded) height while hoisting variable collapsing height in
 * [state]. Minimum (collapsed) height is always equal to the smallest child height.
 *
 * Visual collapsing is achieved by [clipToBounds], although additional layout mechanism is likely
 * to be handy, e.g. [com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffold].
 *
 * Each child may actively participate in collapsing process with [CollapsingTopBarScope] modifiers.
 *
 * Advanced collapsing techniques can be achieved using:
 * - transformations based on [CollapsingTopBarState.layoutInfo]
 * - custom layout logic with [CollapsingTopBarNestedCollapseElement]
 *
 * @param state the state that manages this top bar.
 * @param modifier the [Modifier] to be applied to this top bar.
 * @param clipToBounds the flag whether or not to automatically clip top bar [content] to the
 * actual collapse height.
 *
 * @see CollapsingTopBarScope
 * @see com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffold
 * @see com.flaringapp.compose.topbar.nestedcollapse.CollapsingTopBarColumn
 */
@Composable
fun CollapsingTopBar(
    state: CollapsingTopBarState,
    modifier: Modifier = Modifier,
    clipToBounds: Boolean = true,
    content: @Composable CollapsingTopBarScope.() -> Unit,
) {
    val measurePolicy = remember(state) {
        CollapsingTopBarMeasurePolicy(state)
    }

    val clipToBoundsModifier = if (clipToBounds) {
        Modifier.drawWithContent {
            clipRect(
                bottom = state.layoutInfo.height,
            ) {
                this@drawWithContent.drawContent()
            }
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
            val nestedCollapseMinHeight = placeables.minOf {
                it.topBarParentData?.nestedCollapseElement?.minHeight ?: Int.MAX_VALUE
            }.takeIf { it != Int.MAX_VALUE }

            val collapsedHeight = max(
                placeables.minOf { it.height },
                nestedCollapseMinHeight ?: 0,
            )
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

        return layout(width, measuredLayoutInfo.expandedHeight) {
            val layoutInfo = state.layoutInfo
            val progress = layoutInfo.collapseProgress
            val collapsibleDistance = layoutInfo.collapsibleDistance

            placeables.forEach { placeable ->
                val parentData = placeable.topBarParentData

                val placeableCollapsibleDistance = placeable.height - layoutInfo.collapsedHeight
                val placeableProgress = if (placeableCollapsibleDistance == 0) {
                    1f
                } else {
                    val placeableCollapseHeight = with(layoutInfo) {
                        height.coerceAtMost(placeable.height.toFloat()) - collapsedHeight
                    }
                    placeableCollapseHeight / placeableCollapsibleDistance
                }

                parentData?.progressListener?.onProgressUpdate(
                    totalProgress = progress,
                    itemProgress = placeableProgress,
                )

                parentData?.parallaxRatio?.let { parallaxRatio ->
                    placeable.placeRelative(
                        x = 0,
                        y = -(collapsibleDistance * (1 - progress) * parallaxRatio).roundToInt(),
                    )
                    return@forEach
                }

                placeable.placeRelative(0, 0)
            }
        }
    }
}

/**
 * Scope for the children of [CollapsingTopBar].
 */
@LayoutScopeMarker
@Immutable
interface CollapsingTopBarScope {

    /**
     * Registers a progress listener to be notified every time top bar collapse height changes.
     * Only the last modifier in chain takes effect.
     *
     * @param listener The listener that gets notified of every collapse progress update.
     *
     * @see CollapsingTopBarProgressListener
     */
    fun Modifier.progress(listener: CollapsingTopBarProgressListener): Modifier

    /**
     * Position the element dynamically while collapsing by offsetting up by [ratio] as a fraction
     * of collapsible height. Value 0f means there is no parallax and the element simply sits in
     * place while top bar is collapsing, whereas 1f will make the element follow the collapse
     * motion.
     */
    fun Modifier.parallax(ratio: Float): Modifier

    /**
     * Define an explicit minimum (collapsed) height nested collapse connection between the top bar
     * and this element. The element is responsible for dispatching its own minimum height using
     * [element] handle. This value is read by the top bar to calculate total minimum height
     * among all children.
     *
     * @see [com.flaringapp.compose.topbar.nestedcollapse.CollapsingTopBarColumn]
     */
    fun Modifier.nestedCollapse(element: CollapsingTopBarNestedCollapseElement): Modifier
}

private object CollapsingTopBarScopeInstance : CollapsingTopBarScope {

    override fun Modifier.progress(listener: CollapsingTopBarProgressListener): Modifier {
        return then(ProgressListenerModifier(listener))
    }

    override fun Modifier.parallax(ratio: Float): Modifier {
        return then(ParallaxModifier(ratio))
    }

    override fun Modifier.nestedCollapse(
        element: CollapsingTopBarNestedCollapseElement,
    ): Modifier {
        return then(NestedCollapseModifier(element))
    }
}

private class ProgressListenerModifier(
    private val listener: CollapsingTopBarProgressListener,
) : CollapsingTopBarParentDataModifier() {
    override fun modifyParentData(parentData: CollapsingTopBarParentData) {
        parentData.progressListener = listener
    }
}

private class ParallaxModifier(
    private val ratio: Float,
) : CollapsingTopBarParentDataModifier() {
    override fun modifyParentData(parentData: CollapsingTopBarParentData) {
        parentData.parallaxRatio = ratio
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
    var progressListener: CollapsingTopBarProgressListener? = null,
    var parallaxRatio: Float? = null,
    var nestedCollapseElement: CollapsingTopBarNestedCollapseElement? = null,
)

private val Placeable.topBarParentData: CollapsingTopBarParentData?
    get() = parentData as? CollapsingTopBarParentData
