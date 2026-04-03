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

package com.flaringapp.compose.topbar.sample.shared.ui.samples.column

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flaringapp.compose.topbar.nestedcollapse.CollapsingTopBarColumn
import com.flaringapp.compose.topbar.sample.shared.ui.samples.CollapsingTopBarSample
import com.flaringapp.compose.topbar.sample.shared.ui.samples.common.SampleContent
import com.flaringapp.compose.topbar.sample.shared.ui.samples.common.SampleTopAppBar
import com.flaringapp.compose.topbar.sample.shared.ui.theme.ComposeCollapsingTopBarTheme
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffold
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffoldScrollMode

object ColumnAlignmentSample : CollapsingTopBarSample {

    override val name: String = "Column Alignment"

    @Composable
    override fun Content(onBack: () -> Unit) {
        ColumnAlignmentSampleContent(onBack = onBack)
    }
}

@Composable
fun ColumnAlignmentSampleContent(
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
                SampleTopAppBar(
                    modifier = Modifier.notCollapsible(),
                    title = "Column Alignment",
                    onBack = onBack,
                    containerColor = Color.Transparent,
                )

                AlignmentElement(
                    modifier = Modifier.align(Alignment.Start),
                )

                AlignmentElement(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )

                AlignmentElement(
                    modifier = Modifier.align(Alignment.End),
                )
            }
        },
        body = {
            SampleContent()
        },
    )
}

@Composable
private fun AlignmentElement(
    modifier: Modifier = Modifier,
) {
    Spacer(
        modifier = modifier
            .size(width = 64.dp, height = 40.dp)
            .background(MaterialTheme.colorScheme.secondaryContainer),
    )
}

@Preview
@Composable
private fun Preview() {
    ComposeCollapsingTopBarTheme {
        ColumnAlignmentSampleContent(
            onBack = {},
        )
    }
}
