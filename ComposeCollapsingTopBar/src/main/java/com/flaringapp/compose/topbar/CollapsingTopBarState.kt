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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.Snapshot
import com.flaringapp.compose.topbar.snap.CollapsingTopBarSnapScope
import kotlin.math.max
import kotlin.math.min

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

@Stable
class CollapsingTopBarState internal constructor(
    initialHeight: Float,
) : ScrollableState,
    CollapsingTopBarControls,
    CollapsingTopBarSnapScope {

    constructor(
        isExpanded: Boolean = true,
    ) : this(
        initialHeight = if (isExpanded) Float.MAX_VALUE else 0f
    )

    val layoutInfo: CollapsingTopBarLayoutInfo
        get() = layoutInfoState.value

    private val layoutInfoState = mutableStateOf(
        CollapsingTopBarLayoutInfo(
            height = initialHeight,
            collapsedHeight = 0,
            expandedHeight = Int.MAX_VALUE,
        )
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

    private fun onScroll(delta: Float): Float {
        val canConsumeDelta = if (delta < 0) {
            max(layoutInfo.collapsedHeight - layoutInfo.height, delta)
        } else {
            min(layoutInfo.expandedHeight - layoutInfo.height, delta)
        }

        if (canConsumeDelta == 0f) return 0f

        layoutInfoState.value = layoutInfo.copy(
            height = layoutInfo.height + canConsumeDelta
        )
        return canConsumeDelta
    }
    //endregion

    //region Controls
    override suspend fun expand(
        animationSpec: AnimationSpec<Float>,
    ) = animateScrollBy(
        offset = layoutInfo.expandedHeight - layoutInfo.height,
        animationSpec = animationSpec,
    )

    override suspend fun collapse(
        animationSpec: AnimationSpec<Float>,
    ) = animateScrollBy(
        offset = layoutInfo.collapsedHeight - layoutInfo.height,
        animationSpec = animationSpec,
    )
    //endregion

    override suspend fun snapWithProgress(
        wasMovingUp: Boolean,
        action: suspend CollapsingTopBarControls.(progress: Float) -> Unit,
    ) {
        action.invoke(this, layoutInfo.collapseProgress)
    }

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

        val Saver: Saver<CollapsingTopBarState, Float> = Saver(
            save = { it.layoutInfo.height },
            restore = { CollapsingTopBarState(initialHeight = it) },
        )
    }
}

data class CollapsingTopBarLayoutInfo(
    /**
     * [height] indicates current height of the top bar.
     */
    val height: Float,

    /**
     * [collapsedHeight] indicates the minimum height of the collapsing top bar. The top bar
     * may collapse its height to [collapsedHeight] but not smaller.
     */
    val collapsedHeight: Int,

    /**
     * [expandedHeight] indicates the maximum height of the collapsing top bar. The top bar
     * may expand its height to [expandedHeight] but not larger.
     */
    val expandedHeight: Int,
) {

    val collapseProgress: Float
        get() =
            if (collapsedHeight == expandedHeight) {
                1f
            } else {
                ((height - collapsedHeight) / collapsibleDistance).coerceIn(0f, 1f)
            }

    val collapsibleDistance: Int
        get() = expandedHeight - collapsedHeight

    val isCollapsed: Boolean
        get() = height == collapsedHeight.toFloat()

    val isExpanded: Boolean
        get() = height == expandedHeight.toFloat()
}
