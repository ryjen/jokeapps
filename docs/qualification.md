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


## Verified hosted mobile qualification — October 11, 2026 (Pacific)

[GitHub Actions run #38207261130](https://github.com/ryjen/jokeapps/actions/runs/38207261130) completed with **success** on an isolated `verify/mobile-qualification-20261012` branch, at `795a7c6ce17ac26626ff44ab4d44e956a39b3f67`. This branch was based on canonical `main` commit `a8bf9f92174f01b11ceaf911d4c4539c4594e72d`; its only change was the one-shot workflow. Both jobs ran on GitHub-hosted Ubuntu with full-SHA-pinned setup actions, a read-only token, and 20/25-minute job caps. No automatic workflow was added to `main`.

- **React Native:** Node 24, Yarn 1.22.22; frozen installation with dependency lifecycle scripts disabled **passed**. `corepack yarn test --ci --runInBand`: **2 suites, 2 tests, 1 snapshot passed**. `corepack yarn compile` and `corepack yarn lint`: **passed**.
- **Android APK packaging:** Java 17, SDK 31 and historical Gradle 7.4 wrapper; `./gradlew --no-daemon --console=plain :app:assembleDebug :app:assembleRelease --stacktrace`: **passed** (`BUILD SUCCESSFUL in 4m 5s`).
- APK presence, non-empty size, ZIP integrity and embedded `AndroidManifest.xml` were checked for both variants. Checksums printed by the runner:
  - `app-debug.apk`: **10,390,867 bytes**, SHA-256 `7ab4c24898b220659977ce2e6f4eff5087276f092e0c9796edbca37a804f904c`
  - `app-release-unsigned.apk`: **2,627,566 bytes**, SHA-256 `00042d9b7f4a4f10e4c94030d3fbe82699db9973afc7a67f652dfb2f15d90088`

**Gates remaining at the time of run #38207261130:** Android instrumentation was not yet covered (it was subsequently qualified below); no production release signing, signing-key custody, Play publishing, iOS Xcode build, or RN native Android/iOS packaging was validated. These generated APKs were ephemeral runner outputs, **not** uploaded release artifacts. The Node 20 deprecation warning for pinned setup actions persists and can be resolved separately.

## Verified Android instrumentation — October 12, 2026 (Pacific)

[GitHub Actions run #38225992867](https://github.com/ryjen/jokeapps/actions/runs/38225992867) completed **successfully** on exact source commit `d65ec7e287bdbea117224725140a8041af0ee6e8` (isolated `fix/android-instrumentation-target-20261011`). The only additional file at this verification commit, relative to the proposed source merge, was a **branch/path-scoped one-shot workflow**. It is removed from the merge candidate; the Android application and test sources are unchanged by that removal.

- GitHub-hosted Ubuntu, **Java 17**, Android **API 31 Google APIs x86_64 emulator**, Linux KVM, historical Gradle **7.4**. One-shot job limited to **30 minutes**, read-only GitHub token, SHA-pinned actions, no persistent push/PR workflow enabled on `main`.
- `(cd apps/android && ./gradlew --no-daemon --console=plain :app:connectedDebugAndroidTest --stacktrace)`: **PASS**, `BUILD SUCCESSFUL in 4m 27s`.
- Actual JUnit XML was parsed and gate-checked: **19 instrumented tests, 0 failures, 0 errors, 0 skipped**; an empty or partially skipped test suite would not qualify. The instrumentation target is now the application itself, rather than a test-only Android library process.
- The test harness uses an explicit test-only `Application`/Koin graph, debug-only Compose test host, and Android in-memory SQLite fixture. The SQLDelight `UPDATE`/`DELETE` queries use Android-compatible single-row subqueries instead of unsupported `... LIMIT 1` mutations.
- A prior [run #38225179416](https://github.com/ryjen/jokeapps/actions/runs/38225179416) ran all 19 tests with one failure caused by duplicate fixture keys in the shared in-memory database; the final qualification used separate keys. This is a positive run on the fixed source, not evidence of independent rerun consistency.
- Follow-up [#11](https://github.com/ryjen/jokeapps/issues/11) tracks production **external-key uniqueness and atomic favorite-cache upserts**, which need a migration review and concurrency/regression coverage.

**Not covered:** production-signed release or key custody, Play distribution, iOS Xcode build, RN native app packaging, and repeatability across multiple emulator environments. The earlier debug/unsigned-release APK build was qualified on older source and is separate evidence.

## Verified Android data-correctness regression qualification — October 12, 2026 (Pacific)

[Hosted Actions run #38315903870](https://github.com/ryjen/jokeapps/actions/runs/38315903870) completed **success** on source commit `f88fb62eb748f9550d1960a0e8ef789d00b73237`, branch `fix/android-joke-key-and-cache-fallback-20261012`. Its extra one-shot workflow was scoped to that branch and workflow path, with read-only permissions and a 40-minute job limit. This workflow is **removed** before merging; all Android application and regression-test source is unchanged by that cleanup.

- **API 31 emulator instrumentation:** `:app:connectedDebugAndroidTest` **PASS**, JUnit XML **24 tests, 0 failures, 0 errors, 0 skipped**, Gradle `BUILD SUCCESSFUL in 3m 53s`. New regressions cover numeric external-key/internal-row-ID collisions; normal cache miss, cached hit, random remote null fallback, and cancellation.
- **Android Gradle unit tests, lint and packaging:** `test lint :app:assembleDebug :app:assembleRelease` **PASS**, `BUILD SUCCESSFUL in 1m 54s`, under Java 17 and Android SDK 31. Debug and unsigned release variants assembled; this is **not** a production-signed release.
- **Scope:** Removes the `OR id=:key` condition from SQLDelight external-key queries; correctly handles normal cache-miss and null-remote fallback; propagates cancellation in `JokeRepository` and `JokeService`.
- **Not covered:** Existing-data duplicate-key cleanup, unique-index database migrations, concurrent atomic favorite upserts ([#11](https://github.com/ryjen/jokeapps/issues/11)), production signing, or iOS builds. The success is a single verified emulator environment, not repeated-run consistency.

## Verified Android database migration and atomic favorites — October 12, 2026 (Pacific)

[Hosted Actions run #38317013331](https://github.com/ryjen/jokeapps/actions/runs/38317013331) completed **success** on source commit `9b1b2cc559bcd3663f0f5fa16d0c305ad7aa3161`. The only extra file at this verification commit was the branch/path-scoped one-shot workflow, which is **removed** from the merge candidate; the Android application and test sources do not change in cleanup.

- **Offline SQLite 1→2 migration preflight:** PASS; duplicates collapsed, newest row's content/date retained, any historical favorite state preserved, and the new unique external-key index rejects duplicates.
- **Android API 31 device migration and concurrency instrumentation:** `:app:connectedDebugAndroidTest` PASS, **26 tests, 0 failures, 0 errors, 0 skipped**, Gradle `BUILD SUCCESSFUL in 2m 56s`. The tests construct a real version-1 database and reopen through the upgraded SQLDelight schema, validate the unique index and consolidated favorites, and exercise 12 concurrent favorite writes and idempotent cache refresh.
- **Gradle unit tests, lint and APK builds:** `test lint :app:assembleDebug :app:assembleRelease` PASS, `BUILD SUCCESSFUL in 1m 26s`, Java 17 / Android SDK 31. The release APK is **unsigned**.
- **Migration semantics:** within a duplicate external-key group, retain the **highest integer row ID**, its content and created timestamp, with `isFavorite=true` if any duplicate was favorited. All other duplicate rows are removed before making `joke.key` unique. This is an intentional database data consolidation and must be accepted when updating existing installations. The test verifies one old-db fixture; it is not proof of every legacy dataset or rollback safety.
- **Atomic writes:** favorite creation inserts or ignores by the unique external key, then updates the favorite bit in one database transaction. Repeated stale cache inserts cannot reset an existing favorite.
- **Not covered:** signed releases, production rollout/rollback procedures, alternative Android database versions, iOS native builds, or repeated independent emulator runs. No permanent automatic hosted CI was enabled.

## Acceptance

Capture tool versions, commands, runner identity, commit SHA and exit statuses. All three original source repositories were verified private and archived on October 12, 2026. Keep them as read-only historical references; deletion is a separate decision requiring independent private backups, repository metadata/link impact review, and explicit authorization.
