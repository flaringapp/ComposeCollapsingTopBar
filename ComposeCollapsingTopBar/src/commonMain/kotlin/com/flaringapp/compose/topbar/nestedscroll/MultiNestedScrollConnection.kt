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

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity

/**
 * A [NestedScrollConnection] implementation that delegates nested scroll callbacks to [delegates]
 * while respecting their order: each subsequent delegate will receive available scroll minus
 * the amount consumed by all previous delegates.
 */
class MultiNestedScrollConnection(
    private val delegates: List<NestedScrollConnection>,
) : NestedScrollConnection {

    override fun onPreScroll(
        available: Offset,
        source: NestedScrollSource,
    ): Offset = delegates.fold(Offset.Zero) { delegatesConsumed, delegate ->
        delegate.onPreScroll(
            available = available - delegatesConsumed,
            source = source,
        ) + delegatesConsumed
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource,
    ): Offset = delegates.fold(Offset.Zero) { delegatesConsumed, delegate ->
        delegate.onPostScroll(
            consumed = consumed + delegatesConsumed,
            available = available - delegatesConsumed,
            source = source,
        ) + delegatesConsumed
    }

    override suspend fun onPreFling(
        available: Velocity,
    ): Velocity = delegates.fold(Velocity.Zero) { delegatesConsumed, delegate ->
        delegate.onPreFling(
            available = available - delegatesConsumed,
        ) + delegatesConsumed
    }

    override suspend fun onPostFling(
        consumed: Velocity,
        available: Velocity,
    ): Velocity = delegates.fold(Velocity.Zero) { delegatesConsumed, delegate ->
        delegate.onPostFling(
            consumed = consumed + delegatesConsumed,
            available = available - delegatesConsumed,
        ) + delegatesConsumed
    }
}
