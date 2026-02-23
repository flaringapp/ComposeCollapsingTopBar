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

package com.flaringapp.compose.topbar.nestedscroll

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import com.flaringapp.compose.topbar.snap.CollapsingTopBarNoSnapBehavior
import com.flaringapp.compose.topbar.snap.CollapsingTopBarSnapBehavior

/**
 * Create and remember [NestedScrollConnection] that delegates nested scrolling callbacks to the
 * list of handlers created with receiver [CollapsingTopBarNestedScrollStrategy.createHandlers].
 *
 * **The order of handlers matters.** The first handler receives all available scroll, while each
 * subsequent one gets less by the amount consumed by all previous handlers.
 *
 * @param state the target top bar state receiver strategy is going to be used with.
 * @param flingBehavior the fling behavior to be used for animating [state] fling.
 * @param snapBehavior the snap behavior to be used for animating [state] snap after fling.
 *
 * @return the instance of [NestedScrollConnection] that aggregates all handlers created with
 * [CollapsingTopBarNestedScrollStrategy.createHandlers].
 *
 * @see CollapsingTopBarNestedScrollStrategy
 * @see CollapsingTopBarNestedScrollHandler
 * @see MultiNestedScrollConnection
 */
@Composable
fun <STATE> CollapsingTopBarNestedScrollStrategy<STATE>.rememberNestedScrollConnection(
    state: STATE,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    snapBehavior: CollapsingTopBarSnapBehavior = CollapsingTopBarNoSnapBehavior,
): NestedScrollConnection {
    return remember(this, state, flingBehavior, snapBehavior) {
        MultiNestedScrollConnection(
            createHandlers(
                state = state,
                flingBehavior = flingBehavior,
                snapBehavior = snapBehavior,
            ),
        )
    }
}

/**
 * A strategy/factory for handling nested scrolling in scope of top bar collapse.
 * Responsible for creating sequence of [CollapsingTopBarNestedScrollHandler] that handle specific
 * pieces of scrolling logic.
 *
 * @see CollapsingTopBarNestedScrollHandler
 * @see MultiNestedScrollConnection
 */
@Immutable
interface CollapsingTopBarNestedScrollStrategy<STATE> {

    /**
     * Creates a sequence of scroll handlers to process nested scroll.
     *
     * @param state the target top bar state receiver strategy is going to be used with.
     * @param flingBehavior the fling behavior to be used for animating [state] fling.
     * @param snapBehavior the snap behavior to be used for animating [state] snap after fling.
     *
     * @return list of nested scroll handlers.
     *
     * @see CollapsingTopBarNestedScrollHandler
     */
    fun createHandlers(
        state: STATE,
        flingBehavior: FlingBehavior,
        snapBehavior: CollapsingTopBarSnapBehavior,
    ): List<CollapsingTopBarNestedScrollHandler>
}
