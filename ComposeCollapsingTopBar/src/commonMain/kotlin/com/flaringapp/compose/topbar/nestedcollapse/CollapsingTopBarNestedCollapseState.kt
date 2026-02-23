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
import androidx.compose.runtime.annotation.RememberInComposition
import androidx.compose.runtime.remember
import com.flaringapp.compose.topbar.CollapsingTopBar

/**
 * Creates a [CollapsingTopBarNestedCollapseState] that is remembered across compositions.
 */
@Composable
fun rememberCollapsingTopBarNestedCollapseState(): CollapsingTopBarNestedCollapseState {
    return remember {
        CollapsingTopBarNestedCollapseState()
    }
}

/**
 * A contract for any top bar [CollapsingTopBar] nested collapse element to provide its own
 * minimum height. Used in [CollapsingTopBar] to determine ultimate minimum top bar height.
 *
 * @see CollapsingTopBar
 * @see CollapsingTopBarColumn
 */
@Stable
interface CollapsingTopBarNestedCollapseElement {

    /**
     * The minimum height of this nested collapse element.
     */
    val minHeight: Int
}

/**
 * A mutable implementation of [CollapsingTopBarNestedCollapseElement] for nested collapse element
 * to update. Must be updated in measurement phase.
 *
 * In most cases, this will be created via [rememberCollapsingTopBarNestedCollapseState].
 */
@Stable
class CollapsingTopBarNestedCollapseState @RememberInComposition constructor() :
    CollapsingTopBarNestedCollapseElement {

    /**
     * The minimum height of this nested collapse element. Must be updated in measurement phase.
     * Not a state because hosting [CollapsingTopBar] is remeasured every time after this element,
     * therefore reads up to date value anyways.
     */
    override var minHeight: Int = 0
}
