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

package com.flaringapp.compose.topbar.scaffold

import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import com.flaringapp.compose.topbar.CollapsingTopBar
import com.flaringapp.compose.topbar.CollapsingTopBarScope
import com.flaringapp.compose.topbar.CollapsingTopBarState
import com.flaringapp.compose.topbar.dependent.collapsingTopBarExitStateConnection
import com.flaringapp.compose.topbar.nestedscroll.rememberNestedScrollConnection
import com.flaringapp.compose.topbar.snap.CollapsingTopBarNoSnapBehavior
import com.flaringapp.compose.topbar.snap.CollapsingTopBarSnapBehavior
import com.flaringapp.compose.topbar.snap.rememberCollapsingTopBarSnapBehavior
import kotlin.math.max

/**
 * Collapsing top bar scaffold implements a common collapsing layout structure with
 * [CollapsingTopBar] at top and scrollable content below. This is a crucial piece in collapsing
 * mechanism as it links top bar and content together, and performs elements offsetting based
 * on collapse progress.
 *
 * It's inspired by CoordinatorLayout approach of measuring content under top bar to its max height
 * while sliding up and down as top bar collapses. In addition, [CollapsingTopBar] also measures
 * itself to max height not to remeasure itself as its *collapsing height* changes. Thus we minimize
 * measurement phase rescheduling, and all the heavy (actually, light :D) work is done in placement
 * phase.
 *
 * @param scrollMode the strategy of handling nested scroll to collapse and expand top bar, and
 * optionally exit and enter.
 * @param modifier the [Modifier] to be applied to this scaffold.
 * @param state the state than manages this scaffold.
 * @param enabled the flag whether or not to enable nested scrolling. Top bar won't react to
 * content scrolling if disabled.
 * @param snapBehavior the behavior of top bar snapping after fling. Disabled by default. Can be
 * enabled with [rememberCollapsingTopBarSnapBehavior].
 * @param topBarModifier the [Modifier] to be applied to [CollapsingTopBar] - container of [topBar].
 * @param topBarClipToBounds the flag whether or not to automatically clip top bar content [topBar]
 * to the actual collapse height.
 * @param topBar the content of [CollapsingTopBar].
 * @param body the content under collapsing top bar. By default, direct body children are measured
 * against collapsed top bar height to minimize remeasurement during collapse. Use
 * [CollapsingTopBarScaffoldBodyScope.resizeWithCollapse] only for direct body children that must
 * resize with the current top bar height instead, such as lightweight overlays. It may increase
 * remeasurement cost and is generally not recommended for scrollable content. If you don't use any
 * by default scrollable container (e.g. [androidx.compose.foundation.lazy.LazyList]), then make
 * sure to apply custom [androidx.compose.foundation.verticalScroll] modifier.
 *
 * @see CollapsingTopBar
 */
