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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flaringapp.compose.topbar.ui.theme.ComposeCollapsingTopBarTheme

@Composable
inline fun SampleContent(
    modifier: Modifier = Modifier,
    before: ColumnScope.() -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        before()

        repeat(50) {
            SampleContentItem(
                index = it,
            )
        }
    }
}

@Composable
fun SampleContentItem(
    index: Int,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = lerp(Color.LightGray, Color.DarkGray, index % 10 / 9f)
    val textColor = if (index % 10 >= 3) {
        Color.White
    } else {
        Color.Black
    }

    Text(
        modifier = modifier
            .background(backgroundColor)
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 16.dp),
        text = "Item $index",
        color = textColor,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.titleMedium,
    )
}

@Preview
@Composable
private fun Preview() {
    ComposeCollapsingTopBarTheme {
        SampleContent()
    }
}

@Preview
@Composable
private fun PreviewWithBefore() {
    ComposeCollapsingTopBarTheme {
        SampleContent {
            Text(
                modifier = Modifier
                    .background(Color.Red)
                    .padding(40.dp)
                    .align(Alignment.CenterHorizontally),
                text = "Something before",
            )
        }
    }
}
