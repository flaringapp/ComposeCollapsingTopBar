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
import androidx.compose.runtime.getValue
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
import com.flaringapp.compose.topbar.CollapsingTopBarControls
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffoldState
import com.flaringapp.compose.topbar.scaffold.rememberCollapsingTopBarScaffoldState
import com.flaringapp.compose.topbar.ui.samples.common.rememberSampleExpandRequestHandler
import com.flaringapp.compose.topbar.ui.theme.ComposeCollapsingTopBarTheme

@Composable
fun ScaffoldStateControls(
    state: CollapsingTopBarScaffoldState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
    ) {
        ControlsItem(
            modifier = Modifier.weight(1f),
            state = state,
            name = "Scaffold",
            isExpanded = { it.isExpanded },
            isCollapsed = { it.isCollapsed },
        )

        ControlsItem(
            modifier = Modifier.weight(1f),
            state = state.topBarState,
            name = "Top Bar",
            isExpanded = { it.isExpanded },
            isCollapsed = { it.isCollapsed },
        )

        if (state.exitState.isEnabled) {
            ControlsItem(
                modifier = Modifier.weight(1f),
                state = state.exitState,
                name = "Exit",
                isExpandedName = "Entered",
                isCollapsedName = "Exited",
                expandName = "Enter",
                collapseName = "Exit",
                isExpanded = { it.isFullyEntered },
                isCollapsed = { it.isFullyExited },
            )
        }
    }
}

@Composable
private fun <State : CollapsingTopBarControls> ControlsItem(
    state: State,
    name: String,
    isExpanded: (State) -> Boolean,
    isCollapsed: (State) -> Boolean,
    modifier: Modifier = Modifier,
    isExpandedName: String = "Expanded",
    isCollapsedName: String = "Collapsed",
    expandName: String = "Expand",
    collapseName: String = "Collapse",
) {
    var expandRequest: Boolean? by rememberSampleExpandRequestHandler(state)

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
                state = isExpanded(state),
            ),
            style = MaterialTheme.typography.bodyMedium,
        )

        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = formatStateText(
                stateName = isCollapsedName,
                state = isCollapsed(state),
            ),
            style = MaterialTheme.typography.bodyMedium,
        )

        TextButton(
            onClick = { expandRequest = true },
        ) {
            Text(expandName)
        }

        TextButton(
            onClick = { expandRequest = false },
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
        ScaffoldStateControls(
            state = rememberCollapsingTopBarScaffoldState(),
        )
    }
}
