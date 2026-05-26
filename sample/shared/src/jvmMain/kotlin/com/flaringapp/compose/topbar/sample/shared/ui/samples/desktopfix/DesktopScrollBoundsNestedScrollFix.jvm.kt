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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.awtEventOrNull
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.flaringapp.compose.topbar.scaffold.CollapsingTopBarScaffoldState
import java.awt.event.MouseWheelEvent
import kotlin.math.sqrt

internal actual fun Modifier.desktopScrollBoundsNestedScrollFix(
    state: CollapsingTopBarScaffoldState,
    isContentAtTop: () -> Boolean,
): Modifier = composed {
    val currentIsContentAtTop by rememberUpdatedState(isContentAtTop)

    pointerInput(state) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Initial)

                if (
                    event.type != PointerEventType.Scroll ||
                    event.changes.any { it.isConsumed } ||
                    !currentIsContentAtTop()
                ) {
                    continue
                }

                val consumed = dispatchDesktopScrollBoundsDelta(
                    availableY = event.calculateAvailableY(
                        density = this,
                        viewportHeight = size.height,
                    ),
                    dispatchExitDelta = state.exitState::dispatchRawDelta,
                    dispatchTopBarDelta = state.topBarState::dispatchRawDelta,
                )

                if (consumed != 0f) {
                    event.changes.forEach { it.consume() }
                }
            }
        }
    }
}

private fun dispatchDesktopScrollBoundsDelta(
    availableY: Float,
    dispatchExitDelta: (Float) -> Float,
    dispatchTopBarDelta: (Float) -> Float,
): Float {
    if (availableY <= 0f) {
        return 0f
    }

    var left = availableY
    left -= dispatchExitDelta(left)
    left -= dispatchTopBarDelta(left)

    return availableY - left
}

private fun PointerEvent.calculateAvailableY(
    density: Density,
    viewportHeight: Int,
): Float {
    val scrollDelta = changes
        .sumOf { it.scrollDelta.y.toDouble() }
        .toFloat()
    val scrollAmount = (awtEventOrNull as? MouseWheelEvent)?.scrollAmount?.toFloat() ?: 1f
    val scrollScale = calculateScrollScale(
        density = density,
        viewportHeight = viewportHeight,
    )

    return (-scrollDelta * scrollScale * scrollAmount).coerceAtLeast(0f)
}

private fun PointerEvent.calculateScrollScale(
    density: Density,
    viewportHeight: Int,
): Float {
    val awtEvent = awtEventOrNull as? MouseWheelEvent
    if (awtEvent?.scrollType == MouseWheelEvent.WHEEL_BLOCK_SCROLL) {
        return viewportHeight.toFloat()
    }

    return when (CurrentDesktopPlatform) {
        DesktopPlatform.Linux -> sqrt(viewportHeight.toFloat())

        DesktopPlatform.MacOS -> with(density) { MAC_OS_SCROLL_STEP_DP.dp.toPx() }

        DesktopPlatform.Windows,
        DesktopPlatform.Unknown,
            -> viewportHeight / WINDOWS_SCROLL_VIEWPORT_FRACTION
    }
}

private val CurrentDesktopPlatform: DesktopPlatform by lazy {
    detectCurrentDesktopPlatform()
}

private fun detectCurrentDesktopPlatform(): DesktopPlatform {
    val name = System.getProperty("os.name") ?: return DesktopPlatform.Unknown
    return when {
        name.startsWith("Linux") -> DesktopPlatform.Linux
        name.startsWith("Win") -> DesktopPlatform.Windows
        name == "Mac OS X" -> DesktopPlatform.MacOS
        else -> DesktopPlatform.Unknown
    }
}

private enum class DesktopPlatform {
    Linux,
    MacOS,
    Windows,
    Unknown,
}

private const val MAC_OS_SCROLL_STEP_DP = 10
private const val WINDOWS_SCROLL_VIEWPORT_FRACTION = 20f
