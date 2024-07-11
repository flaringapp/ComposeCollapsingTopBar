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

package com.flaringapp.compose.topbar

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.Stable

/**
 * A functional contract of any top bar state that manages collapsing behavior. Allows to
 * [collapse] and [expand], as well as provides animation utility methods.
 */
@Stable
interface CollapsingTopBarControls {

    /**
     * Animates top bar state collapsing height to its maximum value, i.e. expands.
     *
     * @param animationSpec the animation spec of expand animation.
     */
    suspend fun expand(
        animationSpec: AnimationSpec<Float> = tween(300),
    )

    /**
     * Animates top bar state collapsing height to its minimum value, i.e. collapses.
     *
     * @param animationSpec the animation spec of collapse animation.
     */
    suspend fun collapse(
        animationSpec: AnimationSpec<Float> = tween(300),
    )

    /**
     * An utility method to perform collapsing top bar height animation with [ScrollableState].
     * Useful for state implementations that also implement [ScrollableState].
     *
     * @param offset the offset of top bar height to be applied with an animation.
     * @param animationSpec the animation spec of height animation.
     */
    suspend fun ScrollableState.animateScrollBy(
        offset: Float,
        animationSpec: AnimationSpec<Float>,
    ) {
        scroll {
            animateScrollBy(
                offset = offset,
                animationSpec = animationSpec,
            )
        }
    }

    /**
     * An utility method to perform collapsing top bar height animation on [ScrollScope].
     *
     * @param offset the offset of top bar height to be applied with an animation.
     * @param animationSpec the animation spec of height animation.
     */
    suspend fun ScrollScope.animateScrollBy(
        offset: Float,
        animationSpec: AnimationSpec<Float>,
    ) {
        if (offset == 0f) return

        val animation = AnimationState(0f)
        var previousAnimatedValue = animation.value

        animation.animateTo(
            targetValue = offset,
            animationSpec = animationSpec,
        ) {
            scrollBy(value - previousAnimatedValue)
            previousAnimatedValue = value
        }
    }
}
