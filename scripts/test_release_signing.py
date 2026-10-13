import importlib.util
import unittest
from pathlib import Path

SOURCE = Path(__file__).resolve().parent / "verify-release-signing.py"
spec = importlib.util.spec_from_file_location("release_signing", SOURCE)
assert spec is not None and spec.loader is not None
guard = importlib.util.module_from_spec(spec)
spec.loader.exec_module(guard)


def sample(release: str) -> str:
    return (
        "android {\n"
        "  signingConfigs { debug { storeFile file('debug.keystore') } }\n"
        "  buildTypes {\n"
        "    debug {\n      signingConfig signingConfigs.debug\n    }\n"
        f"    release {{\n{release}\n      minifyEnabled false\n    }}\n"
        "  }\n"
        "}\n"
    )


class ReleaseSigningTests(unittest.TestCase):
    def test_unsigned_release_with_debug_build_is_safe(self):
        self.assertEqual(guard.verify(sample("// Explicitly unsigned")), [])

    def test_public_debug_key_cannot_sign_release(self):
        self.assertEqual(
            guard.verify(sample("signingConfig signingConfigs.debug")),
            ["release uses an embedded signing configuration"],
        )

    def test_other_release_signer_requires_review(self):
        self.assertEqual(
            guard.verify(sample("signingConfig signingConfigs.release")),
            ["release uses an embedded signing configuration"],
        )

    def test_inherited_debug_signing_is_blocked(self):
        for value in ("initWith debug", "setSigningConfig(signingConfigs.debug)",
                      "signingConfig = signingConfigs.debug",
                      "initWith getByName('debug')"):
            with self.subTest(value=value):
                self.assertEqual(
                    guard.verify(sample(value)),
                    ["release uses an embedded signing configuration"],
                )

    def test_missing_build_contract_fails_closed(self):
        self.assertTrue(guard.verify("android {}"))

    def test_actual_checked_in_source_is_safe(self):
        self.assertEqual(guard.verify(guard.GRADLE.read_text(encoding="utf8")), [])


if __name__ == "__main__":
    unittest.main()
