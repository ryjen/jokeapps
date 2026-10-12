#!/usr/bin/env python3
"""Verify the JokeApps monorepo's tracked repository boundaries."""

from pathlib import Path
import subprocess
import sys

ROOT = Path(__file__).resolve().parent.parent
EXPECTED_FILES = (
    "README.md",
    "apps/android/settings.gradle.kts",
    "apps/android/gradlew",
    "apps/android/app/build.gradle.kts",
    "apps/react-native/package.json",
    "apps/react-native/yarn.lock",
    "apps/react-native/ios/Podfile",
    "apps/react-native/android/gradlew",
    "security/pentest/frida/jokeapp-network-debug.js",
    "security/pentest/frida/jokeapp-database-release.js",
)


def main() -> int:
    errors = []
    for relative in EXPECTED_FILES:
        if not (ROOT / relative).is_file():
            errors.append(f"missing required file: {relative}")
    proc = subprocess.run(
        ["git", "ls-files", "--stage", "-z"],
        cwd=ROOT,
        capture_output=True,
        check=True,
    )
    for record in proc.stdout.split(b"\0"):
        if not record:
            continue
        metadata, raw_path = record.split(b"\t", 1)
        mode = metadata.split(b" ", 1)[0]
        path = raw_path.decode("utf-8", "replace")
        if mode == b"160000":
            errors.append(f"submodule/gitlink remains: {path}")
        if Path(path).name == ".gitmodules":
            errors.append(f"tracked .gitmodules remains: {path}")
    if errors:
        for error in errors:
            print(f"FAIL: {error}", file=sys.stderr)
        return 1
    print("OK: workspace entrypoints present; no tracked Gitlinks or .gitmodules")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
