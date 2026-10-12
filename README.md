# JokeApps

JokeApps is a collection of small mobile implementations of the same joke-fetching application, plus opt-in security research tools. Each application is independently buildable; this repository is a **monorepo**, not a shared runtime or a Gradle/JavaScript superproject.

## Workspaces

| Path | Technology | Scope |
| --- | --- | --- |
| [apps/android](apps/android/README.md) | Kotlin, Android, Jetpack Compose, Gradle | Native Android implementation |
| [apps/react-native](apps/react-native/README.md) | React Native, TypeScript, Android and iOS | Cross-platform implementation |
| [security/pentest](security/pentest/README.md) | Frida JavaScript | Authorized local security analysis |

No Git submodules are required.

## Commands

From the repository root:

```sh
make verify       # Verify layout, build entrypoints and absence of Gitlinks
make android-test # Requires the Android SDK/JDK supported by the historical Android project
make rn-test      # Requires Node and Yarn configured for the historical React Native project
```

See [build qualification](docs/qualification.md) for the manually dispatched Android/React Native checks and budget boundaries.

Run Android builds in `apps/android`, and React Native/iOS builds in `apps/react-native`. Dependencies, lockfiles and test suites remain owned by those projects. Migration does not imply that the historical build toolchains work with current SDKs.

## History and licensing

The former repositories were incorporated using non-squashed Git subtree merges. The original commit graphs remain reachable through Git history. See [migration notes](docs/migration.md) for source refs and follow-up validation, and [legacy history](docs/legacy-history.md) for preserved historical branch tips and deletion boundaries.

The native Android project has its own [GPL-3.0 license](apps/android/LICENSE). Do not assume that license governs the React Native or security directories; investigate their respective licensing before redistributing.

Frida scripts are research artifacts for systems you own or are authorized to assess, not part of the application builds.
