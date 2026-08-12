# Security Policy

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 2.x.x   | :white_check_mark: |
| 0.1     | :x:                |

## Reporting a Vulnerability

If you discover a security vulnerability in Number Converter, please report it responsibly:

1. **Do NOT** open a public issue
2. Email the maintainer directly or use GitHub's private vulnerability reporting
3. Include:
   - Description of the vulnerability
   - Steps to reproduce
   - Potential impact
   - Suggested fix (if any)

## Security Measures

Number Converter is a fully offline calculator. Its security posture is mostly a
consequence of how little it does:

- **No permissions requested** — the manifest declares none, including `INTERNET`
- **No network access** of any kind: no update checks, no remote logging, no crash reporting
- **No analytics or tracking**
- **No third-party SDKs** that collect user data
- **No accounts, no sync, no cloud** — nothing leaves the device
- **Local-only storage**: conversion history in a Room database, preferences in DataStore, both private to the app sandbox
- **`allowBackup="false"`** with explicit backup and data-extraction rules
- **ProGuard/R8** minification and resource shrinking in release builds

The only data the app ever handles is the numbers you type and the conversion
history you choose to save. Sharing a result is an explicit, user-initiated
Android share intent.

## Response Timeline

- **Acknowledgment**: Within a week
- **Initial Assessment**: Within 2 weeks
- **Fix Timeline**: Depends on severity and availability
  - Critical: As soon as possible
  - Others: Next release

## Scope

The following are in scope for security reports:

- Remote code execution
- Data leakage from the local history database or preferences
- Privilege escalation
- Denial of service (e.g. crafted input that hangs or crashes the app)
- Exported-component or intent-handling issues

Out of scope:

- Issues requiring physical device access
- Social engineering attacks
- Issues in third-party libraries (report to respective maintainers)
- Incorrect conversion or calculation results — please file those as a **bug report** instead
