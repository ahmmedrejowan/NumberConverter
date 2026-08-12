# Known Issues

This document lists known issues and limitations in Number Converter, along with workarounds where available.

## Calculator

### Negative results cannot be re-entered
**Issue:** Subtraction can produce a negative result (e.g. `5 − 10 = -5`), but the input fields reject the minus sign, so a negative result cannot be pasted back into the Converter or Calculator as an input.

**Workaround:** Drop the sign and work with the magnitude.

**Status:** Negative-number input is not currently supported

---

### Division results are rounded
**Issue:** Division is computed to a fixed number of decimal places, so a non-terminating quotient (e.g. `1 ÷ 3`) is rounded rather than represented exactly.

**Workaround:** Increase **Settings → Decimal Places** for more precision.

**Status:** By design (arbitrary precision still requires a stopping point)

---

## Conversion

### Repeating fractions are truncated
**Issue:** Fractions that do not terminate in the target base (e.g. `0.1` decimal → binary) are cut off at the configured decimal-places limit.

**Workaround:** Increase **Settings → Decimal Places**.

**Status:** Mathematically unavoidable

---

### Very long inputs may feel slow
**Issue:** Conversion uses arbitrary-precision arithmetic. Inputs of several thousand digits take noticeably longer, and the explanation sheet becomes very long.

**Workaround:** The input field caps display at four lines; conversion still completes.

**Status:** Normal behavior for arbitrary precision

---

## Data

### A schema change could still clear history
**Issue:** The database falls back to destructive migration, so a future schema change without an accompanying `Migration` would clear saved history and bookmarks.

**Workaround:** None currently — there is no export feature yet.

**Status:** Partly addressed in 2.0.0. Room schemas are now exported and committed (`app/schemas`), so real migrations can be written and tested from 2.0.0 onward. The destructive fallback remains as a last resort until an explicit migration is added.

---

## Localization & Accessibility

### English only
**Issue:** Most UI strings are hardcoded in English rather than extracted to resources, so the app cannot currently be translated.

**Workaround:** None.

**Status:** Extraction planned

---

### Missing themed launcher icon
**Issue:** The adaptive icon has no monochrome layer, so it does not tint with themed icons on Android 13+.

**Workaround:** None.

**Status:** Planned

---

## Reporting New Issues

If you encounter an issue not listed here, please report it:

1. **Check existing issues:** [GitHub Issues](https://github.com/ahmmedrejowan/NumberConverter/issues)
2. **Create a new issue** with:
   - Device model and Android version
   - App version
   - Steps to reproduce
   - Expected vs actual behavior
   - **For wrong results:** the exact input and both bases
   - Screenshots if applicable

---

## Fixed Issues

Issues that have been fixed in recent releases:

| Issue | Fixed In | Description |
|-------|----------|-------------|
| Onboarding "Skip" | 2.0.0 | Skip scrolled to the last page instead of completing onboarding |
| Silent copy/share | 2.0.0 | Copy and Share gave no confirmation, and could crash when no app could handle the intent |
| Dynamic colors default | 2.0.0 | Material You replaced the app's own palette by default on Android 12+ |
| "Auto-save History" toggle | 2.0.0 | Toggle was stored but never read — conversions saved regardless |
| "Show Explanations" toggle | 2.0.0 | Toggle was stored but never read — "Show steps" always appeared |
| History "Clear" deleted bookmarks | 2.0.0 | Clear wiped the whole table despite promising bookmarks would survive |
| History filled with partial values | 2.0.0 | Typing a value left a row per prefix; identical and superseded entries are now collapsed, and history is capped |

---

*Last updated: 2026-08-12*
