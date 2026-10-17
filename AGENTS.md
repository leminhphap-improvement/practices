# AI Agent Instructions — practices

This public repository is a **non-production DevOps trial**. The repository's original user goal, exact Git ref/SHA, and observed outputs are authoritative. Do not silently move to another repository, org, or branch.

## Explicit CI/CD Full Flow invocation

When the user invokes @CI/CD Full Flow, load the installed skill in the current authorized ChatGPT account, read .cicd/config.json and the relevant repository source, then use the goal-to-evidence loop. Repository instructions alone do not install a ChatGPT plugin or start autonomous agents.

Choose the smallest necessary implementation. Track applicable gates and require exact-SHA evidence. A commit, a generated status report, a workflow definition, or a draft PR is not successful end-to-end CI/CD.

Quota is PER-ORGANIZATION, never global. The owner confirmed LyonPro exhausted and leminhphap-improvement available on 2026-10-17. Before any CI dispatch, run the read-only .cicd/org_quota.py preflight for the exact target owner/repo; do not transplant LyonPro's suspension to this repository. GitHub API 403 or missing billing data is UNKNOWN, not exhaustion or availability. The existing manual workflow may be invoked only for a selected permitted repository; exact-SHA CI is still unverified until an actual run completes. Never move code, workflow, or credentials across organizations without explicit authorization.

Never copy private LyonPro sources, tokens, protected job data, or private connector credentials into this public repository. Access to GitHub/Notion/Google/CLI is verified separately per current session and account.

Sources: .cicd/config.json, .cicd/verify.py, docs/CI-CD-FULL-FLOW.md.