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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.zIndex
import com.flaringapp.compose.topbar.nestedcollapse.CollapsingTopBarColumn
import com.flaringapp.compose.topbar.nestedcollapse.CollapsingTopBarColumnScope
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffold
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffoldScrollMode
import com.flaringapp.compose.topbar.ui.samples.CollapsingTopBarSample
import com.flaringapp.compose.topbar.ui.samples.common.SampleContent
import com.flaringapp.compose.topbar.ui.samples.common.SampleTopAppBar
import com.flaringapp.compose.topbar.ui.theme.ComposeCollapsingTopBarTheme

object ColumnMovingElementSample : CollapsingTopBarSample {

    override val name: String = "Column Moving Element"

    @Composable
    override fun Content(onBack: () -> Unit) {
        ColumnMovingElementSampleContent(onBack = onBack)
    }
}

@Composable
fun ColumnMovingElementSampleContent(
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
                    title = "Column Moving Element",
                    onBack = onBack,
                )

                CollapsibleSlidingAction(
                    modifier = Modifier.zIndex(1f),
                )
            }
        },
        body = {
            SampleContent()
        },
    )
}

@Composable
private fun CollapsingTopBarColumnScope.CollapsibleSlidingAction(
    modifier: Modifier = Modifier,
) {
    var collapseProgress by remember {
        mutableFloatStateOf(1f)
    }
    val twiceAsFastCollapseProgress by remember {
        derivedStateOf {
            ((collapseProgress - 0.5f) * 2).coerceAtLeast(0f)
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .columnProgress { _, itemProgress -> collapseProgress = itemProgress },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.graphicsLayer {
                alpha = twiceAsFastCollapseProgress
                translationY = size.height * (1f - collapseProgress)
            },
            text = "Collapsible element",
        )

        Box(
            modifier = Modifier
                .size(40.dp)
                .graphicsLayer {
                    translationX = lerp(56.dp, 0.dp, twiceAsFastCollapseProgress).toPx()
                }
                .background(color = Color.Red, shape = CircleShape),
        )
    }
}

@Preview
@Composable
private fun Preview() {
    ComposeCollapsingTopBarTheme {
        ColumnMovingElementSampleContent(
            onBack = {},
        )
    }
}
