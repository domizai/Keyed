# Changelog

All notable changes to Keyed are documented here.

## [1.1.0] - 2026-10-10

### Added

- `Keyed.VERSION`, the installed library version, e.g. for bug reports: `println(Keyed.VERSION);`

### Fixed

- Bound fields and setters (`Keyed.bind(...)`) lagged one frame behind `value()`. They were applied before the timelines advanced; now the timelines advance first, so a bound field always matches `value()` in the same `draw()`.

- Calling `Keyed.init()` again no longer leaves the previous default timeline
  and sketch registered.

## [1.0.0] - 2026-10-04

First release.
