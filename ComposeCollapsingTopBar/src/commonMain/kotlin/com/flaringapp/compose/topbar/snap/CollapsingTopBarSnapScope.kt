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

import com.flaringapp.compose.topbar.CollapsingTopBarControls

/**
 * The receiver scope of [CollapsingTopBarSnapBehavior]. Creates a scoped snapping environment of
 * something that represents top bar state as [CollapsingTopBarControls], which even may be
 * dynamic. Responsible for executing snap action requested in [snapWithProgress].
 */
fun interface CollapsingTopBarSnapScope {

    /**
     * Executes snap action [action] providing current progress with [CollapsingTopBarControls]
     * receiver.
     *
     * @param wasMovingUp the last motion direction that resulted in snap being requested. May
     * help complex snap scopes decide where to snap, i.e. what [CollapsingTopBarControls] to use.
     * @param action the snap action lambda to be executed.
     */
    suspend fun snapWithProgress(
        wasMovingUp: Boolean,
        action: suspend CollapsingTopBarControls.(progress: Float) -> Unit,
    )
}
