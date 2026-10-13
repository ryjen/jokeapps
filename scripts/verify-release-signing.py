#!/usr/bin/env python3
"""Fail closed if the public React Native debug key can sign release builds."""

from pathlib import Path
import re
import sys

ROOT = Path(__file__).resolve().parents[1]
GRADLE = ROOT / "apps" / "react-native" / "android" / "app" / "build.gradle"


def block(source: str, name: str) -> str | None:
    match = re.search(r"(?m)^\s*" + re.escape(name) + r"\s*\{", source)
    if match is None:
        return None
    depth = 1
    start = match.end()
    for i in range(start, len(source)):
        if source[i] == "{":
            depth += 1
        elif source[i] == "}":
            depth -= 1
            if depth == 0:
                return source[start:i]
    return None


def verify(source: str) -> list[str]:
    errors = []
    android = block(source, "android")
    builds = block(android, "buildTypes") if android is not None else None
    release = block(builds, "release") if builds is not None else None
    debug = block(builds, "debug") if builds is not None else None
    if release is None or debug is None:
        return ["missing Android debug/release signing contract"]
    # A release package must not be signed with ANY hardcoded config in this
    # public example. A future private distribution contract needs review.
    if re.search(r"\b(?:signingConfig|setSigningConfig|initWith)\b", release):
        errors.append("release uses an embedded signing configuration")
    if not re.search(r"(?m)^\s*signingConfig\s+signingConfigs\.debug\s*$", debug):
        errors.append("debug build configuration changed")
    return errors


def main() -> int:
    try:
        text = GRADLE.read_text(encoding="utf8")
    except OSError:
        print("release-signing: Android example unavailable", file=sys.stderr)
        return 2
    errors = verify(text)
    if errors:
        for message in errors:
            print("release-signing: " + message, file=sys.stderr)
        return 1
    print("release-signing: debug only; release remains unsigned")
    return 0


if __name__ == "__main__":
    sys.exit(main())
