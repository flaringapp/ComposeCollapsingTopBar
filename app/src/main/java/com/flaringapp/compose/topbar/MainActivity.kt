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

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import com.flaringapp.compose.topbar.ui.samples.CollapsingTopBarSample
import com.flaringapp.compose.topbar.ui.samples.advanced.AppBarScrimSample
import com.flaringapp.compose.topbar.ui.samples.advanced.CustomPlacementSample
import com.flaringapp.compose.topbar.ui.samples.advanced.ManualCollapsingControlsSample
import com.flaringapp.compose.topbar.ui.samples.advanced.ParallaxCollapsingSample
import com.flaringapp.compose.topbar.ui.samples.advanced.SnapCollapsingSample
import com.flaringapp.compose.topbar.ui.samples.advanced.StackedWithColumnSample
import com.flaringapp.compose.topbar.ui.samples.basic.CollapsingExitExpandAlwaysSample
import com.flaringapp.compose.topbar.ui.samples.basic.CollapsingExitExpandAtTopSample
import com.flaringapp.compose.topbar.ui.samples.basic.CollapsingExpandAlwaysSample
import com.flaringapp.compose.topbar.ui.samples.basic.CollapsingExpandAtTopSample
import com.flaringapp.compose.topbar.ui.samples.basic.EnterAlwaysCollapsedSample
import com.flaringapp.compose.topbar.ui.samples.column.AlternatelyCollapsibleColumnSample
import com.flaringapp.compose.topbar.ui.samples.column.FullyCollapsibleColumnSample
import com.flaringapp.compose.topbar.ui.samples.column.PartiallyCollapsibleColumnSample
import com.flaringapp.compose.topbar.ui.samples.gallery.SamplesGallery
import com.flaringapp.compose.topbar.ui.samples.gallery.SamplesGalleryGroup
import com.flaringapp.compose.topbar.ui.theme.ComposeCollapsingTopBarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeCollapsingTopBarTheme {
                SamplesNavigation()
            }
        }
    }
}

@Composable
private fun SamplesNavigation() {
    var selectedSample: CollapsingTopBarSample? by remember {
        mutableStateOf(null)
    }

    BackHandler(
        enabled = selectedSample != null,
    ) {
        selectedSample = null
    }

    AnimatedContent(
        modifier = Modifier.fillMaxSize(),
        label = "SampleContentAnimation",
        targetState = selectedSample,
        transitionSpec = {
            val isOpeningSample = targetState != null
            if (isOpeningSample) {
                fadeIn() + scaleIn(initialScale = 1.1f) togetherWith fadeOut() using null
            } else {
                fadeIn() togetherWith fadeOut() + scaleOut(targetScale = 1.1f) using null
            }
        },
    ) { currentSelectedSample ->
        if (currentSelectedSample != null) {
            currentSelectedSample.Content(
                onBack = { selectedSample = null },
            )
            return@AnimatedContent
        }

        SamplesGallery(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.systemBars),
            groups = rememberSampleGroups(),
            onSampleSelect = { selectedSample = it },
        )
    }
}

@Composable
private fun rememberSampleGroups(): SnapshotStateList<SamplesGalleryGroup> {
    return remember {
        mutableStateListOf(
            SamplesGalleryGroup(
                name = "Basic Samples",
                samples = mutableStateListOf(
                    CollapsingExpandAtTopSample,
                    CollapsingExpandAlwaysSample,
                    CollapsingExitExpandAtTopSample,
                    CollapsingExitExpandAlwaysSample,
                    EnterAlwaysCollapsedSample,
                ),
            ),
            SamplesGalleryGroup(
                name = "Column Samples",
                samples = mutableStateListOf(
                    FullyCollapsibleColumnSample,
                    PartiallyCollapsibleColumnSample,
                    AlternatelyCollapsibleColumnSample,
                ),
            ),
            SamplesGalleryGroup(
                name = "Advanced Samples",
                samples = mutableStateListOf(
                    ParallaxCollapsingSample,
                    SnapCollapsingSample,
                    AppBarScrimSample,
                    ManualCollapsingControlsSample,
                    CustomPlacementSample,
                    StackedWithColumnSample,
                ),
            ),
        )
    }
}
