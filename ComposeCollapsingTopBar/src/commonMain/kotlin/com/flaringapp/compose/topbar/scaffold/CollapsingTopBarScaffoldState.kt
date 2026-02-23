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

package com.flaringapp.compose.topbar.scaffold

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.annotation.FrequentlyChangingValue
import androidx.compose.runtime.annotation.RememberInComposition
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import com.flaringapp.compose.topbar.CollapsingTopBarControls
import com.flaringapp.compose.topbar.CollapsingTopBarState
import com.flaringapp.compose.topbar.dependent.CollapsingTopBarExitState
import com.flaringapp.compose.topbar.snap.CollapsingTopBarSnapScope

/**
 * Creates a [CollapsingTopBarScaffoldState] that is remembered across compositions.
 *
 * Changes to the provided initial values will **not** result in the state being recreated or
 * changed in any way if it has already been created. Consider using available controls of state
 * object instead.
 *
 * @param isExpanded the initial state of top bar height being expanded. When false and used with
 * [CollapsingTopBarScaffoldScrollMode] that may exit, then initially it's fully exited.
 */
@Composable
fun rememberCollapsingTopBarScaffoldState(
    isExpanded: Boolean = true,
): CollapsingTopBarScaffoldState {
    return rememberSaveable(saver = CollapsingTopBarScaffoldState.Saver) {
        CollapsingTopBarScaffoldState(
            topBarState = CollapsingTopBarState(isExpanded = isExpanded),
            exitState = CollapsingTopBarExitState(isExited = !isExpanded),
        )
    }
}

/**
 * A state object that can be hoisted to control and observe top bar scaffold collapsing.
 *
 * Encapsulates top bar state [topBarState] and exit state [exitState] (may be disabled depending
 * on scroll mode used).
 *
 * In most cases, this will be created via [rememberCollapsingTopBarScaffoldState].
 */
@Stable
class CollapsingTopBarScaffoldState @RememberInComposition internal constructor(
    val topBarState: CollapsingTopBarState,
    val exitState: CollapsingTopBarExitState,
) : CollapsingTopBarControls,
    CollapsingTopBarSnapScope {

    /**
     * The current visual top bar height, either during collapse or exit.
     */
    @get:FrequentlyChangingValue
    val totalTopBarHeight: Float
        get() = topBarState.layoutInfo.height - exitState.exitHeight

    /**
     * Whether top bar is fully expanded and entered.
     */
    val isExpanded: Boolean by derivedStateOf {
        topBarState.isExpanded && exitState.isFullyEntered
    }

    /**
     * Whether top bar is fully collapsed and exited (if exit is enabled).
     */
    val isCollapsed: Boolean by derivedStateOf {
        topBarState.isCollapsed &&
            (!exitState.isEnabled || exitState.isFullyExited)
    }

    override suspend fun expand(animationSpec: AnimationSpec<Float>) {
        animateHeightTo(animationSpec) {
            topBarState.layoutInfo.expandedHeight.toFloat()
        }
    }

    override suspend fun collapse(animationSpec: AnimationSpec<Float>) {
        animateHeightTo(animationSpec) { canExit ->
            if (canExit) {
                0f
            } else {
                topBarState.layoutInfo.collapsedHeight.toFloat()
            }
        }
    }

    /**
     * Perform height animation on top bar. If top bar can exit, then both [exitState] and
     * [topBarState] scroll scopes are held while animation is running. Otherwise only
     * [topBarState] scroll scope is used. This helps locking scroll access and properly handling
     * cancellation in case animation is interrupted by user input.
     *
     * @see androidx.compose.foundation.gestures.ScrollableState.scroll
     */
    private suspend inline fun animateHeightTo(
        animationSpec: AnimationSpec<Float>,
        targetHeightProvider: (canExit: Boolean) -> Float,
    ) {
        val canExit = exitState.isEnabled
        val targetHeight = targetHeightProvider(canExit)

        if (!canExit) {
            topBarState.animateHeightTo(
                currentHeight = totalTopBarHeight,
                targetHeight = targetHeight,
                animationSpec = animationSpec,
            )
            return
        }

        val isCollapsing = targetHeight < totalTopBarHeight

        topBarState.scroll topBarScrollScope@{
            exitState.scroll exitScrollScope@{
                val scopeOrder = listOf(
                    this@topBarScrollScope,
                    this@exitScrollScope,
                ).let {
                    if (isCollapsing) it else it.asReversed()
                }

                val mergedScrollScope = object : ScrollScope {
                    override fun scrollBy(pixels: Float): Float {
                        val consumed = scopeOrder.fold(pixels) { left, scope ->
                            left - scope.scrollBy(left)
                        }
                        return pixels - consumed
                    }
                }

                mergedScrollScope.animateHeightTo(
                    currentHeight = totalTopBarHeight,
                    targetHeight = targetHeight,
                    animationSpec = animationSpec,
                )
            }
        }
    }

    override suspend fun snapWithProgress(
        wasMovingUp: Boolean,
        action: suspend CollapsingTopBarControls.(progress: Float) -> Unit,
    ) {
        val progress = totalTopBarHeight / topBarState.layoutInfo.expandedHeight
        action.invoke(this, progress)
    }

    companion object {

        /**
         * The default [Saver] implementation for [CollapsingTopBarScaffoldState].
         */
        val Saver: Saver<CollapsingTopBarScaffoldState, *> = listSaver(
            save = {
                listOf(
                    with(CollapsingTopBarState.Saver) { save(it.topBarState)!! },
                    with(CollapsingTopBarExitState.Saver) { save(it.exitState)!! },
                )
            },
            restore = {
                CollapsingTopBarScaffoldState(
                    topBarState = CollapsingTopBarState.Saver.restore(it[0])!!,
                    exitState = with(CollapsingTopBarExitState.Saver) { restore(it[1])!! },
                )
            },
        )
    }
}
