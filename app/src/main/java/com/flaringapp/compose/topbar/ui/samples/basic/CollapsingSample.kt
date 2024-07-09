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

package com.flaringapp.compose.topbar.ui.samples.basic

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffoldScrollMode
import com.flaringapp.compose.topbar.ui.theme.ComposeCollapsingTopBarTheme

@Composable
fun CollapsingExpandAtTopSample(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CollapsingSample(
        title = "Expand At Top",
        modifier = modifier,
        onBack = onBack,
        expandAlways = false,
    )
}

@Composable
fun CollapsingExpandAlwaysSample(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CollapsingSample(
        modifier = modifier,
        title = "Expand Always",
        onBack = onBack,
        expandAlways = true,
    )
}

@Composable
fun CollapsingSample(
    title: String,
    onBack: () -> Unit,
    expandAlways: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
    ) {
        BasicScaffoldSample(
            title = title,
            onBack = onBack,
            scrollMode = CollapsingTopBarScaffoldScrollMode.collapse(expandAlways = expandAlways),
        )
    }
}

@Preview
@Composable
private fun PreviewExpandAtTop() {
    ComposeCollapsingTopBarTheme {
        CollapsingExpandAtTopSample(
            onBack = {},
        )
    }
}

@Preview
@Composable
private fun PreviewExpandAlways() {
    ComposeCollapsingTopBarTheme {
        CollapsingExpandAlwaysSample(
            onBack = {},
        )
    }
}
