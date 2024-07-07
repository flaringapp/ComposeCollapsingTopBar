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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import com.flaringapp.compose.topbar.CollapsingTopBar
import com.flaringapp.compose.topbar.CollapsingTopBarScope
import com.flaringapp.compose.topbar.dependent.collapsingTopBarExitStateConnection
import com.flaringapp.compose.topbar.nestedscroll.rememberNestedScrollConnection
import com.flaringapp.compose.topbar.snap.CollapsingTopBarNoSnapBehavior
import com.flaringapp.compose.topbar.snap.CollapsingTopBarSnapBehavior
import kotlin.math.max

@Composable
fun CollapsingTopBarScaffold(
    state: CollapsingTopBarScaffoldState,
    scrollMode: CollapsingTopBarScaffoldScrollMode,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    snapBehavior: CollapsingTopBarSnapBehavior = CollapsingTopBarNoSnapBehavior,
    topBarModifier: Modifier = Modifier,
    topBarClipToBounds: Boolean = true,
    topBar: @Composable CollapsingTopBarScope.() -> Unit,
    body: @Composable () -> Unit,
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
    val topBarHeightState by remember(state.topBarState) {
        derivedStateOf { state.topBarState.layoutInfo.height.toInt() }
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
                    .then(exitStateUpdateModifier),
                state = state.topBarState,
                clipToBounds = topBarClipToBounds,
            ) {
                topBar()
            }

            body()
        },
        modifier = modifier
            .then(
                if (enabled) {
                    Modifier.nestedScroll(nestedScrollConnection)
                } else {
                    Modifier
                }
            )
    ) { measurables, constraints ->
        val topBarConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val topBarPlaceable = measurables[0].measure(topBarConstraints)

        val bodyPlaceables = if (measurables.size > 1) {
            val bodyHeight = (constraints.maxHeight - topBarMinHeightState).coerceAtLeast(0)

            val bodyConstraints = constraints.copy(
                minWidth = 0,
                minHeight = 0,
                maxHeight = bodyHeight,
            )
            val bodyMeasurables = measurables.subList(1, measurables.size)
            bodyMeasurables.map { it.measure(bodyConstraints) }
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
            val topBarExitHeight = state.exitState.exitHeight.toInt()

            bodyPlaceables.forEach { placeable ->
                placeable.placeRelative(0, topBarHeightState - topBarExitHeight)
            }

            topBarPlaceable.placeRelative(0, -topBarExitHeight)
        }
    }
}
