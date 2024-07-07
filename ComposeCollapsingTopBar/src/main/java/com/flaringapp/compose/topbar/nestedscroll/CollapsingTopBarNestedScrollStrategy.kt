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

@Composable
fun <STATE> CollapsingTopBarNestedScrollStrategy<STATE>.rememberNestedScrollConnection(
    state: STATE,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    snapBehavior: CollapsingTopBarSnapBehavior = CollapsingTopBarNoSnapBehavior,
): NestedScrollConnection {
    return remember(this, state) {
        MultiNestedScrollConnection(
            createHandlers(
                state = state,
                flingBehavior = flingBehavior,
                snapBehavior = snapBehavior,
            )
        )
    }
}

@Immutable
interface CollapsingTopBarNestedScrollStrategy<STATE> {

    fun createHandlers(
        state: STATE,
        flingBehavior: FlingBehavior,
        snapBehavior: CollapsingTopBarSnapBehavior,
    ): List<CollapsingTopBarNestedScrollHandler>
}
