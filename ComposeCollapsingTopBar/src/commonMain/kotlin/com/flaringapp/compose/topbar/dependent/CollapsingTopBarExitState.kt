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
import androidx.compose.runtime.annotation.FrequentlyChangingValue
import androidx.compose.runtime.annotation.RememberInComposition
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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

/**
 * Creates a [CollapsingTopBarExitState] that is remembered across compositions.
 *
 * Changes to the provided initial values will **not** result in the state being recreated or
 * changed in any way if it has already been created. Consider using available controls of state
 * object instead.
 *
 * @param isExited the initial state of top bar exit height being collapsed (exited).
 */
@Composable
fun rememberCollapsingTopBarExitState(
    isExited: Boolean = false,
): CollapsingTopBarExitState {
    return rememberSaveable(saver = CollapsingTopBarExitState.Saver) {
        CollapsingTopBarExitState(
            isExited = isExited,
        )
    }
}

/**
 * A Modifier for connecting [CollapsingTopBarExitState] to [CollapsingTopBarState] to keep track
 * of top bar measurement updates.
 *
 * This modifier is required to make [CollapsingTopBarExitState] work.
 *
 * Must be applied on [com.flaringapp.compose.topbar.CollapsingTopBar].
 *
 * @see [collapsingTopBarDependentStateConnection]
 */
fun Modifier.collapsingTopBarExitStateConnection(
    topBarState: CollapsingTopBarState,
    exitState: CollapsingTopBarExitState,
): Modifier = this.collapsingTopBarDependentStateConnection(topBarState) {
    exitState.updateLayoutInfo(
        collapsedHeight = it.collapsedHeight,
    )
}

/**
 * A state object that can be hoisted to control and observe top bar exiting.
 *
 * Exiting is a movement of collapsed top bar off screen. This mechanism is managed explicitly
 * separately from collapsing not to interfere with collapse progress tracking.
 *
 * Exit offset [exitHeight] must be manually applied on collapsing top bar to take effect. State
 * also requires to be supplied with top bar measurement updates via
 * [collapsingTopBarExitStateConnection].
 *
 * This state also supports disabled mode (when it's always entered/expanded). It is disabled by
 * default until measurement data is supplied. It can be disabled any time with [reset] method.
 *
 * In most cases, this will be created via [rememberCollapsingTopBarExitState].
 */
@Stable
class CollapsingTopBarExitState @RememberInComposition internal constructor(
    initialExitHeight: Float,
) : ScrollableState,
    CollapsingTopBarControls,
    CollapsingTopBarSnapScope {

    /**
     * @param isExited the initial state of top bar exit height being collapsed (exited).
     */
    @RememberInComposition
    constructor(
        isExited: Boolean = false,
    ) : this(
        initialExitHeight = if (isExited) INITIALLY_EXITED_HEIGHT else 0f,
    )

    /**
     * Indicates if state is currently enabled. State becomes enabled upon receiving first
     * measurement update. Disabled state is always entered/expanded.
     */
    val isEnabled: Boolean
        get() = collapsedHeight > 0

    /**
     * The amount of exited height. Value 0f means it's fully entered/expanded, and it can grow
     * (positive) all the way to collapsed top bar height to become fully exited. Collapsed top bar
     * height is updated in scope of measurement updates.
     */
    @get:FrequentlyChangingValue
    val exitHeight: Float
        get() = packedExitHeightState.floatValue.coerceAtMost(collapsedHeight)

    /**
     * The current exit progress of the top bar, where 0f means fully exited, and 1f means fully
     * entered.
     */
    @get:FrequentlyChangingValue
    val exitProgress: Float
        get() = when {
            exitHeight == 0f -> 1f
            collapsedHeight == 0f -> 0f
            else -> 1f - (exitHeight / collapsedHeight).coerceIn(0f, 1f)
        }

    /**
     * Whether top bar exit height is fully exited/collapsed. Disabled state is never exited.
     */
    val isFullyExited: Boolean by derivedStateOf {
        isEnabled && exitHeight == collapsedHeight
    }

    /**
     * Whether top bar exit height is fully entered/expanded. Disabled state is always entered.
     */
    val isFullyEntered: Boolean by derivedStateOf {
        exitHeight == 0f
    }

    /**
     * The entered (collapsed) height of the collapsing top bar, highest [exitHeight] can go.
     */
    val collapsedHeight: Float
        get() = collapsedHeightState.floatValue

    /**
     * Backing state for [exitHeight]. Can be set to actual exit height, or
     * [INITIALLY_EXITED_HEIGHT] if it's initially exited until first [updateLayoutInfo].
     */
    private val packedExitHeightState = mutableFloatStateOf(initialExitHeight)

    /**
     * Backing state for [collapsedHeight].
     */
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

    /**
     * Handles ongoing scroll delta by updating current top bar exit height.
     *
     * @param delta the distance scrolled.
     *
     * @return the amount of scroll consumed.
     */
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
    ) = animateHeightTo(
        currentHeight = -exitHeight,
        targetHeight = 0f,
        animationSpec = animationSpec,
    )

    override suspend fun collapse(
        animationSpec: AnimationSpec<Float>,
    ) = animateHeightTo(
        currentHeight = -exitHeight,
        targetHeight = -collapsedHeight,
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

    /**
     * Disables this state and resets current exit height to entered/expanded.
     */
    fun reset() {
        packedExitHeightState.floatValue = 0f
        collapsedHeightState.floatValue = 0f
    }

    /**
     * Applies new measurements from [CollapsingTopBarState].
     *
     * May update current exit height if:
     * - state is initially exited, and receives its first measurements update. Then exit height
     * is going to change from [INITIALLY_EXITED_HEIGHT] to top bar collapsed height.
     * - state is fully exited, but top bar collapsed height has decreased. Then exit height is
     * going to stay fully exited and change from previous to new collapsed height.
     *
     * @param collapsedHeight the current minimum/collapsed height of top bar.
     *
     * @see collapsingTopBarExitStateConnection
     */
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

        /**
         * An exit height state value to indicate fully exited height until first measurement update.
         */
        private const val INITIALLY_EXITED_HEIGHT = Float.MAX_VALUE

        /**
         * The default [Saver] implementation for [CollapsingTopBarExitState].
         */
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
