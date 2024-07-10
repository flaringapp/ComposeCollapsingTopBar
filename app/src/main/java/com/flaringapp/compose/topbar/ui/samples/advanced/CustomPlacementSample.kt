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

package com.flaringapp.compose.topbar.ui.samples.advanced

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffold
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffoldScrollMode
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffoldState
import com.flaringapp.compose.topbar.scaffold.rememberCollapsingTopBarScaffoldState
import com.flaringapp.compose.topbar.ui.samples.CollapsingTopBarSample
import com.flaringapp.compose.topbar.ui.samples.common.SampleContent
import com.flaringapp.compose.topbar.ui.samples.common.SampleTopAppBar
import com.flaringapp.compose.topbar.ui.samples.common.SampleTopBarImage
import com.flaringapp.compose.topbar.ui.theme.ComposeCollapsingTopBarTheme

object CustomPlacementSample : CollapsingTopBarSample {

    override val name: String = "Custom Placement"

    @Composable
    override fun Content(onBack: () -> Unit) {
        CustomPlacementSampleContent(onBack = onBack)
    }
}

@Composable
fun CustomPlacementSampleContent(
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
    val state = rememberCollapsingTopBarScaffoldState()

    CollapsingTopBarScaffold(
        modifier = modifier,
        state = state,
        scrollMode = CollapsingTopBarScaffoldScrollMode.collapseAndExit(expandAlways = false),
        topBar = {
            SampleTopBarImage()

            SampleTopAppBar(
                title = "Custom Placement Sample",
                onBack = onBack,
            )

            SlidingDot(
                modifier = Modifier.floating(),
                state = state,
            )
        },
        body = {
            SampleContent()
        },
    )
}

@Composable
private fun SlidingDot(
    state: CollapsingTopBarScaffoldState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(placeable.width, placeable.height) {
                    val layoutInfo = state.topBarState.layoutInfo
                    val x = lerp(
                        start = constraints.maxWidth - placeable.width - 16.dp.roundToPx(),
                        stop = 16.dp.roundToPx(),
                        fraction = layoutInfo.collapseProgress,
                    )
                    val y = layoutInfo.height.toInt() - placeable.height - 16.dp.roundToPx()
                    placeable.place(x, y)
                }
            }
            .size(40.dp)
            .background(color = Color.Red, shape = CircleShape),
    )
}

@Preview
@Composable
private fun Preview() {
    ComposeCollapsingTopBarTheme {
        CustomPlacementSampleContent(
            onBack = {},
        )
    }
}
