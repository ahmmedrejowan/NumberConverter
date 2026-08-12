<div align="center">
  <img src="https://raw.githubusercontent.com/ahmmedrejowan/NumberConverter/master/files/logo.png" alt="Number Converter Logo" width="120" height="120">

<h3>Number System Converter & Calculator for Android</h3>

<p>
    An offline, privacy-focused converter for Binary, Octal, Decimal, and Hexadecimal — built with Jetpack Compose and Material 3. Every conversion and calculation comes with a step-by-step explanation of how the answer was reached.
  </p>

[![Android](https://img.shields.io/badge/Platform-Android-green.svg?style=flat)](https://www.android.com/)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=24)
[![License](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-100%25-purple.svg)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Latest-blue.svg)](https://developer.android.com/jetpack/compose)
[![CI](https://github.com/ahmmedrejowan/NumberConverter/actions/workflows/ci.yml/badge.svg)](https://github.com/ahmmedrejowan/NumberConverter/actions/workflows/ci.yml)

</div>

---

## Features

- **Convert Any Base** — Binary, Octal, Decimal, and Hexadecimal in all 12 directions
- **Fractional Support** — converts values with a fractional part, not just integers
- **Step-by-Step Explanations** — every conversion opens a sheet showing positional notation, the division method, remainders, and the final result
- **Mixed-Base Calculator** — add, subtract, multiply, and divide across *different* bases (e.g. `1010₂ + 17₁₀`), with the result in any base you pick
- **Calculation Breakdowns** — the calculator explains operand conversion, the arithmetic, and the conversion back
- **History & Bookmarks** — every conversion is saved, searchable, and can be starred for later
- **Material 3 Design** — dark mode, optional dynamic colors, and adjustable font size
- **100% Offline** — no permissions, no network, no ads, no tracking, no analytics

---

## Download

![GitHub Release](https://img.shields.io/github/v/release/ahmmedrejowan/NumberConverter)

You can download the latest APK from here

<a href="https://github.com/ahmmedrejowan/NumberConverter/releases/latest">
<img src="https://raw.githubusercontent.com/ahmmedrejowan/NumberConverter/master/files/get.png" width="224px" align="center"/>
</a>

Check out the [releases](https://github.com/ahmmedrejowan/NumberConverter/releases) section for more details.

---

## Screenshots

| Shots | Shots | Shots |
| --- | --- | --- |
| ![Screenshot 1](https://raw.githubusercontent.com/ahmmedrejowan/NumberConverter/master/files/shot1.webp) | ![Screenshot 2](https://raw.githubusercontent.com/ahmmedrejowan/NumberConverter/master/files/shot2.webp) | ![Screenshot 3](https://raw.githubusercontent.com/ahmmedrejowan/NumberConverter/master/files/shot3.webp) |
| ![Screenshot 4](https://raw.githubusercontent.com/ahmmedrejowan/NumberConverter/master/files/shot4.webp) | ![Screenshot 5](https://raw.githubusercontent.com/ahmmedrejowan/NumberConverter/master/files/shot5.webp) | ![Screenshot 6](https://raw.githubusercontent.com/ahmmedrejowan/NumberConverter/master/files/shot6.webp) |
| ![Screenshot 7](https://raw.githubusercontent.com/ahmmedrejowan/NumberConverter/master/files/shot7.webp) | ![Screenshot 8](https://raw.githubusercontent.com/ahmmedrejowan/NumberConverter/master/files/shot8.webp) | ![Screenshot 9](https://raw.githubusercontent.com/ahmmedrejowan/NumberConverter/master/files/shot9.webp) |

---

## Architecture

Number Converter follows **Clean Architecture** principles with the **MVVM** pattern:

```
app/src/main/java/com/rejowan/numberconverter/
├── data/                      # Data layer
│   ├── converter/             # BaseConverter + per-base converters, explanation generators
│   ├── local/
│   │   ├── database/          # Room database, DAO, entities
│   │   └── datastore/         # PreferencesManager
│   └── repository/            # Repository implementations
│
├── domain/                    # Domain layer
│   ├── model/                 # NumberBase, Operation, Explanation, HistoryItem
│   ├── repository/            # Repository interfaces
│   └── usecase/               # converter / calculator / history / settings
│
├── presentation/              # Presentation layer (UI)
│   ├── common/                # Shared components, theme, util
│   ├── converter/             # Converter screen, state, components
│   ├── calculator/            # Calculator screen, state, components
│   ├── settings/              # Settings screen, state, components
│   ├── onboarding/            # First-run onboarding
│   ├── home/                  # Bottom-nav host
│   ├── navigation/            # Parent + bottom-nav graphs
│   └── main/                  # MainActivity
│
├── di/                        # Koin dependency injection
└── util/                      # Utilities
```

### Tech Stack

- **UI Framework**: Jetpack Compose (100% Compose UI)
- **Language**: Kotlin (100%)
- **Architecture**: MVVM + Clean Architecture
- **Dependency Injection**: Koin
- **Database**: Room (KSP)
- **Preferences**: DataStore
- **Async**: Kotlin Coroutines + Flow
- **Navigation**: Jetpack Navigation Compose (type-safe routes via Kotlin Serialization)
- **Design**: Material 3 with dynamic color support
- **Precision**: `BigInteger` / `BigDecimal` for arbitrary-precision conversion
- **Testing**: JUnit, MockK, Turbine

---

## Requirements

- **Minimum SDK**: API 24 (Android 7.0 Nougat)
- **Target SDK**: API 37
- **Compile SDK**: API 37
- **Gradle**: 9.7.0
- **AGP**: 9.2.1
- **Kotlin**: 2.4.10
- **Java**: 17

### Permissions

**None.** The app declares no permissions at all — not even `INTERNET`.

**Note:** This app does not collect or transmit any user data.

---

## Build & Run

To build and run the project, follow these steps:

1. Clone the repository:
   ```bash
   git clone https://github.com/ahmmedrejowan/NumberConverter.git
   ```
2. Open the project in Android Studio.
3. Sync the project with Gradle files.
4. Connect your Android device or start an emulator.
5. Click on the "Run" button in Android Studio to build and run the app.

### Release Builds

Release signing reads from `keystore.properties` in the project root (gitignored),
falling back to the `KEYSTORE_FILE`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, and
`KEY_PASSWORD` environment variables used by CI:

```properties
storeFile=release.keystore
storePassword=...
keyAlias=...
keyPassword=...
```

Without either, `assembleRelease` still builds — it just produces an unsigned APK.

---

## Testing

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

Conversion and calculation logic is covered by unit tests across `BaseConverter`,
the explanation generators, the repositories, the use cases, and both ViewModels.

---

## Contributing

Contributions are welcome! Please read [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

### Quick Start

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## License

```
Copyright (C) 2026 K M Rejowan Ahmmed

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.
```

> **Warning**
> This is a copyleft license. Any derivative work must also be open source under the same license.

See [LICENSE](LICENSE) for the full text.

---

## Community

- [Discussions](https://github.com/ahmmedrejowan/NumberConverter/discussions) - Ask questions, share ideas
- [Issues](https://github.com/ahmmedrejowan/NumberConverter/issues) - Report bugs, request features
- [Releases](https://github.com/ahmmedrejowan/NumberConverter/releases) - Download latest versions

---

## Author

**K M Rejowan Ahmmed**

- GitHub: [@ahmmedrejowan](https://github.com/ahmmedrejowan)
- Email: [kmrejowan@gmail.com](mailto:kmrejowan@gmail.com)

---

## Acknowledgments

- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern Android UI toolkit
- [Material Design 3](https://m3.material.io/) - Design system
- [Room](https://developer.android.com/training/data-storage/room) - Database library
- [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) - Preferences storage
- [Koin](https://insert-koin.io/) - Dependency injection framework
- [MockK](https://mockk.io/) - Mocking library for Kotlin
- [Turbine](https://github.com/cashapp/turbine) - Testing library for Kotlin Flow
- [Ubuntu](https://fonts.google.com/specimen/Ubuntu) - Typeface, via Google Fonts

---

## Changelog

### v1.0.0 - Initial Release

- Binary, Octal, Decimal, and Hexadecimal conversion with fractional support
- Step-by-step explanation sheets for every conversion
- Mixed-base calculator with `+`, `−`, `×`, `÷` and calculation breakdowns
- Conversion history with search and bookmarks
- Material 3 design with dark mode, dynamic colors, and font size options
- Clean Architecture with MVVM, 100% Kotlin, fully offline

See [CHANGELOG.md](CHANGELOG.md) for full version history.

---
