# ComposeCollapsingTopBar

[![GitHub Release](https://img.shields.io/github/v/release/flaringapp/ComposeCollapsingTopBar?label=Release)](https://github.com/flaringapp/ComposeCollapsingTopBar/releases/latest)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.flaringapp/ComposeCollapsingTopBar/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.flaringapp/ComposeCollapsingTopBar)
[![ktlint](https://img.shields.io/badge/ktlint%20code--style-%E2%9D%A4-FF4081)](https://pinterest.github.io/ktlint/)
[![Licence](https://img.shields.io/github/license/flaringapp/ComposeCollapsingTopBar)](https://github.com/flaringapp/ComposeCollapsingTopBar/blob/main/LICENSE)

If you like this project, :star: star and :loudspeaker: share it!

ComposeCollapsingTopBar is the ultimate Jetpack Compose library for creating versatile collapsing
header UIs. It provides the capability to build custom top bars with automatic height adjustment,
featuring common scroll modes, snapping, and offering plenty of customization options. It's designed
with ease of use and optimization in mind while providing features similar to `CoordinatorLayout`.

![](/docs/assets/cover_collapsing_stack.gif)
&nbsp;&nbsp;
![](/docs/assets/cover_collapsing_column.gif)

## Download

Add the dependency to your target module's `build.gradle.kts` file:

```kotlin
dependencies {
    implementation("io.github.flaringapp:ComposeCollapsingTopBar:1.0.5")
}
```

Ensure you've configured repositories in `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
```

## Usage

You need a scrollable content for the collapsing header to work. For the sake of optimization, this
library does something similar to `CoordinatorLayout`: instead of resizing a content as a top bar
collapses, the content is simply offset downward. That's why you need a `Scaffold`-like wrapper.

```kotlin
CollapsingTopBarScaffold(
    scrollMode = CollapsingTopBarScaffoldScrollMode.enterAlwaysCollapsed(),
    snapBehavior = rememberCollapsingTopBarSnapBehavior(),
    topBar = {
        TopBarImage()
        TopAppBar()
    },
    body = {
        ContentUnderTopBar()
    },
)
```

`CollapsingTopBarScaffold()` is flexible enough to cover a variety of use cases. However, there's
some extra room for customization: you can use `CollapsingTopBar()` as the actual collapsing header,
and implement your own mechanism to control header and content positioning. Feel free to take a
look at the `CollapsingTopBarScaffold()` source code if you decide to do so.

### Scroll modes

|                   Collapse                    |             Collapse and exit              |                    Enter always collapsed                    |
|:---------------------------------------------:|:------------------------------------------:|:------------------------------------------------------------:|
| ![](/docs/assets/collapsing_mode_regular.gif) | ![](/docs/assets/collapsing_mode_exit.gif) | ![](/docs/assets/collapsing_mode_enter_always_collapsed.gif) |

> [!TIP]
> For most scroll modes you can also specify whether or not to expand anywhere in the list.

Use a required parameter `scrollMode` of `CollapsingTopBarScaffold()`:

```kotlin
CollapsingTopBarScaffold(
    // ...
    scrollMode = CollapsingTopBarScaffoldScrollMode.enterAlwaysCollapsed(),
    // ...
)
```

<details>
<summary>Regular collapse</summary>

#### Regular collapse

```kotlin
CollapsingTopBarScaffoldScrollMode.collapse(expandAlways = false)
```

The top bar will collapse to the height of the smallest child. It has an option `expandAlways` to
control whether or not to expand anywhere in the list.
</details>

<details>
<summary>Collapse and exit</summary>

#### Collapse and exit

```kotlin
CollapsingTopBarScaffoldScrollMode.collapseAndExit(expandAlways = false)
```

The top bar will collapse to the height of the smallest child and then completely exit outside its
bounds. It has an option `expandAlways` to control whether or not to expand anywhere in the list.
</details>

<details>
<summary>Enter always collapsed</summary>

#### Enter always collapsed

```kotlin
CollapsingTopBarScaffoldScrollMode.enterAlwaysCollapsed()
```

The top bar will collapse to the height of the smallest child and then completely exit outside its
bounds. Then it'll enter collapsed anywhere in the list and fully expand only at the top.
</details>

### Snapping

![](/docs/assets/snapping.gif)

Use a `snapBehavior` parameter of `CollapsingTopBarScaffold()`:

```kotlin
CollapsingTopBarScaffold(
    // ...
    snapBehavior = rememberCollapsingTopBarSnapBehavior(),
    // ...
)
```

You can optionally specify a `threshold` fraction of collapse progress to control the bound of
snapping direction:

```kotlin
rememberCollapsingTopBarSnapBehavior(threshold = 0.75f)
```

### Customization

When using `CollapsingTopBarScaffold`, you get access to a `CollapsingTopBarScope` in the `topBar`
block. It offers a few predefined Modifiers to customize element placement and more.

|            Parallax            |            Floating            |          Progress           |
|:------------------------------:|:------------------------------:|:---------------------------:|
| ![](/docs/assets/parallax.gif) | ![](/docs/assets/floating.gif) | ![](/docs/assets/scrim.gif) |

See all supported placement customization Modifiers:

<details>
<summary>Parallax</summary>

#### Parallax

```kotlin
Modifier.parallax()
```

Creates a parallax effect by offsetting an element upward by `ratio` as a fraction of the
collapsible height while the top bar collapses.

```kotlin
CollapsingTopBarScaffold(
    scrollMode = CollapsingTopBarScaffoldScrollMode.collapse(expandAlways = false),
    topBar = {
        SampleTopBarImage(
            modifier = Modifier.parallax(0.25f),
        )
        SampleTopAppBar(
            containerColor = Color.Transparent,
        )
    },
    body = {
        SampleContent()
    },
)
```

> In this example the top bar image is collapsing with a 25% parallax effect.

</details>

<details>
<summary>Floating</summary>

#### Floating

```kotlin
Modifier.floating()
```

Excludes an element from the collapsed height calculation, allowing it to float and position itself
in any way you want (floating button, indicator, etc). Should be used only in the presence of
other non-floating elements in the top bar.

```kotlin
CollapsingTopBarScaffold(
    scrollMode = CollapsingTopBarScaffoldScrollMode.collapseAndExit(expandAlways = false),
    topBar = { topBarState ->
        SampleTopBarImage()
        SampleTopAppBar(
            containerColor = Color.Transparent,
        )
        FloatingButton(
            modifier = Modifier.floating(),
            state = topBarState,
        )
    },
    body = {
        SampleContent()
    },
)
```

> In this example there's a floating button with the `floating` modifier implementing its own
> placement logic using `topBarState`.

</details>

<details>
<summary>Progress</summary>

#### Progress

```kotlin
Modifier.progress { totalProgress, itemProgress -> }
```

Allows you to track the current progress of both the top bar and an element to which this modifier
is applied. As a result, you can create your own transformations of any kind.

```kotlin
CollapsingTopBarScaffold(
    scrollMode = CollapsingTopBarScaffoldScrollMode.collapse(expandAlways = false),
    topBar = {
        var topBarColorProgress by remember { mutableFloatStateOf(1f) }
        SampleTopBarImage(
            modifier = Modifier.progress { _, itemProgress ->
                topBarColorProgress =
                    itemProgress.coerceAtMost(SCRIM_START_FRACTION) / SCRIM_START_FRACTION
            },
        )
        SampleTopAppBar(
            containerColor = MaterialTheme.colorScheme.surface.copy(
                alpha = lerp(1f, 0f, topBarColorProgress),
            ),
        )
    },
    body = {
        SampleContent()
    },
)
```

> In this example the top app bar dynamically changes its color as the top bar image collapses.

</details>

<details>
<summary>Nested collapse</summary>

#### Nested collapse

```kotlin
Modifier.nestedCollapse()
```

Defines a connection between an element with a custom nested collapsing mechanism and the top bar.
Should be used when creating custom complex collapsing elements, e.g.,
[CollapsingTopBarColumn](#CollapsingTopBarColumn).

</details>

### CollapsingTopBarColumn

A `Column`-like layout that creates an amazing stacking collapse effect with predefined placement
customization options and the possibility to build custom transformations based on collapse
progress.

|              Fully collapsible               |               Partially collapsible               |                 Custom transformations                 |
|:--------------------------------------------:|:-------------------------------------------------:|:------------------------------------------------------:|
| ![](/docs/assets/collapsing_column_full.gif) | ![](/docs/assets/collapsing_column_partially.gif) | ![](/docs/assets/collapsing_column_moving_element.gif) |

#### Collapse direction

`CollapsingTopBarColumn` supports two collapse directions:

- `CollapsingTopBarColumnDirection.BottomUp` (default) - starts collapsing with the
  bottommost element sliding under the second last and so on;
- `CollapsingTopBarColumnDirection.TopToBottom` - starts collapsing with the
  topmost element sliding up and so on.

|                     BottomUp                      |                   TopToBottom                   |
|:-------------------------------------------------:|:-----------------------------------------------:|
| ![](/docs/assets/collapsing_column_partially.gif) | ![](/docs/assets/collapsing_column_reverse.gif) |

Use a parameter `collapseDirection` of `CollapsingTopBarColumn()`:

```kotlin
CollapsingTopBarColumn(
    state = topBarState,
    collapseDirection = CollapsingTopBarColumnDirection.TopToBottom,
) { /* ... */ }
```

#### Customization

See all supported placement customization Modifiers:

<details>
<summary>Not collapsible</summary>

#### Not collapsible

```kotlin
Modifier.notCollapsible()
```

Excludes an element from the collapsing process, so that it remains always visible and just slides
up and down with the collapsing movement.

```kotlin
CollapsingTopBarScaffold(
    scrollMode = CollapsingTopBarScaffoldScrollMode.collapse(expandAlways = false),
    topBar = { topBarState ->
        CollapsingTopBarColumn(topBarState) {
            SampleTopBarBanner()
            SampleTopAppBar(
                modifier = Modifier.notCollapsible(),
            )
            SampleVerticalFadingEdge()
            SampleFilterChips()
        }
    },
    body = {
        SampleContent()
    },
)
```

> In this example the top app bar is not collapsible, but every other element is.

</details>

<details>
<summary>Pin when collapsed</summary>

#### Pin when collapsed

```kotlin
Modifier.pinWhenCollapsed()
```

Unlike the default placement behavior that stops sliding an element when it is fully collapsed,
this modifier makes the element continue its movement (pin). Useful if you want an element to
slide all the way up under other transparent elements.

```kotlin
CollapsingTopBarScaffold(
    modifier = modifier,
    scrollMode = CollapsingTopBarScaffoldScrollMode.collapse(expandAlways = false),
    topBar = { topBarState ->
        CollapsingTopBarColumn(
            state = topBarState,
            collapseDirection = CollapsingTopBarColumnDirection.TopToBottom,
        ) {
            SampleTopAppBar(
                modifier = Modifier.notCollapsible(),
            )
            InfoBlock(
                modifier = Modifier.pinWhenCollapsed(),
            )
            SearchBar(
                modifier = Modifier.zIndex(1f),
            )
        }
    },
    body = {
        SampleContent()
    },
)
```

> In this example the `InfoBlock` is pinned when collapsed, and it'll slide under the top app bar
> out of the screen bounds.

</details>

<details>
<summary>Clip to collapse</summary>

#### Clip to collapse

```kotlin
Modifier.clipToCollapse()
```

Clips an element to its bounds as it collapses, so that it's not visible under the following
transparent element.

```kotlin
CollapsingTopBarScaffold(
    scrollMode = CollapsingTopBarScaffoldScrollMode.collapse(expandAlways = true),
    topBar = { topBarState ->
        CollapsingTopBarColumn(
            state = topBarState,
        ) {
            SampleFilterChips(
                modifier = Modifier.clipToCollapse(),
            )
            SampleTopAppBar(
                modifier = Modifier.notCollapsible(),
                containerColor = Color.Transparent,
            )
            SampleFilterChips(
                modifier = Modifier.clipToCollapse(),
            )
        }
    },
    body = {
        SampleContent()
    },
)
```

> In this example all elements but the top app bar clip themselves to collapse bounds.

</details>

<details>
<summary>Column Progress</summary>

#### Column Progress

```kotlin
Modifier.columnProgress()
```

Allows you to track the current progress of both the top bar column and an element to which this
modifier is applied. As a result, you can create your own transformations of any kind.

```kotlin
CollapsingTopBarScaffold(
    scrollMode = CollapsingTopBarScaffoldScrollMode.collapse(expandAlways = false),
    topBar = { topBarState ->
        CollapsingTopBarColumn(topBarState) {
            SampleTopAppBar(
                modifier = Modifier.notCollapsible(),
                title = "Column Moving Element",
                onBack = onBack,
            )

            var textCollapseProgress by remember {
                mutableFloatStateOf(1f)
            }
            Text(
                modifier = Modifier
                    .columnProgress { _, itemProgress -> textCollapseProgress = itemProgress }
                    .graphicsLayer {
                        alpha = textCollapseProgress
                    },
                text = "Collapsible element",
            )
        }
    },
    body = {
        SampleContent()
    },
)
```

> In this example the `Text` element keeps track of its collapse progress and fades out on collapse.

</details>

### React to state changes

You can easily access `CollapsingTopBarScaffoldState` and its nested states to observe state
changes. For convenience, you can also access `CollapsingTopBarState` from within the `topBar`
block of `CollapsingTopBarScaffold()`. Now just use your imagination to figure out what beautiful
effects to create. :wink:

<details>
<summary>Example</summary>

```kotlin
val state = rememberCollapsingTopBarScaffoldState()
val topBarShadowElevation by animateDpAsState(
    label = "ShadowAnimation",
    targetValue = if (state.topBarState.isCollapsed) 12.dp else 0.dp,
)
CollapsingTopBarScaffold(
    state = state,
    scrollMode = CollapsingTopBarScaffoldScrollMode.collapse(expandAlways = false),
    topBarModifier = Modifier.graphicsLayer {
        shadowElevation = topBarShadowElevation.toPx()
    },
    topBar = {
        SampleTopBarImage()
        SampleTopAppBar()
    },
    body = {
        SampleContent()
    },
)
```

> In this example the top bar drops a shadow that appears with animation as soon as the top bar is
> collapsed.

</details>

#### Working with states

<details>
<summary>CollapsingTopBarScaffoldState</summary>

#### CollapsingTopBarScaffoldState

This is a state holder for top bar layout data that also allows some manual control.

You can easily access the current top bar state with `state.isExpanded` and `state.isCollapsed`.
Or you can programmatically toggle one with `state.expand()` and `state.collapse()` inside
`LaunchedEffect` or a custom coroutine.

If you need more detailed layout data, refer to:

- `topBarState` for collapsing progress;
- `exitState` for exiting progress;
    - note that it's empty unless you use a *scroll mode that supports exiting*.

</details>

<details>
<summary>CollapsingTopBarState</summary>

#### CollapsingTopBarState

Contains information about the collapsing state up until top bar starts exiting (if chosen scroll
mode supports that). Offers state data similar to scaffold: `state.isExpanded` and
`state.isCollapsed`, as well as manual controls: `state.expand()` and `state.collapse()`.

This state exposes comprehensive measurement data via `layoutInfo`, such as collapse progress
`layoutInfo.collapseProgress`, collapsed height `layoutInfo.collapsedHeight` etc.

</details>

<details>
<summary>CollapsingTopBarExitState</summary>

#### CollapsingTopBarExitState

> This state only makes sense if you use a scroll mode that supports exiting: `collapseAndExit()`,
> `enterAlwaysCollapsed()`.

Contains information about the top bar exiting state up from when top bar is collapsed. Offers state
data: `state.isFullyEntered` and `state.isFullyExited`, as well as manual controls:
`state.expand()` and `state.collapse()`.

This state exposes current exit offset via `exitHeight`.

</details>

## More examples

Feel free to install a [demo app](/app) and try yourself even more samples!

## Contributing

Please contribute! I will gladly review any pull requests.

## License

ComposeCollapsingTopBar is distributed under the terms of the Apache License (Version 2.0).
See the [license](LICENSE) for more information.
