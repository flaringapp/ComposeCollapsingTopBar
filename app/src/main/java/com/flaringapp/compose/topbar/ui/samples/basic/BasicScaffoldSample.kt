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

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.util.lerp
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffold
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffoldScrollMode
import com.flaringapp.compose.topbar.ui.samples.CollapsingTopBarSampleDogDefaults
import com.flaringapp.compose.topbar.ui.samples.common.SampleContent
import com.flaringapp.compose.topbar.ui.samples.common.SampleTopAppBar
import com.flaringapp.compose.topbar.ui.samples.common.SampleTopBarImage
import com.flaringapp.compose.topbar.ui.theme.ComposeCollapsingTopBarTheme

private const val SCRIM_START_FRACTION = 0.25f

@Composable
fun BasicScaffoldSampleContent(
    title: String,
    onBack: () -> Unit,
    scrollMode: CollapsingTopBarScaffoldScrollMode,
    modifier: Modifier = Modifier,
) {
    CollapsingTopBarScaffold(
        modifier = modifier,
        scrollMode = scrollMode,
        topBar = {
            var topBarColorProgress by remember { mutableFloatStateOf(1f) }

            SampleTopBarImage(
                modifier = Modifier
                    .progress { _, itemProgress ->
                        topBarColorProgress =
                            itemProgress.coerceAtMost(SCRIM_START_FRACTION) / SCRIM_START_FRACTION
                    },
                dog = CollapsingTopBarSampleDogDefaults.Basic,
            )

            SampleTopAppBar(
                title = title,
                onBack = onBack,
                containerColor = MaterialTheme.colorScheme.surface.copy(
                    alpha = lerp(1f, 0f, topBarColorProgress),
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
        BasicScaffoldSampleContent(
            title = "Basic Scaffold",
            onBack = {},
            scrollMode = CollapsingTopBarScaffoldScrollMode.collapse(expandAlways = false),
        )
    }
}
