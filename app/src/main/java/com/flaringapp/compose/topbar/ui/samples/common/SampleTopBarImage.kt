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

package com.flaringapp.compose.topbar.ui.samples.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.flaringapp.compose.topbar.R
import com.flaringapp.compose.topbar.ui.theme.ComposeCollapsingTopBarTheme

sealed class SampleTopBarImageDog(
    val imageRes: Int,
) {
    data object GoldenMartian : SampleTopBarImageDog(R.drawable.img_top_bar_dog_1)
    data object LabradorInCar : SampleTopBarImageDog(R.drawable.img_top_bar_dog_2)
    data object Dachshund : SampleTopBarImageDog(R.drawable.img_top_bar_dog_3)
}

@Composable
fun SampleTopBarImage(
    dog: SampleTopBarImageDog,
    modifier: Modifier = Modifier,
    height: Dp = 300.dp,
) {
    Image(
        modifier = modifier.height(height),
        painter = painterResource(dog.imageRes),
        contentDescription = null,
        contentScale = ContentScale.FillHeight,
    )
}

@Preview
@Composable
private fun Preview() {
    ComposeCollapsingTopBarTheme {
        SampleTopBarImage(
            dog = SampleTopBarImageDog.GoldenMartian,
        )
    }
}
