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
import com.flaringapp.compose.topbar.ui.samples.CollapsingTopBarSample
import com.flaringapp.compose.topbar.ui.theme.ComposeCollapsingTopBarTheme

object CollapsingExitExpandAtTopSample : CollapsingTopBarSample {

    override val name: String = "Collapsing with exit, expand at top"

    @Composable
    override fun Content(onBack: () -> Unit) {
        CollapsingExitExpandAtTopSample(onBack = onBack)
    }
}

object CollapsingExitExpandAlwaysSample : CollapsingTopBarSample {

    override val name: String = "Collapsing with exit, expand always"

    @Composable
    override fun Content(onBack: () -> Unit) {
        CollapsingExitExpandAlwaysSample(onBack = onBack)
    }
}

@Composable
fun CollapsingExitExpandAtTopSample(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CollapsingExitSample(
        modifier = modifier,
        title = "Collapse Exit / Expand At Top",
        onBack = onBack,
        expandAlways = false,
    )
}

@Composable
fun CollapsingExitExpandAlwaysSample(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CollapsingExitSample(
        modifier = modifier,
        title = "Collapse Exit / Expand Always",
        onBack = onBack,
        expandAlways = true,
    )
}

@Composable
fun CollapsingExitSample(
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
            scrollMode = CollapsingTopBarScaffoldScrollMode.collapseAndExit(
                expandAlways = expandAlways,
            ),
        )
    }
}

@Preview
@Composable
private fun PreviewExpandAtTop() {
    ComposeCollapsingTopBarTheme {
        CollapsingExitExpandAtTopSample(
            onBack = {},
        )
    }
}

@Preview
@Composable
private fun PreviewExpandAlways() {
    ComposeCollapsingTopBarTheme {
        CollapsingExitExpandAlwaysSample(
            onBack = {},
        )
    }
}
