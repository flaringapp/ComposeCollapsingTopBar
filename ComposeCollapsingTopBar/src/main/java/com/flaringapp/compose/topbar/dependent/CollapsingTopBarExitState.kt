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

package com.flaringapp.compose.topbar.dependent

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Modifier
import com.flaringapp.compose.topbar.CollapsingTopBarControls
import com.flaringapp.compose.topbar.CollapsingTopBarState
import com.flaringapp.compose.topbar.snap.CollapsingTopBarSnapScope
import kotlin.math.max
import kotlin.math.min

@Composable
fun rememberCollapsingTopBarExitState(
    isExited: Boolean = false,
): CollapsingTopBarExitState {
    return rememberSaveable(isExited, saver = CollapsingTopBarExitState.Saver) {
        CollapsingTopBarExitState(
            isExited = isExited,
        )
    }
}

fun Modifier.collapsingTopBarExitStateConnection(
    topBarState: CollapsingTopBarState,
    exitState: CollapsingTopBarExitState,
): Modifier = this.collapsingTopBarDependentStateConnection(topBarState) {
    exitState.updateLayoutInfo(
        collapsedHeight = it.collapsedHeight,
    )
}

@Stable
class CollapsingTopBarExitState internal constructor(
    initialExitHeight: Float,
) : ScrollableState,
    CollapsingTopBarControls,
    CollapsingTopBarSnapScope {

    constructor(
        isExited: Boolean = false,
    ) : this(
        initialExitHeight = if (isExited) INITIALLY_EXITED_HEIGHT else 0f,
    )

    val isEnabled: Boolean
        get() = collapsedHeight > 0

    val exitHeight: Float
        get() = packedExitHeightState.floatValue.coerceAtMost(collapsedHeight)

    private val collapsedHeight: Float
        get() = collapsedHeightState.floatValue

    // Actual height or INITIALLY_EXITED_HEIGHT if initially exited until first updateLayoutInfo
    private val packedExitHeightState = mutableFloatStateOf(initialExitHeight)

    private val collapsedHeightState = mutableFloatStateOf(0f)

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
            max(exitHeight - collapsedHeight, delta)
        } else {
            min(exitHeight, delta)
        }

        if (canConsumeDelta == 0f) return 0f

        packedExitHeightState.floatValue -= canConsumeDelta
        return canConsumeDelta
    }
    //endregion

    //region Controls
    override suspend fun expand(
        animationSpec: AnimationSpec<Float>,
    ) = animateScrollBy(
        offset = exitHeight,
        animationSpec = animationSpec,
    )

    override suspend fun collapse(
        animationSpec: AnimationSpec<Float>,
    ) = animateScrollBy(
        offset = exitHeight - collapsedHeight,
        animationSpec = animationSpec,
    )
    //endregion

    override suspend fun snapWithProgress(
        wasMovingUp: Boolean,
        action: suspend CollapsingTopBarControls.(progress: Float) -> Unit,
    ) {
        if (!isEnabled) return

        val progress = 1f - exitHeight / collapsedHeight
        action.invoke(this, progress)
    }

    fun reset() {
        packedExitHeightState.floatValue = 0f
        collapsedHeightState.floatValue = 0f
    }

    internal fun updateLayoutInfo(collapsedHeight: Int) {
        val lastExitHeight: Float
        val lastCollapsedHeight: Float
        Snapshot.withoutReadObservation {
            lastExitHeight = exitHeight
            lastCollapsedHeight = this.collapsedHeight
        }

        val wasExited = lastCollapsedHeight > 0 && lastExitHeight == lastCollapsedHeight
        if (wasExited || lastExitHeight > collapsedHeight) {
            packedExitHeightState.floatValue = collapsedHeight.toFloat()
        }

        collapsedHeightState.floatValue = collapsedHeight.toFloat()
    }

    companion object {

        private const val INITIALLY_EXITED_HEIGHT = Float.MAX_VALUE

        val Saver: Saver<CollapsingTopBarExitState, Float> = Saver(
            save = {
                when {
                    it.collapsedHeight == 0f -> it.packedExitHeightState.floatValue
                    it.exitHeight == it.collapsedHeight -> INITIALLY_EXITED_HEIGHT
                    else -> it.exitHeight
                }
            },
            restore = { CollapsingTopBarExitState(initialExitHeight = it) },
        )
    }
}
