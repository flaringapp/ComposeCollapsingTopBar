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

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.annotation.RememberInComposition
import androidx.compose.runtime.remember
import com.flaringapp.compose.topbar.CollapsingTopBarControls.Companion.DefaultAnimationSpec

/**
 * Defines the logic of snap animation.
 *
 * When nested scroll fling has ended, [snap] is called to perform snapping animation with
 * [CollapsingTopBarSnapScope] receiver.
 */
@Immutable
interface CollapsingTopBarSnapBehavior {

    /**
     * Performs snapping animation with [CollapsingTopBarSnapScope] receiver.
     *
     * @param wasMovingUp whether a fling motion that preceded snapping was directed upwards
     * (with negative velocity) or not.
     */
    suspend fun CollapsingTopBarSnapScope.snap(wasMovingUp: Boolean)
}

/**
 * A simple implementation of [CollapsingTopBarSnapBehavior] that **does not** perform snapping.
 */
object CollapsingTopBarNoSnapBehavior : CollapsingTopBarSnapBehavior {

    override suspend fun CollapsingTopBarSnapScope.snap(wasMovingUp: Boolean) = Unit
}

/**
 * Performs snapping based on current [CollapsingTopBarSnapScope] collapse progress received in
 * lambda [CollapsingTopBarSnapScope.snapWithProgress]. Uses [threshold] as a bound to determine
 * in which direction to snap.
 *
 * @param threshold the fraction of collapse progress, a bound to define in which direction to snap.
 * If current collapse progress is larger than this value, then expand snapping is performed;
 * collapse snapping is performed otherwise.
 * @param animationSpec the animation spec of snap animation.
 */
class CollapsingTopBarThresholdSnapBehavior @RememberInComposition constructor(
    private val threshold: Float = 0.5f,
    private val animationSpec: AnimationSpec<Float> = DefaultAnimationSpec,
) : CollapsingTopBarSnapBehavior {

    override suspend fun CollapsingTopBarSnapScope.snap(wasMovingUp: Boolean) {
        snapWithProgress(wasMovingUp) { progress ->
            if (progress >= threshold) {
                expand(animationSpec)
            } else {
                collapse(animationSpec)
            }
        }
    }
}

/**
 * Create and remember [CollapsingTopBarThresholdSnapBehavior] which performs snapping based on
 * current collapse progress and specified [threshold].
 *
 * @param threshold the fraction of collapse progress, a bound to define in which direction to snap.
 * If current collapse progress is larger than this value, then expand snapping is performed;
 * collapse snapping is performed otherwise.
 * @param animationSpec the animation spec of snap animation.
 *
 * @see CollapsingTopBarThresholdSnapBehavior
 */
@Composable
fun rememberCollapsingTopBarSnapBehavior(
    threshold: Float = 0.5f,
    animationSpec: AnimationSpec<Float> = DefaultAnimationSpec,
): CollapsingTopBarSnapBehavior {
    return remember(threshold) {
        CollapsingTopBarThresholdSnapBehavior(
            threshold = threshold,
            animationSpec = animationSpec,
        )
    }
}
