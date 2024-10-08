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

package com.flaringapp.compose.topbar.nestedscroll

import androidx.compose.ui.unit.Velocity
import com.flaringapp.compose.topbar.snap.CollapsingTopBarSnapBehavior
import com.flaringapp.compose.topbar.snap.CollapsingTopBarSnapScope

/**
 * A nested scroll handler that executes top bar snapping after fling has ended on scope
 * [snapScope] with behavior [snapBehavior].
 *
 * @param snapBehavior the snap behavior to be used for snapping with [snapScope] after fling.
 * @param snapScope the receiver scope to be used for executing snap animation with [snapBehavior].
 */
class CollapsingTopBarNestedScrollSnap(
    private val snapBehavior: CollapsingTopBarSnapBehavior,
    private val snapScope: CollapsingTopBarSnapScope,
) : CollapsingTopBarNestedScrollHandler {

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        if (available.y != 0f) return Velocity.Zero

        val wasMovingUp = consumed.y < 0

        with(snapBehavior) {
            snapScope.snap(wasMovingUp)
        }

        return Velocity(0f, available.y)
    }
}
