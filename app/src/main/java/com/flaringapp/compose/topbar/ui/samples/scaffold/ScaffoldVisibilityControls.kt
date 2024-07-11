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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flaringapp.compose.topbar.ui.theme.ComposeCollapsingTopBarTheme

@Composable
fun ScaffoldVisibilityControls(
    doggoVisible: Boolean,
    columnVisible: Boolean,
    changeDoggoVisible: (Boolean) -> Unit,
    changeColumnVisible: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        VisibilityControl(
            modifier = Modifier.weight(1f),
            label = "Doggo \uD83D\uDC36 visible",
            checked = doggoVisible,
            onCheckedChange = changeDoggoVisible,
        )

        VisibilityControl(
            modifier = Modifier.weight(1f),
            label = "Column visible",
            checked = columnVisible,
            onCheckedChange = changeColumnVisible,
        )
    }
}

@Composable
private fun VisibilityControl(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )

        Text(
            text = label,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    ComposeCollapsingTopBarTheme {
        ScaffoldVisibilityControls(
            modifier = Modifier.fillMaxWidth(),
            doggoVisible = true,
            columnVisible = true,
            changeDoggoVisible = {},
            changeColumnVisible = {},
        )
    }
}
