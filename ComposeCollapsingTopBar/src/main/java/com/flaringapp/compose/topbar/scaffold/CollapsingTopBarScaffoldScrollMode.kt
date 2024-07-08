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
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffoldScrollMode.Companion.collapse
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffoldScrollMode.Companion.collapseAndExit
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffoldScrollMode.Companion.enterAlwaysCollapsed
import com.flaringapp.compose.topbar.snap.CollapsingTopBarSnapBehavior
import com.flaringapp.compose.topbar.snap.CollapsingTopBarSnapScope

/**
 * A set of common collapsing modes that define how top bar behaves on content scroll.
 *
 * @see collapse
 * @see collapseAndExit
 * @see enterAlwaysCollapsed
 */
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

        /**
         * Create and remember scroll mode in which top bar just collapses, and never exits.
         *
         * @param expandAlways the flag to determine whether top bar can expand as soon as content
         * scrolls upwards, or only when scrollable content underneath is fully scrolled to the top.
         */
        @Composable
        fun collapse(
            expandAlways: Boolean,
        ): CollapsingTopBarScaffoldScrollMode = remember {
            CollapsingTopBarScaffoldScrollMode(
                expandAlways = expandAlways,
            )
        }

        /**
         * Create and remember scroll mode in which top bar sequentially collapses and exits.
         *
         * While scrolling down, the motion is: `expanded -> collapsed -> exited`. While scrolling
         * up, the motion is reversed: `exited -> collapsed -> expanded`.
         *
         * @param expandAlways the flag to determine whether top bar can expand as soon as content
         * scrolls upwards, or only when scrollable content underneath is fully scrolled to the top.
         */
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

        /**
         * Create and remember scroll mode in which top bar sequentially collapses and exits,
         * but may enter collapsed while content underneath is scrolling up, and expand at top.
         *
         * While scrolling down, the motion is: `expanded -> collapsed -> exited`. While scrolling
         * up, the motion is reversed: `exited -> collapsed -> (content scrolls to the top) ->
         * expanded`.
         */
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