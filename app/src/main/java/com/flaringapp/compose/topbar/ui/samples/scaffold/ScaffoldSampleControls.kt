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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffoldState
import com.flaringapp.compose.topbar.scaffold.rememberCollapsingTopBarScaffoldState
import com.flaringapp.compose.topbar.ui.theme.ComposeCollapsingTopBarTheme

@Composable
fun ScaffoldSampleControls(
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
            text = formatStateText(
                stateName = isExpandedName,
                state = isExpanded,
            ),
            style = MaterialTheme.typography.bodyMedium,
        )

        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = formatStateText(
                stateName = isCollapsedName,
                state = isCollapsed,
            ),
            style = MaterialTheme.typography.bodyMedium,
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

private fun formatStateText(
    stateName: String,
    state: Boolean,
): AnnotatedString = buildAnnotatedString {
    append(stateName)
    append(": ")
    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
        append(state.toString())
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    ComposeCollapsingTopBarTheme {
        ScaffoldSampleControls(
            state = rememberCollapsingTopBarScaffoldState(),
        )
    }
}
