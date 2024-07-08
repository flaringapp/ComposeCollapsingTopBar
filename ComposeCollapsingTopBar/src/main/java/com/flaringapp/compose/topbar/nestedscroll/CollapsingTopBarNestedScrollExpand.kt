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
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import com.flaringapp.compose.topbar.nestedscroll.CollapsingTopBarNestedScrollExpand.Always
import com.flaringapp.compose.topbar.nestedscroll.CollapsingTopBarNestedScrollExpand.AtTop

/**
 * An origin class that encapsulates expand scroll handling logic.
 *
 * Expanding in terms of this handler means dispatching scroll to [state], which is in its turn
 * responsible for further processing.
 *
 * @see Always
 * @see AtTop
 */
abstract class CollapsingTopBarNestedScrollExpand : CollapsingTopBarNestedScrollHandler {

    companion object {

        /**
         * Creates one of [Always], [AtTop] scroll handlers based on [enterAlways] flag.
         *
         * @param state the scrollable top bar state that expands.
         * @param flingBehavior the fling behavior to be used for animating [state] fling.
         * @param enterAlways the flag to create [Always] handler if true, or [AtTop] if false.
         */
        fun of(
            state: ScrollableState,
            flingBehavior: FlingBehavior,
            enterAlways: Boolean,
        ): CollapsingTopBarNestedScrollHandler {
            return if (enterAlways) {
                Always(
                    state = state,
                    flingBehavior = flingBehavior,
                )
            } else {
                AtTop(
                    state = state,
                    flingBehavior = flingBehavior,
                )
            }
        }
    }

    abstract val state: ScrollableState
    abstract val flingBehavior: FlingBehavior

    /**
     * Dispatch [available] offset to [state] if it's downward.
     *
     * @param available the offset to be dispatched.
     *
     * @return the amount of offset consumed.
     */
    protected fun expand(available: Offset): Offset {
        val dy = available.y
        if (dy <= 0) {
            return Offset.Zero
        }

        val consume = state.dispatchRawDelta(dy)
        return Offset(0f, consume)
    }

    /**
     * Dispatch [available] velocity to [state] with [flingBehavior] if it's downward.
     *
     * @param available the velocity to be dispatched.
     *
     * @return the amount of velocity consumed.
     */
    protected suspend fun expand(available: Velocity): Velocity {
        val dy = available.y
        if (dy <= 0) {
            return Velocity.Zero
        }

        val consume = dy - state.fling(flingBehavior, dy)
        return Velocity(x = 0f, y = consume)
    }

    /**
     * A top bar nested scroll handler that expands [state] while receiving pre- scroll and fling.
     * Consumes available scroll before children, so that top bar expands anywhere.
     *
     * @param state the scrollable top bar state that expands.
     * @param flingBehavior the fling behavior to be used for animating [state] fling.
     */
    class Always(
        override val state: ScrollableState,
        override val flingBehavior: FlingBehavior,
    ) : CollapsingTopBarNestedScrollExpand() {

        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            return expand(available)
        }

        override suspend fun onPreFling(available: Velocity): Velocity {
            return expand(available)
        }
    }

    /**
     * A top bar nested scroll handler that expands [state] while receiving post- scroll and fling.
     * Consumes available scroll after children, so that top bar expands only when scrollable
     * children no longer consume downward scroll (are at top).
     *
     * @param state the scrollable top bar state that expands.
     * @param flingBehavior the fling behavior to be used for animating [state] fling.
     */
    class AtTop(
        override val state: ScrollableState,
        override val flingBehavior: FlingBehavior,
    ) : CollapsingTopBarNestedScrollExpand() {

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource,
        ): Offset {
            return expand(available)
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            return expand(available)
        }
    }
}
