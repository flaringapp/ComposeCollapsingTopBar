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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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

object AlignmentSample : CollapsingTopBarSample {

    override val name: String = "Alignment"

    @Composable
    override fun Content(onBack: () -> Unit) {
        AlignmentSampleContent(onBack = onBack)
    }
}

@Composable
fun AlignmentSampleContent(
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

            AlignmentElement(
                modifier = Modifier.align(Alignment.TopStart),
                text = "Top Start",
            )
            AlignmentElement(
                modifier = Modifier.align(Alignment.TopCenter),
                text = "Top Center",
            )
            AlignmentElement(
                modifier = Modifier.align(Alignment.TopEnd),
                text = "Top End",
            )
            AlignmentElement(
                modifier = Modifier.align(Alignment.CenterStart),
                text = "Center Start",
            )
            AlignmentElement(
                modifier = Modifier.align(Alignment.Center),
                text = "Center",
            )
            AlignmentElement(
                modifier = Modifier.align(Alignment.CenterEnd),
                text = "Center End",
            )
            AlignmentElement(
                modifier = Modifier.align(Alignment.BottomStart),
                text = "Bottom Start",
            )
            AlignmentElement(
                modifier = Modifier.align(Alignment.BottomCenter),
                text = "Bottom Center",
            )
            AlignmentElement(
                modifier = Modifier.align(Alignment.BottomEnd),
                text = "Bottom End",
            )

            SampleTopAppBar(
                title = "Alignment",
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
fun AlignmentElement(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .alpha(0.5f)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.small,
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Preview
@Composable
private fun Preview() {
    ComposeCollapsingTopBarTheme {
        AlignmentSampleContent(
            onBack = {},
        )
    }
}
