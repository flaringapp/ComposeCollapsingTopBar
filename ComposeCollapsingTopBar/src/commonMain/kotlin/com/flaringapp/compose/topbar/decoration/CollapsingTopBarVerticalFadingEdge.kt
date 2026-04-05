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

package com.flaringapp.compose.topbar.decoration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Draws a vertical fading edge without affecting layout height.
 *
 * This can be used as an overlay between stacked top bar elements to soften visible overlap while
 * keeping surrounding layout positions unchanged.
 *
 * @param color the opaque edge color that fades to transparent.
 * @param modifier the [Modifier] to be applied to this fading edge.
 * @param height the visible height of the fading edge.
 * @param reverse when `false`, fades from top to bottom; when `true`, fades from bottom to top.
 */
@Composable
public fun CollapsingTopBarVerticalFadingEdge(
    color: Color,
    modifier: Modifier = Modifier,
    height: Dp = 12.dp,
    reverse: Boolean = false,
) {
    val brush = remember(color, reverse) {
        if (reverse) {
            Brush.verticalGradient(
                listOf(color.copy(alpha = 0f), color),
            )
        } else {
            Brush.verticalGradient(
                listOf(color, color.copy(alpha = 0f)),
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(0.dp)
            .wrapContentHeight(Alignment.Top, unbounded = true)
            .height(height)
            .background(brush),
    )
}
