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

package com.flaringapp.compose.topbar.ui.samples.scaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flaringapp.compose.topbar.nestedcollapse.CollapsingTopBarColumn
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffold
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffoldScrollMode
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffoldState
import com.flaringapp.compose.topbar.scaffold.rememberCollapsingTopBarScaffoldState
import com.flaringapp.compose.topbar.snap.rememberCollapsingTopBarSnapBehavior
import com.flaringapp.compose.topbar.ui.theme.ComposeCollapsingTopBarTheme

@Composable
fun ScaffoldSampleContent(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top)),
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

    var toggleExpandRequest: Boolean? by remember {
        mutableStateOf(null)
    }

    LaunchedEffect(toggleExpandRequest) {
        val expand = toggleExpandRequest ?: return@LaunchedEffect

        if (expand) {
            scaffoldState.expand()
        } else {
            scaffoldState.collapse()
        }

        toggleExpandRequest = null
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

                    HeaderTopAppBar(
                        isExpanded = !scaffoldState.isCollapsed,
                        toggleExpand = { toggleExpandRequest = scaffoldState.isCollapsed },
                    )

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
                ContentHeader()

                ContentControls(
                    state = scaffoldState,
                )

                repeat(50) {
                    ContentItem(
                        index = it,
                    )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HeaderTopAppBar(
    isExpanded: Boolean,
    toggleExpand: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val icon = if (isExpanded) {
        Icons.Rounded.KeyboardArrowUp
    } else {
        Icons.Rounded.KeyboardArrowDown
    }

    TopAppBar(
        modifier = modifier,
        title = { Text("Regular top app bar") },
        navigationIcon = {
            IconButton(
                onClick = toggleExpand,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                )
            }
        },
    )
}

@Composable
private fun ContentHeader(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(horizontal = 32.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
        )

        Text(
            text = "Content start \uD83D\uDC47",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
        )

        HorizontalDivider(
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ContentControls(
    state: CollapsingTopBarScaffoldState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "Controls",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge,
        )

        Row(
            modifier = Modifier.padding(top = 16.dp),
        ) {
            ControlsItem(
                modifier = Modifier.weight(1f),
                name = "Scaffold",
                isExpanded = state.isExpanded,
                isCollapsed = state.isCollapsed,
                onExpand = { state.expand() },
                onCollapse = { state.collapse() },
            )

            ControlsItem(
                modifier = Modifier.weight(1f),
                name = "Top Bar",
                isExpanded = state.topBarState.layoutInfo.isExpanded,
                isCollapsed = state.topBarState.layoutInfo.isCollapsed,
                onExpand = { state.topBarState.expand() },
                onCollapse = { state.topBarState.collapse() },
            )

            ControlsItem(
                modifier = Modifier.weight(1f),
                name = "Exit",
                isExpandedName = "Entered",
                isCollapsedName = "Exited",
                expandName = "Enter",
                collapseName = "Exit",
                isExpanded = state.exitState.isFullyEntered,
                isCollapsed = state.exitState.isFullyExited,
                onExpand = { state.exitState.expand() },
                onCollapse = { state.exitState.collapse() },
            )
        }
    }
}

@Composable
private fun ControlsItem(
    name: String,
    isExpanded: Boolean,
    isCollapsed: Boolean,
    onExpand: suspend () -> Unit,
    onCollapse: suspend () -> Unit,
    modifier: Modifier = Modifier,
    isExpandedName: String = "Expanded",
    isCollapsedName: String = "Collapsed",
    expandName: String = "Expand",
    collapseName: String = "Collapse",
) {
    var toggleExpandRequest: Boolean? by remember {
        mutableStateOf(null)
    }

    LaunchedEffect(toggleExpandRequest) {
        val expand = toggleExpandRequest ?: return@LaunchedEffect

        if (expand) {
            onExpand()
        } else {
            onCollapse()
        }

        toggleExpandRequest = null
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
        )

        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = "$isExpandedName: $isExpanded",
        )

        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = "$isCollapsedName: $isCollapsed",
        )

        TextButton(
            onClick = { toggleExpandRequest = true },
        ) {
            Text(expandName)
        }

        TextButton(
            onClick = { toggleExpandRequest = false },
        ) {
            Text(collapseName)
        }
    }
}

@Composable
private fun ContentItem(
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
        ScaffoldSampleContent()
    }
}