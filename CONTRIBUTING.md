# Contributing to Number Converter

Hey there! Thanks for wanting to contribute to Number Converter. Whether it's a bug fix, new feature, or just a typo correction - every contribution helps make this app better for everyone.

## Ways to Contribute

### Found a Bug?

1. Search [existing issues](https://github.com/ahmmedrejowan/NumberConverter/issues) first - maybe it's already reported
2. Check [KNOWN_ISSUES.md](KNOWN_ISSUES.md) - it might already be documented
3. If not, open a new issue using the Bug Report template

For a wrong conversion or calculation, please include **the exact input and both bases** so it can be reproduced.

### Have an Idea?

We love hearing new ideas! Share them in [Discussions](https://github.com/ahmmedrejowan/NumberConverter/discussions/categories/ideas) or open a Feature Request issue.

### Want to Code?

Awesome! Here's how:

1. Fork the repo
2. Create a branch: `git checkout -b feature/your-feature`
3. Make your changes
4. Test it works
5. Open a Pull Request

Don't worry about getting everything perfect - we can work through it together in the PR.

## Setting Up Locally

**You'll need:**
- Android Studio (Ladybug or newer)
- JDK 17

**Quick start:**
```bash
git clone https://github.com/ahmmedrejowan/NumberConverter.git
cd NumberConverter
./gradlew assembleDebug
```

## Code Style

We try to keep things consistent:

- **Kotlin** - Follow standard [Kotlin conventions](https://kotlinlang.org/docs/coding-conventions.html)
- **Compose** - Use `remember`, proper state hoisting, keep composables small, `modifier` as the first optional parameter
- **Architecture** - Clean Architecture with MVVM

For commits, we use conventional format like:
- `feat(calculator): add modulo operation`
- `fix(converter): correct fractional rounding for base 8`

But don't stress too much about this - we can always squash and clean up commits later.

## Testing

Conversion and calculation logic is the heart of this app, so **changes to it need tests**.

```bash
./gradlew testDebugUnitTest
```

Covered today: `BaseConverter`, both explanation generators, `ConverterRepositoryImpl`, the converter and calculator use cases, and both ViewModels.

If you're touching maths, add a case that would have failed before your change.

## Project Structure

```
app/src/main/java/com/rejowan/numberconverter/
├── data/
│   ├── converter/       # BaseConverter, per-base converters, explanation generators
│   ├── local/
│   │   ├── database/    # Room database, DAO, entities
│   │   └── datastore/   # PreferencesManager
│   └── repository/      # Repository implementations
├── domain/
│   ├── model/           # NumberBase, Operation, Explanation, HistoryItem
│   ├── repository/      # Repository interfaces
│   └── usecase/         # converter / calculator / history / settings
├── presentation/
│   ├── common/          # Shared components, theme, util
│   ├── converter/       # Converter screen, state, components
│   ├── calculator/      # Calculator screen, state, components
│   ├── settings/        # Settings screen, state, components
│   ├── onboarding/      # First-run onboarding
│   ├── home/            # Bottom-nav host
│   ├── navigation/      # Parent + bottom-nav graphs
│   └── main/            # MainActivity
├── di/                  # Koin DI modules
└── util/                # Utilities
```

## Key Technologies

- **UI:** Jetpack Compose + Material 3
- **Database:** Room
- **Preferences:** DataStore
- **DI:** Koin
- **Async:** Kotlin Coroutines + Flow
- **Precision:** `BigInteger` / `BigDecimal`
- **Testing:** JUnit, MockK, Turbine

## A Note on Shared UI

The Converter and Calculator deliberately share their components
(`presentation/common/components/`) so the two screens can't drift apart
visually. If you're adding something to one of them, check whether it belongs
in the shared set first.

## Questions?

- Need help? Ask in [Discussions Q&A](https://github.com/ahmmedrejowan/NumberConverter/discussions/categories/q-a)
- Found a bug? Open an [Issue](https://github.com/ahmmedrejowan/NumberConverter/issues)
- Have an idea? Share in [Discussions](https://github.com/ahmmedrejowan/NumberConverter/discussions/categories/ideas)

---

Thanks again for contributing!
