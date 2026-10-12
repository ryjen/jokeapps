# Legacy Git history preservation

The working monorepo is on `main`. The [migration history](migration.md) contains the **source default-branch** commit graphs as non-squashed subtree imports.

A separate set of annotated `legacy/` tags preserves **all original branch tips** of the former public native-Android and Frida-security repositories, including unmerged experiments. These tags intentionally point to the **original standalone repository roots**, not to the `apps/android/` or `security/pentest/` folder layout.

| Original repository | Original branch | Archival tag | Pinned source commit |
| --- | --- | --- | --- |
| `ryjen/jokeapp-droid` | `main` | `legacy/jokeapp-droid/main` | `80dc639d78ee` |
| `ryjen/jokeapp-droid` | `devel` | `legacy/jokeapp-droid/devel` | `fa4f08737860` |
| `ryjen/jokeapp-droid` | `feature/dev-refactor` | `legacy/jokeapp-droid/feature/dev-refactor` | `883d2702b9b9` |
| `ryjen/jokeapp-droid` | `feature/view-state-and-facades` | `legacy/jokeapp-droid/feature/view-state-and-facades` | `30cb545316e5` |
| `ryjen/jokeapp-droid` | `feature/xplatform` | `legacy/jokeapp-droid/feature/xplatform` | `6701ef9fb92e` |
| `ryjen/jokeapp-droid` | `redux-abstraction` | `legacy/jokeapp-droid/redux-abstraction` | `30a44cb804cf` |
| `ryjen/jokeapp-droid` | `sslpinned` | `legacy/jokeapp-droid/sslpinned` | `623429c7a03e` |
| `ryjen/jokeapp-security` | `main` | `legacy/jokeapp-security/main` | `ec42be7a7d4d` |
| `ryjen/jokeapp-security` | `tech-stack-file` | `legacy/jokeapp-security/tech-stack-file` | `06adf8f21f39` |

## How to inspect a legacy branch

```sh
git fetch origin 'refs/tags/legacy/*:refs/tags/legacy/*'
git show --stat legacy/jokeapp-droid/sslpinned
git log --oneline legacy/jokeapp-security/tech-stack-file
```

The unmerged Android branches include cross-platform, Redux-abstraction and certificate-pinning experiments; the separate Frida branch adds historical tech-stack metadata. They were **archived, not applied** to the maintained application.

## Deletion and licensing boundaries

Git tags preserve reachable Git objects, not hosting-platform metadata such as issues, pull-request discussions, stars, forks, branch protection, web redirects or release downloads. A private source repository also exists; its non-default branch history has **not** been published as a public tag. Keep private refs private unless explicitly authorized for release.

Original repositories can remain archived as permanent read-only references. Deleting them is a separate irreversible repository-management decision, not part of the Git history import. Before any deletion, confirm the private/public boundary, external links and code-qualification evidence. The canonical follow-up is [issue #2](https://github.com/ryjen/jokeapps/issues/2).
