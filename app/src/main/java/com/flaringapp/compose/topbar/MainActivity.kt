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

package com.flaringapp.compose.topbar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flaringapp.compose.topbar.nestedcollapse.CollapsingTopBarColumn
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffold
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffoldScrollMode
import com.flaringapp.compose.topbar.scaffold.rememberCollapsingTopBarScaffoldState
import com.flaringapp.compose.topbar.snap.rememberCollapsingTopBarSnapBehavior
import com.flaringapp.compose.topbar.ui.theme.ComposeCollapsingTopBarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeCollapsingTopBarTheme {
                Content()
            }
        }
    }
}

@Composable
private fun Content() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        CollapsingColumn()
    }
}

@Composable
private fun CollapsingColumn() {
    val scaffoldState = rememberCollapsingTopBarScaffoldState()

    var showBox by remember {
        mutableStateOf(false)
    }
    var showColumn by remember {
        mutableStateOf(true)
    }

    CollapsingTopBarScaffold(
        modifier = Modifier.fillMaxSize(),
        state = scaffoldState,
        scrollMode = CollapsingTopBarScaffoldScrollMode.enterAlwaysCollapsed(),
        snapBehavior = rememberCollapsingTopBarSnapBehavior(threshold = 0.5f),
        topBar = {
            if (showBox) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp)
                        .background(Color.Yellow),
                )
            }

            if (showColumn) {
                CollapsingTopBarColumn(
                    modifier = Modifier.background(Color.LightGray),
                    state = scaffoldState.topBarState,
                ) {
                    Button(
                        modifier = Modifier.notCollapsible(),
                        onClick = { showBox = !showBox },
                    ) {
                        Text(text = "${if (showBox) "Hide" else "Show"} box")
                    }

                    SampleItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(Color.Green),
                    )

                    SampleItem(
                        modifier = Modifier
                            .width(100.dp)
                            .height(40.dp)
                            .background(Color.Blue),
                    )

                    SampleItem(
                        modifier = Modifier
                            .width(200.dp)
                            .height(60.dp)
                            .background(Color.Black)
                            .notCollapsible(),
                    )

                    SampleItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(Color.Magenta),
                    )

                    SampleItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(Color.Red),
                    )
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Button(
                    modifier = Modifier.align(Alignment.TopEnd),
                    onClick = { showColumn = !showColumn },
                ) {
                    Text(text = "${if (showColumn) "Hide" else "Show"} column")
                }
            }
        },
        body = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                repeat(50) {
                    Text(it.toString())
                }
            }
        },
    )
}

@Composable
private fun SampleItem(
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = buildString {
            repeat(50) {
                append("Hello $it")
            }
        },
    )
}

@Preview
@Composable
private fun Preview() {
    ComposeCollapsingTopBarTheme {
        Content()
    }
}
