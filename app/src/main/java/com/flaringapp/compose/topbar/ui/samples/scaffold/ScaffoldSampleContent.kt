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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flaringapp.compose.topbar.nestedcollapse.CollapsingTopBarColumn
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffold
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffoldState
import com.flaringapp.compose.topbar.scaffold.rememberCollapsingTopBarScaffoldState
import com.flaringapp.compose.topbar.screen
import com.flaringapp.compose.topbar.snap.CollapsingTopBarNoSnapBehavior
import com.flaringapp.compose.topbar.snap.rememberCollapsingTopBarSnapBehavior
import com.flaringapp.compose.topbar.ui.samples.CollapsingTopBarSampleDogDefaults
import com.flaringapp.compose.topbar.ui.samples.common.SampleFilterChips
import com.flaringapp.compose.topbar.ui.samples.common.SampleTopAppBar
import com.flaringapp.compose.topbar.ui.samples.common.SampleTopBarBanner
import com.flaringapp.compose.topbar.ui.samples.common.SampleTopBarImage
import com.flaringapp.compose.topbar.ui.theme.ComposeCollapsingTopBarTheme

@Composable
fun ScaffoldSampleContent(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        CollapsingColumn(
            onBack = onBack,
        )
    }
}

@Composable
private fun CollapsingColumn(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scaffoldState = rememberCollapsingTopBarScaffoldState()

    var scrollControlMode: ScaffoldScrollControlMode by remember {
        mutableStateOf(ScaffoldScrollControlMode.Collapse)
    }
    var scrollEnterAlways by remember {
        mutableStateOf(false)
    }
    var snapEnabled by remember {
        mutableStateOf(true)
    }

    var showDoggo by remember {
        mutableStateOf(true)
    }
    var showColumn by remember {
        mutableStateOf(true)
    }

    val snapBehavior = if (snapEnabled) {
        rememberCollapsingTopBarSnapBehavior(threshold = 0.5f)
    } else {
        CollapsingTopBarNoSnapBehavior
    }

    CollapsingTopBarScaffold(
        modifier = modifier,
        state = scaffoldState,
        scrollMode = scrollControlMode.rememberScrollMode(expandAlways = scrollEnterAlways),
        snapBehavior = snapBehavior,
        topBar = { topBarState ->
            if (showDoggo) {
                SampleTopBarImage(
                    dog = CollapsingTopBarSampleDogDefaults.Column,
                )
            }

            if (showColumn) {
                val topBarShape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)

                CollapsingTopBarColumn(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            shape = topBarShape,
                        ),
                    state = topBarState,
                ) {
                    SampleTopBarBanner(
                        shape = topBarShape,
                        windowInsets = WindowInsets.screen.only(WindowInsetsSides.Top),
                    )

                    SampleTopAppBar(
                        modifier = Modifier.notCollapsible(),
                        title = "Scaffold Playground",
                        onBack = onBack,
                        containerColor = Color.Transparent,
                    )

                    SampleFilterChips()
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

                ContentScrollControls(
                    scrollMode = scrollControlMode,
                    scrollEnterAlways = scrollEnterAlways,
                    snapEnabled = snapEnabled,
                    changeScrollMode = { scrollControlMode = it },
                    changeScrollEnterAlways = { scrollEnterAlways = it },
                    changeSnapEnabled = { snapEnabled = it },
                )

                ContentStateControls(
                    modifier = Modifier.padding(top = 8.dp),
                    state = scaffoldState,
                )

                ScaffoldVisibilityControls(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    doggoVisible = showDoggo,
                    columnVisible = showColumn,
                    changeDoggoVisible = { showDoggo = it },
                    changeColumnVisible = { showColumn = it },
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
            text = "Content beginning \uD83D\uDC47",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
        )

        HorizontalDivider(
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ContentScrollControls(
    scrollMode: ScaffoldScrollControlMode,
    scrollEnterAlways: Boolean,
    snapEnabled: Boolean,
    changeScrollMode: (ScaffoldScrollControlMode) -> Unit,
    changeScrollEnterAlways: (Boolean) -> Unit,
    changeSnapEnabled: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        ScaffoldControlsTitleText(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp),
            text = "Scroll Controls",
        )

        ScaffoldScrollControls(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            selectedMode = scrollMode,
            selectMode = changeScrollMode,
        )

        Row(
            modifier = Modifier.padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = scrollEnterAlways,
                onCheckedChange = changeScrollEnterAlways,
                enabled = scrollMode !is ScaffoldScrollControlMode.EnterAlwaysCollapsed,
            )

            Text(
                text = "Enter always",
            )

            Spacer(
                modifier = Modifier.weight(1f),
            )

            Text(
                text = "Snap",
            )

            Checkbox(
                checked = snapEnabled,
                onCheckedChange = changeSnapEnabled,
            )
        }
    }
}

@Composable
private fun ContentStateControls(
    state: CollapsingTopBarScaffoldState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        ScaffoldControlsTitleText(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp),
            text = "State Controls",
        )

        ScaffoldStateControls(
            state = state,
        )
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
        ScaffoldSampleContent(
            onBack = {},
        )
    }
}
