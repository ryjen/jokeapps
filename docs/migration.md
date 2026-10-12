# Monorepo migration record

## Sources

| Repository | Branch | Imported Git commit | Monorepo prefix |
| --- | --- | --- | --- |
| [ryjen/jokeapps](https://github.com/ryjen/jokeapps) | main | `55e01ddd53cfce8814ab3c56ab88a644ce00ab0c` | root |
| [ryjen/jokeapp-droid](https://github.com/ryjen/jokeapp-droid) | main | `80dc639d78ee65a7b6c8e589493cb3ceeee33491` | `apps/android` |
| [ryjen/jokeapp-rn](https://github.com/ryjen/jokeapp-rn) | main | `5d32a0da70150b95553a650b36b75e426a332714` | `apps/react-native` |
| [ryjen/jokeapp-security](https://github.com/ryjen/jokeapp-security) | main | `09e15493c5bfb5149732bcb7d13f39240332fc74` | `security/pentest` |

The prior `jokeapps` umbrella contained submodules `android` (pinned `0bb5d865...`) and `react-native` (pinned `101b4925...`). The native Android repository in turn had a `security` submodule pinned at `09e15493...`. This migration deliberately takes the current default-branch head of each source rather than the older umbrella submodule pins.

Each source history is imported via `git subtree add` **without** `--squash`, preserving original commits as ancestors. The old root and nested Gitlinks and their `.gitmodules` files are then removed.

## Validation

- `python3 scripts/verify-layout.py` verifies the expected build entrypoints and rejects Gitlinks and tracked `.gitmodules`.
- `make android-test` runs the native Android unit tests using the project's original Gradle wrapper.
- `make rn-test` runs the React Native Jest tests.
- For iOS validation, use Xcode and CocoaPods in `apps/react-native/ios`.
- Frida scripts are deliberately excluded from ordinary application CI.

## Cutover checklist

1. Confirm layout verification and source provenance.
2. Confirm Android and React Native tests in isolated environments with compatible SDKs.
3. Use the root [manual qualification workflow](qualification.md) for opt-in Android and React Native checks. Automatic path-scoped jobs require a separately approved runner policy; do not introduce unbounded hosted builds.
4. Review any repository-level references, branch protection and URLs that still point to the former repositories.
5. Merge the migration branch after verification.
6. Update source repositories with archival notices and archive them **only after** successful cutover. Do not delete source repositories because they retain independent issues, release assets and audit trails.

No claim of runtime build compatibility or completed remote publication is made by this migration record.
