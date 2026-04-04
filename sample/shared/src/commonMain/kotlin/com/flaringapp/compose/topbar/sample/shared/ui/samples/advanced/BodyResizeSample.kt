/*
 * Copyright 2026 Flaringapp
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

package com.flaringapp.compose.topbar.sample.shared.ui.samples.advanced

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.flaringapp.compose.topbar.sample.shared.ui.samples.CollapsingTopBarSample
import com.flaringapp.compose.topbar.sample.shared.ui.samples.CollapsingTopBarSampleDogDefaults
import com.flaringapp.compose.topbar.sample.shared.ui.samples.common.SampleTopAppBar
import com.flaringapp.compose.topbar.sample.shared.ui.samples.common.SampleTopBarImage
import com.flaringapp.compose.topbar.sample.shared.ui.theme.ComposeCollapsingTopBarTheme
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffold
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffoldScrollMode

object BodyResizeSample : CollapsingTopBarSample {

    override val name: String = "Body Resize"

    @Composable
    override fun Content(onBack: () -> Unit) {
        BodyResizeSampleContent(onBack = onBack)
    }
}

@Composable
fun BodyResizeSampleContent(
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
        scrollMode = CollapsingTopBarScaffoldScrollMode.collapse(expandAlways = false),
        topBar = {
            SampleTopBarImage(
                dog = CollapsingTopBarSampleDogDefaults.Advanced,
            )

            SampleTopAppBar(
                title = "Body Resize",
                onBack = onBack,
                containerColor = Color.Transparent,
            )
        },
        body = {
            GradientContent(
                modifier = Modifier.resizeWithCollapse(),
            )
        },
    )
}

@Composable
private fun GradientContent(
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
    val gradient = remember(colorScheme) {
        Brush.verticalGradient(
            colors = listOf(
                colorScheme.primary,
                colorScheme.surface,
                colorScheme.tertiaryContainer,
            ),
        )
    }

    Spacer(
        modifier = modifier
            .fillMaxSize()
            .background(gradient)
            .verticalScroll(rememberScrollState()),
    )
}

@Preview
@Composable
private fun Preview() {
    ComposeCollapsingTopBarTheme {
        BodyResizeSampleContent(
            onBack = {},
        )
    }
}
