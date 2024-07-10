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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.tooling.preview.Preview
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffold
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffoldScrollMode
import com.flaringapp.compose.topbar.scaffold.rememberCollapsingTopBarScaffoldState
import com.flaringapp.compose.topbar.ui.samples.CollapsingTopBarSample
import com.flaringapp.compose.topbar.ui.samples.CollapsingTopBarSampleDogDefaults
import com.flaringapp.compose.topbar.ui.samples.common.SampleContent
import com.flaringapp.compose.topbar.ui.samples.common.SampleTopAppBar
import com.flaringapp.compose.topbar.ui.samples.common.SampleTopBarImage
import com.flaringapp.compose.topbar.ui.theme.ComposeCollapsingTopBarTheme
import androidx.compose.ui.util.lerp as primitiveLerp

private const val SCRIM_START_FRACTION = 0.5f

object AppBarScrimSample : CollapsingTopBarSample {

    override val name: String = "App Bar Scrim"

    @Composable
    override fun Content(onBack: () -> Unit) {
        AppBarScrimSampleContent(onBack = onBack)
    }
}

@Composable
fun AppBarScrimSampleContent(
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

    val topBarColorProgress by remember {
        derivedStateOf {
            val progress = state.topBarState.layoutInfo.collapseProgress
            // Treat 0 <-> >=SCRIM_START_FRACTION progress as 0 <-> 1
            progress.coerceAtMost(SCRIM_START_FRACTION) / SCRIM_START_FRACTION
        }
    }

    CollapsingTopBarScaffold(
        modifier = modifier,
        state = state,
        scrollMode = CollapsingTopBarScaffoldScrollMode.collapse(expandAlways = false),
        topBar = {
            SampleTopBarImage(
                dog = CollapsingTopBarSampleDogDefaults.Advanced,
            )

            SampleTopAppBar(
                title = "App Bar Scrim",
                onBack = onBack,
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                    alpha = primitiveLerp(1f, 0f, topBarColorProgress),
                ),
                contentColor = lerp(
                    start = MaterialTheme.colorScheme.onSurface,
                    stop = MaterialTheme.colorScheme.onPrimaryContainer,
                    fraction = topBarColorProgress,
                ),
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
        AppBarScrimSampleContent(
            onBack = {},
        )
    }
}
