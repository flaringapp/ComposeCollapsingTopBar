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

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flaringapp.compose.topbar.nestedcollapse.CollapsingTopBarColumn
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffold
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffoldScrollMode
import com.flaringapp.compose.topbar.ui.samples.CollapsingTopBarSample
import com.flaringapp.compose.topbar.ui.samples.common.SampleContent
import com.flaringapp.compose.topbar.ui.samples.common.SampleFilterChips
import com.flaringapp.compose.topbar.ui.samples.common.SampleTopAppBar
import com.flaringapp.compose.topbar.ui.samples.common.SampleTopBarBanner
import com.flaringapp.compose.topbar.ui.samples.common.SampleVerticalFadingEdge
import com.flaringapp.compose.topbar.ui.theme.ComposeCollapsingTopBarTheme

object FullyCollapsibleColumnSample : CollapsingTopBarSample {

    override val name: String = "Fully Collapsible Column"

    @Composable
    override fun Content(onBack: () -> Unit) {
        FullyCollapsibleColumnSampleContent(onBack = onBack)
    }
}

@Composable
fun FullyCollapsibleColumnSampleContent(
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
                SampleTopBarBanner(
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                    windowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top),
                )

                SampleTopAppBar(
                    modifier = Modifier.padding(top = 8.dp),
                    title = "Fully Collapsible Column",
                    onBack = onBack,
                    ignoreWindowInsets = true,
                )

                SampleVerticalFadingEdge()
                SampleFilterChips()
            }
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
        FullyCollapsibleColumnSampleContent(
            onBack = {},
        )
    }
}
