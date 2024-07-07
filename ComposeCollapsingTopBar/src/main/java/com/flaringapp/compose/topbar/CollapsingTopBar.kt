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

                val placeableProgress =
                    layoutInfo.height.coerceAtMost(placeable.height.toFloat()) / placeable.height

                parentData?.progressListener?.onProgressUpdate(
                    totalProgress = progress,
                    itemProgress = placeableProgress,
                )

                parentData?.parallaxRatio?.let { parallaxRatio ->
                    placeable.placeRelative(
                        x = 0,
                        y = -(collapsibleDistance * (1 - progress) * parallaxRatio).roundToInt(),
                    )
                    return@layout
                }

                placeable.placeRelative(0, 0)
            }
        }
    }
}

@LayoutScopeMarker
@Immutable
interface CollapsingTopBarScope {

    fun Modifier.progress(listener: CollapsingTopBarProgressListener): Modifier

    fun Modifier.parallax(ratio: Float): Modifier

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
