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

package com.flaringapp.compose.topbar.sample.shared.ui.samples.desktopfix

import androidx.compose.ui.Modifier
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffoldState

/**
 * Sample-only workaround for CMP-10236.
 *
 * https://youtrack.jetbrains.com/issue/CMP-10236
 *
 * Compose Desktop can skip parent nested-scroll `onPostScroll` when scrollable content is already
 * at its bounds. This modifier forwards that leftover wheel delta to the scaffold state until
 * Compose handles the case upstream.
 */
internal expect fun Modifier.desktopScrollBoundsNestedScrollFix(
    state: CollapsingTopBarScaffoldState,
    isContentAtTop: () -> Boolean,
): Modifier
