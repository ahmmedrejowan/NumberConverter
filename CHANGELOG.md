# Changelog

All notable changes to Number Converter will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

---

## [2.0.0] - 2026-08-12

A complete rewrite. Version 0.1 was a single-screen View-based converter; 2.0.0
is a new app built from scratch on Jetpack Compose with Clean Architecture,
step-by-step explanations, a mixed-base calculator, and searchable history.

Because effectively nothing carries over from 0.1 — architecture, UI, data
storage, and the license all changed — this is a major version bump rather than
a 1.0.0.

### Added
- **Conversion**
  - Binary, Octal, Decimal, and Hexadecimal conversion in all 12 directions
  - Fractional (non-integer) value support
  - Arbitrary precision via `BigInteger` / `BigDecimal`
  - Configurable decimal places for fractional results
  - Live conversion as you type, debounced

- **Step-by-Step Explanations**
  - Bottom sheet breaking down every conversion
  - Positional notation for base → decimal
  - Division-and-remainder method for decimal → base
  - Separate integral and fractional walkthroughs
  - Monospace math blocks, selectable text

- **Calculator**
  - Addition, subtraction, multiplication, and division
  - Operands may each be in a *different* base, with the result in any base
  - Division-by-zero guarded
  - Full calculation breakdown: operand conversion, arithmetic, result conversion

- **History**
  - Every conversion saved automatically
  - Search across inputs, outputs, and base names
  - Bookmark conversions for later
  - Restore a past conversion back into the converter
  - Per-item delete and clear-all

- **Settings**
  - Theme: Light / Dark / System
  - Dynamic (Material You) colors toggle
  - Font size: Small / Medium / Large
  - Decimal places
  - Clear history and reset settings
  - About: changelog, privacy policy, app license, open-source licenses, creator

- **UI/UX**
  - Material 3 design with a violet identity palette
  - Animated bottom navigation with a cutout indicator
  - First-run onboarding with morphing background shapes
  - Edge-to-edge with splash screen

### Changed
- **License changed from Apache 2.0 to GNU GPL v3.0** — the project is now copyleft; derivative works must be released under the same license
- Rebuilt entirely on Jetpack Compose; the 0.1 View-based UI is gone
- Data is now stored in a Room database and DataStore rather than SharedPreferences

### Fixed
- "Auto-save History" now actually controls whether conversions are saved
- "Show Explanations" now actually controls whether step-by-step breakdowns are offered
- History "Clear" now keeps bookmarked conversions, as its confirmation promises
- History no longer accumulates a row per keystroke — identical conversions move to the top instead of duplicating, a value still being typed replaces the prefix it supersedes, and unbookmarked history is capped at 200 entries

### Technical
- **Architecture:** Clean Architecture with MVVM
- **UI:** Jetpack Compose with Material 3
- **Database:** Room (KSP)
- **Preferences:** DataStore
- **DI:** Koin
- **Async:** Kotlin Coroutines and Flow
- **Navigation:** Type-safe routes via Kotlin Serialization
- **Privacy:** No permissions declared, fully offline

---

## [0.1] - 2024-06-25

Initial public release. Single-screen View-based converter for the four number
bases, without explanations, calculator, or history.

---

## Version History

| Version | Release Date | Highlights |
|---------|--------------|------------|
| 2.0.0 | 2026-08-12 | Full Compose rewrite: explanations, calculator, history, GPL-3.0 |
| 0.1 | 2024-06-25 | Initial release |

---

[Unreleased]: https://github.com/ahmmedrejowan/NumberConverter/compare/v2.0.0...HEAD
[2.0.0]: https://github.com/ahmmedrejowan/NumberConverter/releases/tag/v2.0.0
[0.1]: https://github.com/ahmmedrejowan/NumberConverter/releases/tag/0.1
