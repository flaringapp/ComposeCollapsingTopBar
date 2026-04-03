# Repository Guidelines

## Project Focus
This repo contains a published Compose Multiplatform library for collapsing top bar / toolbar UI. Most work here is incremental library evolution rather than greenfield setup: adding modifiers, extending existing behavior, tightening interactions, and keeping the sample app aligned with library changes.

Treat changes as library work first:
- Preserve existing behavior unless the task explicitly changes it.
- For any behavior change, read the affected implementation end-to-end and review nearby code paths that may now be inconsistent.
- If you touch public API, verify whether docs, samples, and the checked-in ABI snapshot also need updates.

## Project Structure
`ComposeCollapsingTopBar/` is the published library module.
- Public APIs live in `ComposeCollapsingTopBar/src/commonMain/kotlin/com/flaringapp/compose/topbar`.
- Related behavior is grouped under `scaffold`, `nestedscroll`, `nestedcollapse`, `snap`, and `dependent`.
- The ABI snapshot is `ComposeCollapsingTopBar/api/ComposeCollapsingTopBar.klib.api`.

`sample/shared/` is the Compose Multiplatform sample app used to exercise and demonstrate library behavior.
- Entry point: `sample/shared/src/commonMain/kotlin/com/flaringapp/compose/topbar/sample/shared/App.kt`
- Sample registry: `sample/shared/src/commonMain/kotlin/com/flaringapp/compose/topbar/sample/shared/ui/samples/CollapsingTopBarSampleGroups.kt`
- Gallery UI: `sample/shared/src/commonMain/kotlin/com/flaringapp/compose/topbar/sample/shared/ui/samples/gallery`
- Shared sample helpers: `sample/shared/src/commonMain/kotlin/com/flaringapp/compose/topbar/sample/shared/ui/samples/common`

`sample/android/` packages the Android launcher app for manual verification. Documentation assets used by the README live in `docs/assets/`.

## Change Expectations
Before editing:
- Read all directly related files, not just the first obvious entry point.
- Trace how state, modifiers, nested scroll, snap, and scaffold interactions connect if your change touches any of them.
- Check whether sample code already demonstrates the behavior you are changing.

When editing:
- Update existing code paths when needed so old and new behavior remain coherent.
- Keep new APIs and modifiers aligned with existing naming and package structure under `com.flaringapp.compose.topbar`.
- Be explicit about public visibility and return types.

After editing:
- Revisit impacted samples and previews.
- If behavior changed, make sure the sample app still demonstrates the intended interaction clearly.

## Build, Test, and Verification
Use the Gradle wrapper from the repo root:
- `./gradlew ktlintCheck`
- `./gradlew ktlintFormat`
- `./gradlew checkLegacyAbi`
- `./gradlew :sample:android:assembleDebug`

Minimum expectation for most code changes:
- Run `./gradlew :sample:android:assembleDebug ktlintCheck checkLegacyAbi` when feasible.
- For public API changes, pay special attention to `checkLegacyAbi` and the `.klib.api` snapshot.
- For behavior changes, validate through the sample app, especially `sample/shared/` and the Android sample shell.

## Coding Style
- Follow Kotlin defaults with 4-space indentation and trailing commas where formatting expects them.
- Use `UpperCamelCase` for composables and state holders.
- Use `lowerCamelCase` for functions and properties.
- Keep package names under `com.flaringapp.compose.topbar`.
- Run `./gradlew ktlintFormat` before wrapping up when formatting changed.

## Samples And Preview Structure
The preview strategy is sample-driven. Most previews live in `sample/shared/src/commonMain/...` beside the composable they exercise.

High-level structure:
- Each navigable sample is typically an `object` implementing `CollapsingTopBarSample`.
- That object exposes `name` for the gallery card and `Content(onBack)` for in-app navigation.
- The actual reusable UI usually lives in a `*SampleContent(...)` composable in the same file.
- Local `@Preview` functions render that content inside `ComposeCollapsingTopBarTheme`.

Current sample wiring:
- `App.kt` builds the gallery from `CollapsingTopBarSampleGroups`.
- `CollapsingTopBarSampleGroups` is the source of truth for which samples appear in each gallery section.
- Gallery rows render sample names only; the preview itself is not the gallery card.
- Reusable sample pieces and control widgets are split into `common/`, `scaffold/`, `gallery/`, and feature-specific packages.

How to add a new sample preview for a feature:
1. Add or update the feature sample file under the relevant package in `sample/shared/ui/samples`.
2. If it should be navigable from the sample app, expose it as a `CollapsingTopBarSample` object.
3. Put the main demo UI in a reusable `*SampleContent(...)` composable.
4. Add a local `@Preview` that wraps `*SampleContent(...)` in `ComposeCollapsingTopBarTheme`.
5. Register the sample in `CollapsingTopBarSampleGroups` if it belongs in the gallery.
6. If the feature depends on shared demo building blocks, prefer extending existing helpers in `ui/samples/common` or scaffold controls instead of duplicating UI setup.

## Tests
There is no large committed unit-test suite yet. All testing is done manually at this point.
