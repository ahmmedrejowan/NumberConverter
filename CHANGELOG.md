# Changelog

All notable changes to Number Converter will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Mixed-base calculator explanation sheet, matching the converter's step-by-step sheet
- Shared component set across the Converter and Calculator screens
- CI workflow (lint, unit tests, debug APK) and manual release workflow
- Release signing configuration driven by `keystore.properties` or CI environment variables

### Changed
- Dropped the Learn tab; the app is now Convert / Calculate / Settings
- Dynamic colors now default to **off** so the app ships its own violet identity
- Input filtering moved into `NumberBase`, shared by both screens

### Fixed
- Onboarding "Skip" now completes onboarding instead of scrolling to the last page
- Copy and Share now confirm success, and report gracefully when no app can handle the intent

---

## [1.0.0] - Unreleased

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

## Version History

| Version | Release Date | Highlights |
|---------|--------------|------------|
| 1.0.0 | Unreleased | Initial release |

---

[Unreleased]: https://github.com/ahmmedrejowan/NumberConverter/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/ahmmedrejowan/NumberConverter/releases/tag/v1.0.0
