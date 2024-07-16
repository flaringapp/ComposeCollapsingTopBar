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

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.Snapshot
import com.flaringapp.compose.topbar.snap.CollapsingTopBarSnapScope
import kotlin.math.max
import kotlin.math.min

/**
 * Creates a [CollapsingTopBarState] that is remembered across compositions.
 *
 * Changes to the provided initial values will **not** result in the state being recreated or
 * changed in any way if it has already been created. Consider using available controls of state
 * object instead.
 *
 * @param isExpanded the initial state of top bar height being expanded.
 */
@Composable
fun rememberCollapsingTopBarState(
    isExpanded: Boolean = true,
): CollapsingTopBarState {
    return rememberSaveable(saver = CollapsingTopBarState.Saver) {
        CollapsingTopBarState(
            isExpanded = isExpanded,
        )
    }
}

/**
 * A state object that can be hoisted to control and observe top bar collapsing.
 *
 * In most cases, this will be created via [rememberCollapsingTopBarState].
 */
@Stable
class CollapsingTopBarState internal constructor(
    initialHeight: Float,
) : ScrollableState,
    CollapsingTopBarControls,
    CollapsingTopBarSnapScope {

    /**
     * @param isExpanded the initial state of top bar height being expanded.
     */
    constructor(
        isExpanded: Boolean = true,
    ) : this(
        initialHeight = if (isExpanded) Float.MAX_VALUE else 0f,
    )

    /**
     * Whether top bar height has reached its minimum height.
     */
    val isCollapsed: Boolean by derivedStateOf {
        layoutInfo.isCollapsed
    }

    /**
     * Whether top bar height has reached its maximum height.
     */
    val isExpanded: Boolean by derivedStateOf {
        layoutInfo.isExpanded
    }

    /**
     * The layout info object calculated during the last layout pass.
     *
     * Note that this property is observable and is updated after every scroll or remeasure.
     * If you use it in the composable function it will be recomposed on every change causing
     * potential performance issues including infinity recomposition loop.
     * Therefore, avoid using it in the composition.
     */
    val layoutInfo: CollapsingTopBarLayoutInfo
        get() = layoutInfoState.value

    private val layoutInfoState = mutableStateOf(
        CollapsingTopBarLayoutInfo(
            height = initialHeight,
            collapsedHeight = 0,
            expandedHeight = Int.MAX_VALUE,
        ),
    )

    //region Scrolling
    private val scrollableState = ScrollableState { onScroll(it) }

    override suspend fun scroll(
        scrollPriority: MutatePriority,
        block: suspend ScrollScope.() -> Unit,
    ) = scrollableState.scroll(scrollPriority, block)

    override fun dispatchRawDelta(delta: Float): Float =
        scrollableState.dispatchRawDelta(delta)

    override val isScrollInProgress: Boolean
        get() = scrollableState.isScrollInProgress

    /**
     * Handles ongoing scroll delta by updating current top bar height.
     *
     * @param delta the distance scrolled.
     *
     * @return the amount of scroll consumed.
     */
    private fun onScroll(delta: Float): Float {
        val canConsumeDelta = if (delta < 0) {
            max(-layoutInfo.expandHeightDelta, delta)
        } else {
            min(layoutInfo.collapseHeightDelta, delta)
        }

        if (canConsumeDelta == 0f) return 0f

        layoutInfoState.value = layoutInfo.copy(
            height = layoutInfo.height + canConsumeDelta,
        )
        return canConsumeDelta
    }
    //endregion

    //region Controls
    override suspend fun expand(
        animationSpec: AnimationSpec<Float>,
    ) = animateScrollBy(
        offset = layoutInfo.collapseHeightDelta,
        animationSpec = animationSpec,
    )

    override suspend fun collapse(
        animationSpec: AnimationSpec<Float>,
    ) = animateScrollBy(
        offset = -layoutInfo.expandHeightDelta,
        animationSpec = animationSpec,
    )
    //endregion

    override suspend fun snapWithProgress(
        wasMovingUp: Boolean,
        action: suspend CollapsingTopBarControls.(progress: Float) -> Unit,
    ) {
        action.invoke(this, layoutInfo.collapseProgress)
    }

    /**
     * Updates [layoutInfoState] based on measured layout data.
     *
     * May update current height as well if:
     * - top bar was fully expanded, and max height increased, then keep expanded;
     * - height is no longer in min-max bounds, then coerce in new bounds.
     *
     * @param collapsedHeight the minimum (collapsed) height top bar can occupy.
     * @param expandedHeight the maximum (expanded) height top bar can occupy.
     *
     * @return the up to date layout info with changes applied.
     */
    internal fun applyMeasureResult(
        collapsedHeight: Int,
        expandedHeight: Int,
    ): CollapsingTopBarLayoutInfo {
        val lastLayoutInfo = Snapshot.withoutReadObservation { layoutInfo }

        val newHeight =
            if (lastLayoutInfo.isExpanded && expandedHeight > lastLayoutInfo.expandedHeight) {
                // Max height increases while being expanded - keep expanded
                expandedHeight.toFloat()
            } else {
                lastLayoutInfo.height.coerceIn(collapsedHeight.toFloat(), expandedHeight.toFloat())
            }

        return CollapsingTopBarLayoutInfo(
            height = newHeight,
            collapsedHeight = collapsedHeight,
            expandedHeight = expandedHeight,
        ).also {
            layoutInfoState.value = it
        }
    }

    companion object {

        /**
         * The default [Saver] implementation for [CollapsingTopBarState].
         */
        val Saver: Saver<CollapsingTopBarState, Float> = Saver(
            save = { it.layoutInfo.height },
            restore = { CollapsingTopBarState(initialHeight = it) },
        )
    }
}

/**
 * The current layout measurements state of collapsing top bar.
 */
data class CollapsingTopBarLayoutInfo(
    /**
     * The current collapsing height of the top bar.
     */
    val height: Float,

    /**
     * The minimum height of the collapsing top bar, lowest [height] can go.
     */
    val collapsedHeight: Int,

    /**
     * The maximum height of the collapsing top bar, highest [height] can go.
     */
    val expandedHeight: Int,
) {

    /**
     * The current collapse progress of the top bar, where 0f means fully collapsed, and 1f means
     * fully expanded.
     *
     * If top bar minimum and maximum height is the same, then it's considered expanded.
     */
    val collapseProgress: Float
        get() =
            if (collapsedHeight == expandedHeight) {
                1f
            } else {
                (expandHeightDelta / collapsibleDistance).coerceIn(0f, 1f)
            }

    /**
     * The height difference between current and expanded height, positive value.
     */
    val collapseHeightDelta: Float
        get() = expandedHeight - height

    /**
     * The height difference between current and collapsed height, positive value.
     */
    val expandHeightDelta: Float
        get() = height - collapsedHeight

    /**
     * The total variable height amount that may collapse.
     */
    internal val collapsibleDistance: Int
        get() = expandedHeight - collapsedHeight

    /**
     * Whether top bar height has reached its minimum height.
     */
    internal val isCollapsed: Boolean
        get() = height == collapsedHeight.toFloat()

    /**
     * Whether top bar height has reached its maximum height.
     */
    internal val isExpanded: Boolean
        get() = height == expandedHeight.toFloat()
}
