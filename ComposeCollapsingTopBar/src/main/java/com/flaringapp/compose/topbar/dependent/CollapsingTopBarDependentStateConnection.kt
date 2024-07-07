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

package com.flaringapp.compose.topbar.dependent

import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.IntOffset
import com.flaringapp.compose.topbar.CollapsingTopBarLayoutInfo
import com.flaringapp.compose.topbar.CollapsingTopBarState

fun Modifier.collapsingTopBarDependentStateConnection(
    state: CollapsingTopBarState,
    update: (layoutInfo: CollapsingTopBarLayoutInfo) -> Unit,
): Modifier = this.layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)

    val lastLayoutInfo = Snapshot.withoutReadObservation { state.layoutInfo }
    update(lastLayoutInfo)

    layout(placeable.width, placeable.height) {
        placeable.place(IntOffset.Zero)
    }
}
