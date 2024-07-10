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

package com.flaringapp.compose.topbar.ui.samples.column

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flaringapp.compose.topbar.nestedcollapse.CollapsingTopBarColumn
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffold
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffoldScrollMode
import com.flaringapp.compose.topbar.screen
import com.flaringapp.compose.topbar.ui.samples.CollapsingTopBarSample
import com.flaringapp.compose.topbar.ui.samples.common.SampleContent
import com.flaringapp.compose.topbar.ui.samples.common.SampleFilterChips
import com.flaringapp.compose.topbar.ui.samples.common.SampleTopAppBar
import com.flaringapp.compose.topbar.ui.samples.common.SampleTopBarBanner
import com.flaringapp.compose.topbar.ui.samples.common.SampleVerticalFadingEdge
import com.flaringapp.compose.topbar.ui.theme.ComposeCollapsingTopBarTheme

object AlternatelyCollapsibleColumnSample : CollapsingTopBarSample {

    override val name: String = "Alternately Collapsible Column"

    @Composable
    override fun Content(onBack: () -> Unit) {
        AlternatelyCollapsibleColumnSampleContent(onBack = onBack)
    }
}

@Composable
fun AlternatelyCollapsibleColumnSampleContent(
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
        topBar = { topBarState ->
            CollapsingTopBarColumn(topBarState) {
                val topWindowInsetsPadding =
                    WindowInsets.screen.only(WindowInsetsSides.Top).asPaddingValues()

                FixedElement(
                    modifier = Modifier.notCollapsible(),
                    text = "Fixed top bar header",
                    contentPadding = topWindowInsetsPadding,
                )

                SampleTopBarBanner(
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                )

                SampleTopAppBar(
                    modifier = Modifier.notCollapsible(),
                    title = "Alternately Collapsible Column",
                    onBack = onBack,
                    ignoreWindowInsets = true,
                )

                SampleVerticalFadingEdge()
                SampleFilterChips()

                FixedElement(
                    modifier = Modifier.notCollapsible(),
                    text = "Fixed top bar footer",
                )
            }
        },
        body = {
            SampleContent()
        },
    )
}

@Composable
private fun FixedElement(
    text: String,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
    ) {
        Text(
            modifier = Modifier.padding(contentPadding),
            text = text,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
private fun Preview() {
    ComposeCollapsingTopBarTheme {
        AlternatelyCollapsibleColumnSampleContent(
            onBack = {},
        )
    }
}
