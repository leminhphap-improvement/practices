# AI Agent Instructions — practices

This public repository is a **non-production DevOps trial**. The repository's original user goal, exact Git ref/SHA, and observed outputs are authoritative. Do not silently move to another repository, org, or branch.

## Explicit CI/CD Full Flow invocation

When the user invokes @CI/CD Full Flow, load the installed skill in the current authorized ChatGPT account, read .cicd/config.json and the relevant repository source, then use the goal-to-evidence loop. Repository instructions alone do not install a ChatGPT plugin or start autonomous agents.

Choose the smallest necessary implementation. Track applicable gates and require exact-SHA evidence. A commit, a generated status report, a workflow definition, or a draft PR is not successful end-to-end CI/CD.

Quota is PER-ORGANIZATION, never global. The owner confirmed LyonPro exhausted and leminhphap-improvement available on 2026-10-17. Before any CI dispatch, run the read-only .cicd/org_quota.py preflight for the exact target owner/repo; do not transplant LyonPro's suspension to this repository. GitHub API 403 or missing billing data is UNKNOWN, not exhaustion or availability. This repository's quota-aware verification workflow is allowed on push to main only, or manual dispatch, under owner-confirmed available scope; exact-SHA CI remains unverified until a real job completes. Never move code, workflow, or credentials across organizations without explicit authorization.

Never copy private LyonPro sources, tokens, protected job data, or private connector credentials into this public repository. Access to GitHub/Notion/Google/CLI is verified separately per current session and account.

Sources: .cicd/config.json, .cicd/verify.py, docs/CI-CD-FULL-FLOW.md.

## Global automation policy — no manual GitHub Actions dispatch (owner decision, 2026-10-30)

- Do not add or retain `workflow_dispatch` in GitHub Actions workflows for this repository. The sole organization exception is repositories owned by GitHub organization `huyquanghan-apps`; this exception does not apply to other organizations or personal repositories.
- Do not replace manual dispatch with another human-operated workflow button, input form, or manual rerun path. For normal work, follow Unified Project Workflow: inspect the exact source/ref, make the authorized change, commit/push it, and let the narrowest relevant automatic trigger run. Docs-only changes use source readback and appropriate local checks; do not create CI just to simulate a manual action.
- Replace parameterized dispatch with version-controlled, validated request/config files and scoped `push.paths` triggers. Preserve explicit authorization, privacy, destructive-action, and release gates as data/approval checks in the request contract; never convert a human authorization requirement into an unattended side effect.
- Use native lifecycle events where they are the true cause: `push` for committed source/config/request changes, `pull_request` for review checks, `release`/tag push for releases, `workflow_run` for dependent automation, and `schedule` only for genuinely recurring work. Avoid broad push triggers; scope paths and branches.
- One-off diagnostics and parameterized experiments are run by the active Unified Project Workflow executor against the exact target, not by a manually dispatched Actions workflow. Record source SHA, commands, output, and acceptance evidence. Do not claim a hosted run if no automatic event actually started it.
- Update tests, docs, scripts and downstream consumers that assume manual dispatch. Add a negative conformance check that rejects `workflow_dispatch` in workflow YAML. Do not leave dead inputs or branches that depend on the removed event.
- Organization exclusion: do not change repositories owned by `huyquanghan-apps`. This is the only exception. Repositories in `leminhphap-improvement`, `LyonPro`, `lyon-game`, and personal accounts are in scope when accessible.
