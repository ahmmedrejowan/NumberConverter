# Known Issues

This document lists known issues and limitations in Number Converter, along with workarounds where available.

## Settings

### "Auto-save History" toggle has no effect
**Issue:** The toggle persists correctly and survives a restart, but nothing reads it — conversions are saved to history regardless of its state.

**Workaround:** Use **Settings → Clear History**, or delete individual entries from the history sheet.

**Status:** Confirmed bug, fix planned

---

### "Show Explanations" toggle has no effect
**Issue:** Same as above — the preference is stored but never consulted. The "Show steps" button appears whenever an explanation is available, on both the Converter and the Calculator.

**Workaround:** Simply don't tap "Show steps"; the sheet is never shown unless you open it.

**Status:** Confirmed bug, fix planned

---

## History

### "Clear" in the history sheet also deletes bookmarks
**Issue:** The confirmation says *"Bookmarked items will be preserved"*, but the action clears the entire history table, bookmarks included. The Settings → Clear History dialog is accurate about this; the history sheet's wording is not.

**Workaround:** Delete unwanted entries individually instead of using Clear.

**Status:** Confirmed bug, fix planned

---

### History fills with partial values while typing
**Issue:** Conversion is debounced and saved on each settled result, so typing `1010` one digit at a time can leave separate entries for `1`, `10`, `101`, and `1010`. There is no de-duplication and no cap on the number of stored rows.

**Workaround:** Paste values rather than typing them, and clear history periodically.

**Status:** Known limitation, redesign planned

---

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

### History and settings are lost if the database schema changes
**Issue:** The database is currently configured for destructive migration, so a schema change in a future version clears saved history and bookmarks.

**Workaround:** None currently — there is no export feature yet.

**Status:** Must be resolved before the first public release

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
| Onboarding "Skip" | Unreleased | Skip scrolled to the last page instead of completing onboarding |
| Silent copy/share | Unreleased | Copy and Share gave no confirmation, and could crash when no app could handle the intent |
| Dynamic colors default | Unreleased | Material You replaced the app's own palette by default on Android 12+ |

---

*Last updated: 2026-08-12*
