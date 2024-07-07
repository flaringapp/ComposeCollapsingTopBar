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

package com.flaringapp.compose.topbar.nestedcollapse

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember

@Composable
fun rememberCollapsingTopBarNestedCollapseState(): CollapsingTopBarNestedCollapseState {
    return remember {
        CollapsingTopBarNestedCollapseState()
    }
}

/**
 * A contract for parent (collapsing top bar) to access child's measured min height
 */
@Stable
interface CollapsingTopBarNestedCollapseElement {

    val minHeight: Int
}

/**
 * Implementation of [CollapsingTopBarNestedCollapseElement] for child to update measured
 * min height
 */
@Stable
class CollapsingTopBarNestedCollapseState : CollapsingTopBarNestedCollapseElement {

    override var minHeight: Int = 0
}
