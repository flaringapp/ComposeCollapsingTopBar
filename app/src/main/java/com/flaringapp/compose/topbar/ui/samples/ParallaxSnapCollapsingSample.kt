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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffold
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffoldScrollMode
import com.flaringapp.compose.topbar.scaffold.rememberCollapsingTopBarScaffoldState
import com.flaringapp.compose.topbar.snap.rememberCollapsingTopBarSnapBehavior
import com.flaringapp.compose.topbar.ui.samples.common.SampleContent
import com.flaringapp.compose.topbar.ui.samples.common.SampleTopAppBar
import com.flaringapp.compose.topbar.ui.samples.common.SampleTopBarImage
import com.flaringapp.compose.topbar.ui.theme.ComposeCollapsingTopBarTheme

@Composable
fun ParallaxSnapCollapsingSample(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
    ) {
        CollapsingContent(
            onBack = onBack,
        )
    }
}

@Composable
private fun CollapsingContent(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CollapsingTopBarScaffold(
        modifier = modifier,
        state = rememberCollapsingTopBarScaffoldState(),
        scrollMode = CollapsingTopBarScaffoldScrollMode.collapse(expandAlways = false),
        snapBehavior = rememberCollapsingTopBarSnapBehavior(threshold = 0.5f),
        topBar = {
            SampleTopBarImage(
                modifier = Modifier
                    .parallax(0.25f),
            )

            SampleTopAppBar(
                title = "Parallax Snap Collapsing Sample",
                onBack = onBack,
            )
        },
        body = {
            SampleContent()
        },
    )
}

@Preview
@Composable
private fun Preview() {
    ComposeCollapsingTopBarTheme {
        ParallaxSnapCollapsingSample(
            onBack = {},
        )
    }
}