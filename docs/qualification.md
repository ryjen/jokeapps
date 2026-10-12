# Build qualification

The JokeApps workspaces deliberately retain independent Android and React Native toolchains. Historical sources were moved unchanged; passing structural verification does **not** establish they compile with modern dependencies or SDKs.

## CI execution

The root [manual qualification workflow](../.github/workflows/qualify.yml) accepts `layout` (default), `android`, `react-native`, or `all`. Only selected builds execute; layout validation always executes.

- GitHub Actions: select **Mobile qualification (manual)**, **Run workflow**, and the target. The jobs are **not** triggered on push or pull requests.
- Uses hosted Linux runners only when explicitly dispatched. Android is capped at 25 minutes, React Native at 20 minutes, and the layout job at 5 minutes. Real billed minutes depend on the run.
- No mobile emulator or macOS runner is scheduled.
- CI permissions are read-only, checkout credentials are not persisted, and actions are pinned by commit SHA.
- RN dependencies use a frozen Yarn lockfile and lifecycle scripts are disabled during dependency installation.
- This workflow is intentionally not a branch-protection required check. Registering jokeapps with Dubnium, GitLab, or a local runner requires approved admission policy and per-path workflow design before enabling automatic CI.

## Local qualification

Use the matching historical or confirmed-compatible toolchains, rather than silently upgrading application source:

```sh
python3 scripts/verify-layout.py
(cd apps/android && ./gradlew --no-daemon test && ./gradlew --no-daemon lint)
(cd apps/react-native && yarn install --frozen-lockfile --ignore-scripts && yarn test --ci --runInBand && yarn compile && yarn lint)
```

The Android project declares Gradle 7.4, AGP 7.3.1, Kotlin 1.6.10 and SDK 31. React Native uses 0.72.5 with Yarn Classic lockfile. CI probes Java 17 and Node 24; if compatibility breaks, report the failure distinctly from source regressions. Instrumented Android and iOS/Xcode tests are separate, opt-in gates.


## Verified Android qualification — October 11, 2026 (Pacific)

- [GitHub Actions run #38206708347](https://github.com/ryjen/jokeapps/actions/runs/38206708347), **completed successfully**; run commit `4f902c0cf42d31e3d462be55986d8e521ba95205` on isolated `verify/android-20261011`. Its only added source-tree file was the one-shot workflow; the Android application sources match `main` at `bacbffff1bde98a08b5ba2f390dd50cc29d47b57`.
- GitHub-hosted Ubuntu runner, **JDK 17**, Android **SDK 31**, Gradle wrapper **7.4**. The one-shot workflow was limited to that verification branch and capped at 25 minutes; it is **not** enabled on ordinary pushes or pull requests.
- `(cd apps/android && ./gradlew --no-daemon --console=plain test --stacktrace)`: **PASS**, Gradle `BUILD SUCCESSFUL in 1m 59s`. Nonempty debug/release unit test tasks ran in `data`, `domain`, and `meta`; several other module unit-test tasks reported `NO-SOURCE`.
- `(cd apps/android && ./gradlew --no-daemon --console=plain lint --stacktrace)`: **PASS**, Gradle `BUILD SUCCESSFUL in 32s`; debug lint reports generated for application and UI modules.
- **Not covered:** instrumentation/emulator, installable APK and signed release build, iOS, or React Native parity in GitHub-hosted Actions. Passing lint does not mean zero informational warnings.
- Recorded toolchain warning: the SHA-pinned checkout/setup-java v4 actions are Node 20-based and the runner forces Node 24. Upgrading action majors can be reviewed separately without changing these verified source results.

## Acceptance

Capture tool versions, commands, runner identity, commit SHA and exit statuses. Do not archive the remaining original source repositories until both workspaces have repeatable green checks or an explicit historical/archival exception is approved.
