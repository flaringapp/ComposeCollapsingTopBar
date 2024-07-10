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

package com.flaringapp.compose.topbar.ui.samples.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.flaringapp.compose.topbar.ui.theme.ComposeCollapsingTopBarTheme

@Composable
fun SampleVerticalFadingEdge(
    modifier: Modifier = Modifier,
    height: Dp = 12.dp,
    color: Color = MaterialTheme.colorScheme.surface,
) {
    val brush = remember(color) {
        Brush.verticalGradient(
            listOf(color, color.copy(alpha = 0f)),
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(brush),
    )
}

@Preview
@Composable
private fun Preview() {
    ComposeCollapsingTopBarTheme {
        SampleVerticalFadingEdge()
    }
}
