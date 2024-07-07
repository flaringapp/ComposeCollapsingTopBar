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

package com.flaringapp.compose.topbar.snap

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

interface CollapsingTopBarSnapBehavior {

    suspend fun CollapsingTopBarSnapScope.snap(wasMovingUp: Boolean)
}

object CollapsingTopBarNoSnapBehavior : CollapsingTopBarSnapBehavior {

    override suspend fun CollapsingTopBarSnapScope.snap(wasMovingUp: Boolean) = Unit
}

class CollapsingTopBarThresholdSnapBehavior(
    private val threshold: Float,
) : CollapsingTopBarSnapBehavior {

    override suspend fun CollapsingTopBarSnapScope.snap(wasMovingUp: Boolean) {
        snapWithProgress(wasMovingUp) { progress ->
            if (progress >= threshold) {
                expand()
            } else {
                collapse()
            }
        }
    }
}

@Composable
fun rememberCollapsingTopBarSnapBehavior(
    threshold: Float,
): CollapsingTopBarSnapBehavior {
    return remember(threshold) {
        CollapsingTopBarThresholdSnapBehavior(
            threshold = threshold,
        )
    }
}
