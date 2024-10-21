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

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SingleChoiceSegmentedButtonRowScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.flaringapp.compose.topbar.nestedcollapse.CollapsingTopBarColumnDirection
import com.flaringapp.compose.topbar.ui.theme.ComposeCollapsingTopBarTheme

@Composable
fun ScaffoldColumnDirectionControls(
    selectedMode: ScaffoldColumnDirectionMode,
    selectMode: (ScaffoldColumnDirectionMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val modes = remember {
        mutableStateListOf(
            ScaffoldColumnDirectionMode.BottomUp,
            ScaffoldColumnDirectionMode.TopToBottom,
        )
    }

    SingleChoiceSegmentedButtonRow(
        modifier = modifier.height(IntrinsicSize.Max),
    ) {
        modes.forEach { mode ->
            CollapseModeButton(
                modifier = Modifier.fillMaxHeight(),
                mode = mode,
                selected = mode == selectedMode,
                select = { selectMode(mode) },
            )
        }
    }
}

@Composable
private fun SingleChoiceSegmentedButtonRowScope.CollapseModeButton(
    mode: ScaffoldColumnDirectionMode,
    selected: Boolean,
    select: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SegmentedButton(
        modifier = modifier,
        selected = selected,
        onClick = select,
        shape = RectangleShape,
    ) {
        Text(
            text = mode.name,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Immutable
sealed class ScaffoldColumnDirectionMode {

    abstract val name: String

    abstract fun provideColumnDirection(): CollapsingTopBarColumnDirection

    data object BottomUp : ScaffoldColumnDirectionMode() {

        override val name: String
            get() = "Bottom up"

        override fun provideColumnDirection(): CollapsingTopBarColumnDirection {
            return CollapsingTopBarColumnDirection.BottomUp
        }
    }

    data object TopToBottom : ScaffoldColumnDirectionMode() {

        override val name: String
            get() = "Top to bottom"

        override fun provideColumnDirection(): CollapsingTopBarColumnDirection {
            return CollapsingTopBarColumnDirection.TopToBottom
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    ComposeCollapsingTopBarTheme {
        ScaffoldColumnDirectionControls(
            modifier = Modifier.fillMaxWidth(),
            selectedMode = ScaffoldColumnDirectionMode.BottomUp,
            selectMode = {},
        )
    }
}
