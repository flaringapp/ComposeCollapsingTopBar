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

class CollapsingTopBarNestedScrollCollapse(
    private val state: ScrollableState,
    private val flingBehavior: FlingBehavior,
) : CollapsingTopBarNestedScrollHandler {

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val dy = available.y
        if (dy >= 0) {
            return Offset.Zero
        }

        val consume = state.dispatchRawDelta(dy)
        return Offset(x = 0f, y = consume)
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        val dy = available.y
        if (dy >= 0) {
            return Velocity.Zero
        }

        val consume = dy - state.fling(flingBehavior, dy)
        return Velocity(x = 0f, y = consume)
    }
}