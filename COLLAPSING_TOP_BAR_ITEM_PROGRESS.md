# CollapsingTopBar itemProgress semantics

This note defines how `CollapsingTopBarProgressListener.itemProgress` should behave in `CollapsingTopBar`.

## Contract

`itemProgress` is a `0f..1f` value describing how much of the child's collapsible portion is still visible.

- `1f` means the collapsible portion is fully visible.
- `0f` means the collapsible portion is no longer visible.
- This matches the existing listener direction used by `CollapsingTopBarColumn`: higher means more visible and less collapsed.

## What counts as collapsible portion

The child's collapsible portion is the part of the child outside the collapsed-height baseline area.

The portion of the child that already lies within the top bar's collapsed-height area is treated as non-collapsible baseline content and is excluded from `itemProgress`.

Because of that:

- a child may still be partially visible when `itemProgress == 0f`
- a child whose height is less than or equal to the collapsed height has no collapsible portion, so its `itemProgress` is always `1f`

## Layout-aware measurement

`itemProgress` must be measured from final placed bounds, after layout placement effects are applied:

- alignment
- parallax

Visibility is measured against the current visible top bar viewport.

This means:

- alignment changes when collapse starts affecting the child
- parallax can reduce `itemProgress` by moving the collapsible portion out of view, even before full top bar collapse

## Examples

### Top-aligned child, no parallax

- Top bar height: `200 -> 50`
- Child height: `100`
- Collapsed height: `50`

The child's lower `50dp` is the collapsible portion.
`itemProgress` stays `1f` until top bar height goes below `100dp`, then decreases to `0f` at `50dp`.

### Child height equals collapsed height

- Child height: `50`
- Collapsed height: `50`

The child has no collapsible portion.
`itemProgress` is always `1f`.

### Center-aligned child

If a center-aligned child already overlaps the collapsed-height area in expanded state, that overlapping area is excluded from the metric. Only the remaining area participates in `itemProgress`.

### 100% parallax

If the child's collapsible portion slides upward and leaves the viewport due to parallax, `itemProgress` should decrease accordingly and may reach `0f` before the top bar is fully collapsed.

## Invariants for future implementation

- `itemProgress` must remain within `0f..1f`
- `itemProgress` direction must match `CollapsingTopBarColumn`: larger value means more visible and less collapsed
- baseline collapsed-height content is excluded from the metric
- `align` and `parallax` affect measurement through final placed bounds
- zero collapsible portion returns `1f`
