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

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flaringapp.compose.topbar.ui.theme.ComposeCollapsingTopBarTheme

@Composable
fun SampleTopBarBanner(
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    windowInsets: WindowInsets = WindowInsets(0),
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = shape,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .windowInsetsPadding(windowInsets),
        ) {
            Text(
                text = "Save your time",
                style = LocalTextStyle.current.copy(
                    brush = rememberTitleAnimatedBrush(),
                ),
                fontWeight = FontWeight.Bold,
            )

            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = formatBannerMessage(colorScheme = MaterialTheme.colorScheme),
            )
        }
    }
}

private fun formatBannerMessage(
    colorScheme: ColorScheme,
): AnnotatedString = buildAnnotatedString {
    append("With ")
    withStyle(SpanStyle(color = colorScheme.primary, fontWeight = FontWeight.Bold)) {
        append("ComposeCollapsingTopBar")
    }
}

@Composable
private fun rememberTitleAnimatedBrush(): Brush {
    val colorScheme = MaterialTheme.colorScheme

    val infiniteTransition = rememberInfiniteTransition(
        label = "BrushOffsetTransition",
    )

    val offset by infiniteTransition.animateFloat(
        label = "BrushOffsetAnimation",
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
    )

    return remember(colorScheme, offset) {
        val colors = listOf(
            colorScheme.error,
            colorScheme.tertiary,
            colorScheme.inversePrimary,
            colorScheme.error,
            colorScheme.tertiary,
        )

        object : ShaderBrush() {
            override fun createShader(size: Size): Shader {
                val widthOffset = size.width * offset
                val heightOffset = size.height * offset
                return LinearGradientShader(
                    colors = colors,
                    from = Offset(widthOffset, heightOffset),
                    to = Offset(widthOffset + size.width, heightOffset + size.height),
                    tileMode = TileMode.Mirror,
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ComposeCollapsingTopBarTheme {
        SampleTopBarBanner()
    }
}
