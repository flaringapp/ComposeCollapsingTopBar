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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flaringapp.compose.topbar.sample.shared.ui.samples.CollapsingTopBarSample
import com.flaringapp.compose.topbar.sample.shared.ui.samples.CollapsingTopBarSampleDogDefaults
import com.flaringapp.compose.topbar.sample.shared.ui.samples.common.SampleContent
import com.flaringapp.compose.topbar.sample.shared.ui.samples.common.SampleTopAppBar
import com.flaringapp.compose.topbar.sample.shared.ui.samples.common.SampleTopBarImage
import com.flaringapp.compose.topbar.sample.shared.ui.theme.ComposeCollapsingTopBarTheme
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffold
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffoldScrollMode

object PinSample : CollapsingTopBarSample {

    override val name: String = "Pin"

    @Composable
    override fun Content(onBack: () -> Unit) {
        PinSampleContent(onBack = onBack)
    }
}

@Composable
fun PinSampleContent(
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

            TallElement(
                modifier = Modifier
                    .align(Alignment.Center)
                    .pin(stopAtTop = true),
            )

            SmallElement(
                modifier = Modifier
                    .floating()
                    .align(Alignment.BottomEnd)
                    .pin(),
            )

            SampleTopAppBar(
                title = "Pin",
                onBack = onBack,
                containerColor = Color.Transparent,
            )
        },
        body = {
            SampleContent()
        },
    )
}

@Composable
private fun TallElement(
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
    val gradient = remember(colorScheme) {
        Brush.verticalGradient(
            colors = listOf(
                colorScheme.primaryContainer,
                colorScheme.primary,
                colorScheme.tertiaryContainer,
            ),
        )
    }

    Box(
        modifier = modifier
            .size(width = 40.dp, height = 200.dp)
            .background(brush = gradient, shape = MaterialTheme.shapes.medium),
    )
}

@Composable
private fun SmallElement(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.medium,
            ),
    )
}

@Preview
@Composable
private fun Preview() {
    ComposeCollapsingTopBarTheme {
        PinSampleContent(
            onBack = {},
        )
    }
}
