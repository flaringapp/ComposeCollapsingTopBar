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

package com.flaringapp.compose.topbar.ui.samples

import com.flaringapp.compose.topbar.ui.samples.advanced.AppBarScrimSample
import com.flaringapp.compose.topbar.ui.samples.advanced.AppBarShadowSample
import com.flaringapp.compose.topbar.ui.samples.advanced.FloatingElementSample
import com.flaringapp.compose.topbar.ui.samples.advanced.ManualCollapsingControlsSample
import com.flaringapp.compose.topbar.ui.samples.advanced.ParallaxCollapsingSample
import com.flaringapp.compose.topbar.ui.samples.advanced.SnapCollapsingSample
import com.flaringapp.compose.topbar.ui.samples.basic.CollapsingExitExpandAlwaysSample
import com.flaringapp.compose.topbar.ui.samples.basic.CollapsingExitExpandAtTopSample
import com.flaringapp.compose.topbar.ui.samples.basic.CollapsingExpandAlwaysSample
import com.flaringapp.compose.topbar.ui.samples.basic.CollapsingExpandAtTopSample
import com.flaringapp.compose.topbar.ui.samples.basic.EnterAlwaysCollapsedSample
import com.flaringapp.compose.topbar.ui.samples.column.AlternatelyCollapsibleColumnSample
import com.flaringapp.compose.topbar.ui.samples.column.ColumnInStackSample
import com.flaringapp.compose.topbar.ui.samples.column.ColumnMovingElementSample
import com.flaringapp.compose.topbar.ui.samples.column.FullyCollapsibleColumnSample
import com.flaringapp.compose.topbar.ui.samples.column.PartiallyCollapsibleColumnSample
import com.flaringapp.compose.topbar.ui.samples.scaffold.ScaffoldPlaygroundSample

object CollapsingTopBarSampleGroups {

    val Basic: List<CollapsingTopBarSample>
        get() = listOf(
            CollapsingExpandAtTopSample,
            CollapsingExpandAlwaysSample,
            CollapsingExitExpandAtTopSample,
            CollapsingExitExpandAlwaysSample,
            EnterAlwaysCollapsedSample,
        )

    val Column: List<CollapsingTopBarSample>
        get() = listOf(
            FullyCollapsibleColumnSample,
            PartiallyCollapsibleColumnSample,
            AlternatelyCollapsibleColumnSample,
            ColumnInStackSample,
            ColumnMovingElementSample,
        )

    val Advanced: List<CollapsingTopBarSample>
        get() = listOf(
            ParallaxCollapsingSample,
            SnapCollapsingSample,
            AppBarShadowSample,
            AppBarScrimSample,
            ManualCollapsingControlsSample,
            FloatingElementSample,
        )

    val Playground: List<CollapsingTopBarSample>
        get() = listOf(
            ScaffoldPlaygroundSample,
        )
}
