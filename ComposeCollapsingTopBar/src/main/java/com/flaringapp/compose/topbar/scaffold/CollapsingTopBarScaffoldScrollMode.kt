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

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.flaringapp.compose.topbar.nestedscroll.CollapsingTopBarNestedScrollCollapse
import com.flaringapp.compose.topbar.nestedscroll.CollapsingTopBarNestedScrollExpand
import com.flaringapp.compose.topbar.nestedscroll.CollapsingTopBarNestedScrollHandler
import com.flaringapp.compose.topbar.nestedscroll.CollapsingTopBarNestedScrollSnap
import com.flaringapp.compose.topbar.nestedscroll.CollapsingTopBarNestedScrollStrategy
import com.flaringapp.compose.topbar.snap.CollapsingTopBarSnapBehavior
import com.flaringapp.compose.topbar.snap.CollapsingTopBarSnapScope

data class CollapsingTopBarScaffoldScrollMode internal constructor(
    val expandAlways: Boolean,
    val exit: Exit? = null,
) : CollapsingTopBarNestedScrollStrategy<CollapsingTopBarScaffoldState> {

    data class Exit internal constructor(
        val enterAlways: Boolean,
    )

    val canExit: Boolean
        get() = exit != null

    override fun createHandlers(
        state: CollapsingTopBarScaffoldState,
        flingBehavior: FlingBehavior,
        snapBehavior: CollapsingTopBarSnapBehavior,
    ): List<CollapsingTopBarNestedScrollHandler> = buildList {
        this += CollapsingTopBarNestedScrollCollapse(
            state = state.topBarState,
            flingBehavior = flingBehavior,
        )
        if (exit != null) {
            this += CollapsingTopBarNestedScrollCollapse(
                state = state.exitState,
                flingBehavior = flingBehavior,
            )
            this += CollapsingTopBarNestedScrollExpand.of(
                state = state.exitState,
                flingBehavior = flingBehavior,
                enterAlways = exit.enterAlways,
            )
        }
        this += CollapsingTopBarNestedScrollExpand.of(
            state = state.topBarState,
            flingBehavior = flingBehavior,
            enterAlways = expandAlways,
        )
        this += CollapsingTopBarNestedScrollSnap(
            snapBehavior = snapBehavior,
            snapScope = createSnapScope(state),
        )
    }

    private fun createSnapScope(
        scaffoldState: CollapsingTopBarScaffoldState,
    ) = CollapsingTopBarSnapScope { wasMovingUp, action ->
        val delegateScope = when {
            exit == null -> scaffoldState.topBarState
            exit.enterAlways == expandAlways -> scaffoldState
            else -> resolveEnterAlwaysCollapsedSnapScope(scaffoldState, wasMovingUp)
        }

        delegateScope.snapWithProgress(wasMovingUp, action)
    }

    private fun resolveEnterAlwaysCollapsedSnapScope(
        scaffoldState: CollapsingTopBarScaffoldState,
        wasMovingUp: Boolean,
    ): CollapsingTopBarSnapScope {
        return when {
            wasMovingUp -> scaffoldState
            scaffoldState.topBarState.layoutInfo.isCollapsed -> scaffoldState.exitState
            else -> scaffoldState.topBarState
        }
    }

    companion object {

        @Composable
        fun collapse(
            expandAlways: Boolean,
        ): CollapsingTopBarScaffoldScrollMode = remember {
            CollapsingTopBarScaffoldScrollMode(
                expandAlways = expandAlways,
            )
        }

        @Composable
        fun collapseAndExit(
            expandAlways: Boolean,
        ): CollapsingTopBarScaffoldScrollMode = remember {
            CollapsingTopBarScaffoldScrollMode(
                expandAlways = expandAlways,
                exit = Exit(
                    enterAlways = expandAlways,
                ),
            )
        }

        @Composable
        fun enterAlwaysCollapsed(): CollapsingTopBarScaffoldScrollMode = remember {
            CollapsingTopBarScaffoldScrollMode(
                expandAlways = false,
                exit = Exit(
                    enterAlways = true,
                ),
            )
        }
    }
}