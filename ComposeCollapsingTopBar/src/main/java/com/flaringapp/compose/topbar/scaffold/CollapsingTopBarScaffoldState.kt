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
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import com.flaringapp.compose.topbar.CollapsingTopBarControls
import com.flaringapp.compose.topbar.CollapsingTopBarState
import com.flaringapp.compose.topbar.dependent.CollapsingTopBarExitState
import com.flaringapp.compose.topbar.snap.CollapsingTopBarSnapScope

@Composable
fun rememberCollapsingTopBarScaffoldState(
    isExpanded: Boolean = true,
): CollapsingTopBarScaffoldState {
    return rememberSaveable(isExpanded, saver = CollapsingTopBarScaffoldState.Saver) {
        CollapsingTopBarScaffoldState(
            topBarState = CollapsingTopBarState(isExpanded = isExpanded),
            exitState = CollapsingTopBarExitState(isExited = !isExpanded),
        )
    }
}

@Stable
class CollapsingTopBarScaffoldState internal constructor(
    val topBarState: CollapsingTopBarState,
    val exitState: CollapsingTopBarExitState,
) : CollapsingTopBarControls,
    CollapsingTopBarSnapScope {

    val totalHeight: Float
        get() = topBarState.layoutInfo.height - exitState.exitHeight

    override suspend fun expand(animationSpec: AnimationSpec<Float>) {
        animateScrollTo(animationSpec) {
            topBarState.layoutInfo.expandedHeight.toFloat()
        }
    }

    override suspend fun collapse(animationSpec: AnimationSpec<Float>) {
        animateScrollTo(animationSpec) { canExit ->
            if (canExit) {
                0f
            } else {
                topBarState.layoutInfo.collapsedHeight.toFloat()
            }
        }
    }

    private suspend inline fun animateScrollTo(
        animationSpec: AnimationSpec<Float>,
        targetValueProvider: (canExit: Boolean) -> Float,
    ) {
        val canExit = exitState.isEnabled
        val targetValue = targetValueProvider(canExit)

        if (!canExit) {
            topBarState.animateScrollBy(
                offset = targetValue - topBarState.layoutInfo.height,
                animationSpec = animationSpec,
            )
            return
        }

        val offset = targetValue - totalHeight

        topBarState.scroll topBarScrollScope@{
            exitState.scroll exitScrollScope@{
                val scopeOrder = listOf(
                    this@topBarScrollScope,
                    this@exitScrollScope,
                ).let {
                    if (offset < 0) it else it.asReversed()
                }

                val mergedScrollScope = object : ScrollScope {
                    override fun scrollBy(pixels: Float): Float {
                        val consumed = scopeOrder.fold(pixels) { left, scope ->
                            left - scope.scrollBy(left)
                        }
                        return pixels - consumed
                    }
                }

                mergedScrollScope.animateScrollBy(
                    offset = offset,
                    animationSpec = animationSpec,
                )
            }
        }
    }

    override suspend fun snapWithProgress(
        wasMovingUp: Boolean,
        action: suspend CollapsingTopBarControls.(progress: Float) -> Unit,
    ) {
        val progress = totalHeight / topBarState.layoutInfo.expandedHeight
        action.invoke(this, progress)
    }

    companion object {

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
