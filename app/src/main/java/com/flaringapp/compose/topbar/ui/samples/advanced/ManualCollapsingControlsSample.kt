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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffold
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffoldScrollMode
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffoldState
import com.flaringapp.compose.topbar.scaffold.rememberCollapsingTopBarScaffoldState
import com.flaringapp.compose.topbar.ui.samples.CollapsingTopBarSample
import com.flaringapp.compose.topbar.ui.samples.CollapsingTopBarSampleDogDefaults
import com.flaringapp.compose.topbar.ui.samples.common.SampleContent
import com.flaringapp.compose.topbar.ui.samples.common.SampleTopAppBar
import com.flaringapp.compose.topbar.ui.samples.common.SampleTopBarImage
import com.flaringapp.compose.topbar.ui.samples.common.rememberSampleExpandRequestHandler
import com.flaringapp.compose.topbar.ui.theme.ComposeCollapsingTopBarTheme

object ManualCollapsingControlsSample : CollapsingTopBarSample {

    override val name: String = "Manual Collapsing"

    @Composable
    override fun Content(onBack: () -> Unit) {
        ManualCollapsingControlsSampleContent(onBack = onBack)
    }
}

@Composable
fun ManualCollapsingControlsSampleContent(
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
        scrollMode = CollapsingTopBarScaffoldScrollMode.collapse(expandAlways = false),
        topBar = {
            SampleTopBarImage(
                dog = CollapsingTopBarSampleDogDefaults.Advanced,
            )

            SampleTopAppBar(
                title = "Manual Collapsing",
                onBack = onBack,
            )
        },
        body = {
            SampleContent {
                TopBarControls(
                    state = state,
                )
            }
        },
    )
}

@Composable
private fun TopBarControls(
    state: CollapsingTopBarScaffoldState,
    modifier: Modifier = Modifier,
) {
    var expandRequest: Boolean? by rememberSampleExpandRequestHandler(state)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.LightGray),
        horizontalArrangement = Arrangement.Absolute.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Button(
            onClick = { expandRequest = true },
        ) {
            Text("Expand")
        }

        Button(
            onClick = { expandRequest = false },
        ) {
            Text("Collapse")
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ComposeCollapsingTopBarTheme {
        ManualCollapsingControlsSampleContent(
            onBack = {},
        )
    }
}
