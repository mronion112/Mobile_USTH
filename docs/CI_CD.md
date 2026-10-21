# CI/CD

Frontend-only GitHub Actions pipelines. No backend jobs exist by design.

## Workflows

| Workflow | Trigger | Jobs |
| --- | --- | --- |
| `ci.yml` | `pull_request` and `push` to `main` | `docs-validate` → `android-build`, `security-scan` |
| `cd.yml` | `v*` tags and manual dispatch | `build-artifact` |

- `ci` watches `main` only. Feature branches are validated through the pull
  request that targets `main`.
- `concurrency` cancels superseded `ci` runs and serializes `cd` runs.
- `permissions` are read-only except pull-request metadata for gitleaks.
- `docs-validate` runs `scripts/ci/check-docs.py` first; it fails on missing
  guides, removed API reference names, versioned REST paths, or obvious
  secrets, so cheap checks gate the Gradle work.
- `android-build` verifies the Gradle wrapper, then runs the matrix
  `assemble` (`assembleDebug`) and `verify` (`testDebugUnitTest lintDebug`)
  in parallel. Reports upload only on failure.
- Both workflows skip the Gradle work while `gradlew` is absent, so they stay
  green until the app module reaches `main`.
- `security-scan` runs `gitleaks-action` as non-blocking until the team
  confirms a clean baseline, then remove `continue-on-error`.

## Android toolchain

| Item | Value | Source |
| --- | --- | --- |
| JDK | Temurin 21 | `app/build.gradle.kts` targets Java 17; Gradle 9 needs 17+ |
| Gradle | Wrapper `9.7.1` | `gradle/wrapper/gradle-wrapper.properties` |
| AGP | `9.2.1` | `build.gradle.kts` |
| Kotlin | `2.2.10` | `build.gradle.kts` |
| compileSdk / targetSdk | `36` | `app/build.gradle.kts` |
| minSdk | `24` | `app/build.gradle.kts` |
| Installed SDK packages | `platform-tools` via `setup-android`; `platforms;android-36` and `build-tools;36.0.0` via `sdkmanager` | `ci.yml`, `cd.yml` |

Update this table and both workflows together when AGP, Gradle, or
`compileSdk` changes.

`setup-android` receives `packages: platform-tools` so it does not try to
install the retired `tools` package, which fails on cmdline-tools 16.
Licenses are accepted before the explicit SDK 36 install.

## Pinned actions

`ubuntu-24.04`, `actions/checkout@v5`, `actions/setup-python@v6`,
`actions/setup-java@v5`, `gradle/actions/setup-gradle@v5`,
`gradle/actions/wrapper-validation@v5`, `android-actions/setup-android@v4`,
`actions/upload-artifact@v6`, `gitleaks/gitleaks-action@v3`.

Every action above runs on the Node 24 runtime. Node 20 targets are
deprecated on GitHub-hosted runners, so keep this list on majors that
declare `using: node24`.

## Excluded scope

- No backend build, test, or deploy jobs.
- No live feed smoke tests; `scripts/ci/check-docs.py` fails CI if any
  markdown references versioned REST paths or the removed API reference files.
- Automated tests use fake RSS fixtures and a fake local store only, never
  production feeds.

## Secrets

Store signing keys and store passwords in GitHub Actions secrets.
Never commit `local.properties`, keystores, `.env`, or tokens.
`cd.yml` reads secrets only from `${{ secrets.* }}`. Release builds are
currently unsigned because `app/build.gradle.kts` defines no signing
config; add it with secret-backed `storeFile`, `storePassword`,
`keyAlias`, and `keyPassword` before publishing.

## Branch protection

Enable for `main`: require `docs-validate`, `android-build`, and
`security-scan` status checks; require PR review from FE Lead;
enforce `feature/<screen>-<topic>` branches with one screen per PR
per `docs/TEAM_WORKFLOW.md`.
