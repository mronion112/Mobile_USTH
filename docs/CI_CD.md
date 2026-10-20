# CI/CD

Frontend-only GitHub Actions pipelines. No backend jobs exist by design.

## Workflows

| Workflow | Trigger | Jobs |
| --- | --- | --- |
| `ci.yml` | `pull_request` and `push` to `main` | `docs-validate` → `android-build`, `security-scan` |
| `cd.yml` | `workflow_dispatch` and `v*` tags | `build-artifact` |

- `concurrency` cancels superseded `ci` runs and serializes `cd` runs.
- `android-build` runs cheap `docs-validate` first, then matrix
  `[assembleDebug, testDebugUnitTest, lintDebug]`. It skips gracefully
  while `gradlew` is absent (scaffold phase) and builds once the Android
  module lands.
- Gradle cache key: `hashFiles('**/*.gradle*', '**/gradle-wrapper.properties', '**/libs.versions.toml')`.
- `security-scan` runs `gitleaks-action` as non-blocking until the team
  confirms a clean baseline, then remove `continue-on-error`.
- Pinned: `ubuntu-24.04`, `actions/checkout@v4`, `actions/setup-python@v5`,
  `actions/setup-java@v4`, `actions/cache@v4`, `actions/upload-artifact@v4`,
  `gitleaks/gitleaks-action@v2`.

## Excluded scope

- No backend build, test, or deploy jobs.
- No live feed smoke tests; `scripts/ci/check-docs.py` fails CI if any
  markdown references versioned REST paths or the removed API reference files.
- Automated tests use fake RSS fixtures and a fake local store only, never production feeds.

## Secrets

Store signing keys and store passwords in GitHub Actions secrets.
Never commit `local.properties`, keystores, `.env`, or tokens.
Required names are declared by the Android module when it lands;
`cd.yml` reads them only from `${{ secrets.* }}`.

## Branch protection

Enable for `main`: require `docs-validate`, `android-build`, and
`security-scan` status checks; require PR review from FE Lead;
enforce `feature/<screen>-<topic>` branches with one screen per PR
per `docs/TEAM_WORKFLOW.md`.
