# B08 — Quality, integration and CI

**Owner:** Nguyễn Quang Vũ Hoàng; all feature owners contribute tests.

## Adopted workflow from Web_USTH

- Keep unit tests network-free; integration tests use an isolated database and mock RSS HTTP server.
- Separate CI checks: build/unit test, integration/contract test, static quality, and optional Android build/lint.
- Use fixtures/factories to create only the delta needed by each test. Demo data is not the only test fixture.
- Protect infrastructure/config files; PR must explain changes to CI, DB migration or API contract.

## Required check matrix

| Area | Minimum coverage |
| --- | --- |
| B01 | clean migration + constraints |
| B03 | RSS/Atom, timeout, SSRF, XXE, duplicate |
| B04/B07 | add/remove/status/one-source-failure |
| B05/B06 | pagination, filter, search, bookmark |
| FE | S01–S09 loading/empty/error + key tap paths |

## PR template expectations

- Link feature folder/issue.
- State API/schema impact and how it was tested.
- Attach mobile screenshot for UI changes.
- Update relevant `Guide.md` and `docs/api-contract.md` if contract changes.

## Done when

- [ ] A new clone can follow one documented setup/test path.
- [ ] CI blocks failed backend tests and contract-breaking changes.
- [ ] Final demo checklist covers every S01–S09 and automatic fetching.
