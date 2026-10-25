"""Validate frontend scaffold and enforce the no-endpoint policy.

Checks:
1. Every feature folder owns a non-empty Guide.md.
2. PR template exists.
3. No markdown doc references backend REST endpoints or the removed
   API reference files. The app fetches RSS on device and reads the
   local store; REST endpoint documentation is intentionally absent.
4. No obvious committed secrets in scanned text files.
"""

from __future__ import annotations

import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]

GUIDE_DIRS = [
    "00-app-shell",
    "00-shared-components",
    "S01-home-news-feed",
    "S02-article-detail",
    "S03-search-filter",
    "S04-category-feed",
    "S05-bookmarks",
    "S06-rss-sources",
    "S07-add-rss-source",
    "S08-remove-source-confirmation",
    "S09-fetch-status-states",
]

ENDPOINT_MARKERS = (
    "api/v1",
    "ApiEnvelope",
    "api-contract",
    "API_NOTES",
)

SECRET_PATTERNS = (
    re.compile(r"AKIA[0-9A-Z]{16}"),
    re.compile(r"BEGIN [A-Z ]*PRIVATE KEY"),
    re.compile(r"(?i)(api[_-]?key|api[_-]?secret)\s*[:=]\s*['\"][^'\"]+['\"]"),
)

SCAN_SUFFIXES = {".md", ".yml", ".yaml", ".py", ".kts", ".kt", ".gradle"}


def fail(message: str) -> int:
    print(f"FAIL: {message}")
    return 1


def main() -> int:
    errors = 0

    for dirname in GUIDE_DIRS:
        guide = ROOT / "docs" / "guides" / dirname / "Guide.md"
        if not guide.is_file():
            errors += fail(f"missing {guide.relative_to(ROOT)}")
            continue
        text = guide.read_text(encoding="utf-8", errors="replace")
        if len(text.strip()) < 50 or "## " not in text:
            errors += fail(f"empty or heading-less {guide.relative_to(ROOT)}")

    template = ROOT / ".github" / "pull_request_template.md"
    if not template.is_file():
        errors += fail("missing .github/pull_request_template.md")

    for path in sorted(ROOT.rglob("*.md")):
        if ".git/" in path.as_posix():
            continue
        if path.name == ".note.ai.md" or ".note.ai/" in path.as_posix():
            continue
        try:
            text = path.read_text(encoding="utf-8", errors="strict")
        except (OSError, UnicodeDecodeError):
            continue
        for marker in ENDPOINT_MARKERS:
            if marker in text:
                errors += fail(f"{path.relative_to(ROOT)} references {marker!r}")

    for path in sorted(ROOT.rglob("*")):
        if not path.is_file() or path.suffix not in SCAN_SUFFIXES:
            continue
        if ".git/" in path.as_posix():
            continue
        try:
            text = path.read_text(encoding="utf-8", errors="strict")
        except (OSError, UnicodeDecodeError):
            continue
        for pattern in SECRET_PATTERNS:
            if pattern.search(text):
                errors += fail(f"possible secret in {path.relative_to(ROOT)}")
                break

    if errors:
        print(f"{errors} check(s) failed.")
        return 1
    print(f"OK: {len(GUIDE_DIRS)} guides, no endpoint docs, no secrets.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