@Suppress("ComposeParameterOrder")
@Composable
public fun CollapsingTopBarScaffold(
    scrollMode: CollapsingTopBarScaffoldScrollMode,
    modifier: Modifier = Modifier,
    state: CollapsingTopBarScaffoldState = rememberCollapsingTopBarScaffoldState(),
    enabled: Boolean = true,
    snapBehavior: CollapsingTopBarSnapBehavior = CollapsingTopBarNoSnapBehavior,
    topBarModifier: Modifier = Modifier,
    topBarClipToBounds: Boolean = true,
    topBar: @Composable CollapsingTopBarScope.(topBarState: CollapsingTopBarState) -> Unit,
    body: @Composable CollapsingTopBarScaffoldBodyScope.() -> Unit,
) {
    LaunchedEffect(scrollMode.canExit) {
        if (!scrollMode.canExit) {
            state.exitState.reset()
        }
    }

    val nestedScrollConnection = scrollMode.rememberNestedScrollConnection(
        state = state,
        snapBehavior = snapBehavior,
    )

    val topBarMinHeightState by remember(state.topBarState, scrollMode.canExit) {
        if (scrollMode.canExit) {
            return@remember mutableIntStateOf(0)
        }
        derivedStateOf { state.topBarState.layoutInfo.collapsedHeight }
    }

    val exitStateUpdateModifier = if (scrollMode.canExit) {
        Modifier.collapsingTopBarExitStateConnection(
            topBarState = state.topBarState,
            exitState = state.exitState,
        )
    } else {
        Modifier
    }

    Layout(
        content = {
            CollapsingTopBar(
                modifier = topBarModifier
                    .then(exitStateUpdateModifier)
                    // Contrary motion to scaffold placement
                    .offset {
                        val collapseHeight = state.topBarState.layoutInfo.collapseHeightDelta
                        IntOffset(x = 0, y = collapseHeight.toInt())
                    },
                state = state.topBarState,
                clipToBounds = topBarClipToBounds,
            ) {
                topBar(state.topBarState)
            }

            CollapsingTopBarScaffoldBodyScopeInstance.body()
        },
        modifier = modifier
            .then(
                if (enabled) {
                    Modifier.nestedScroll(nestedScrollConnection)
                } else {
                    Modifier
                },
            ),
    ) { measurables, constraints ->
        val topBarConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val topBarPlaceable = measurables[0].measure(topBarConstraints)

        val bodyPlaceables = if (measurables.size > 1) {
            val collapsedBodyConstrains = constraints.copy(
                minWidth = 0,
                minHeight = 0,
                maxHeight = (constraints.maxHeight - topBarMinHeightState).coerceAtLeast(0),
            )
            val resizedBodyConstrains = collapsedBodyConstrains.copy(
                maxHeight = (constraints.maxHeight - state.totalTopBarHeight.toInt())
                    .coerceAtLeast(0),
            )

            val bodyMeasurables = measurables.subList(1, measurables.size)
            bodyMeasurables.map { measurable ->
                val nodeConstraints = if (measurable.bodyParentData?.resizeWithCollapse == true) {
                    resizedBodyConstrains
                } else {
                    collapsedBodyConstrains
                }

                measurable.measure(nodeConstraints)
            }
        } else {
            emptyList()
        }

        val width = max(
            topBarPlaceable.width,
            bodyPlaceables.maxOfOrNull { it.width } ?: 0,
        ).let { constraints.constrainWidth(it) }

        val bodyHeight = bodyPlaceables.maxOfOrNull { it.height } ?: 0
        val height = (topBarMinHeightState + bodyHeight)
            .let { constraints.constrainHeight(it) }

        layout(width, height) {
            val topBarHeight = state.topBarState.layoutInfo.height.toInt()
            val topBarCollapseOffset = -state.topBarState.layoutInfo.collapseHeightDelta.toInt()
            val topBarExitHeight = state.exitState.exitHeight.toInt()

            bodyPlaceables.forEach { placeable ->
                placeable.placeRelative(0, topBarHeight - topBarExitHeight)
            }

            topBarPlaceable.placeRelative(0, topBarCollapseOffset - topBarExitHeight)
        }
    }
}

/**
 * Scope for direct body children of [CollapsingTopBarScaffold].
 */
@LayoutScopeMarker
@Immutable
public interface CollapsingTopBarScaffoldBodyScope {

    /**
     * Opt this direct scaffold body child into measurement against current visible top bar height
     * instead of collapsed top bar height.
     *
     * This may increase remeasurement cost during collapse. Prefer the default scaffold body
     * behavior unless this child must respond to the current visible top bar height, e.g. for a
     * lightweight overlay.
     */
    public fun Modifier.resizeWithCollapse(): Modifier
}

private object CollapsingTopBarScaffoldBodyScopeInstance : CollapsingTopBarScaffoldBodyScope {

    override fun Modifier.resizeWithCollapse(): Modifier {
        return then(ResizeWithCollapseModifier())
    }
}

private class ResizeWithCollapseModifier : CollapsingTopBarScaffoldBodyParentDataModifier() {
    override fun modifyParentData(parentData: CollapsingTopBarScaffoldBodyParentData) {
        parentData.resizeWithCollapse = true
    }
}

private abstract class CollapsingTopBarScaffoldBodyParentDataModifier : ParentDataModifier {

    override fun Density.modifyParentData(parentData: Any?): Any {
        val data = parentData as? CollapsingTopBarScaffoldBodyParentData
            ?: CollapsingTopBarScaffoldBodyParentData()
        this@CollapsingTopBarScaffoldBodyParentDataModifier.modifyParentData(data)
        return data
    }

    protected abstract fun modifyParentData(parentData: CollapsingTopBarScaffoldBodyParentData)
}

private data class CollapsingTopBarScaffoldBodyParentData(
    var resizeWithCollapse: Boolean = false,
)

private val Measurable.bodyParentData: CollapsingTopBarScaffoldBodyParentData?
    get() = parentData as? CollapsingTopBarScaffoldBodyParentData
