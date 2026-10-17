# AI Agent Instructions — practices

This public repository is a **non-production DevOps trial**. The repository's original user goal, exact Git ref/SHA, and observed outputs are authoritative. Do not silently move to another repository, org, or branch.

## Explicit CI/CD Full Flow invocation

When the user invokes @CI/CD Full Flow, load the installed skill in the current authorized ChatGPT account, read .cicd/config.json and the relevant repository source, then use the goal-to-evidence loop. Repository instructions alone do not install a ChatGPT plugin or start autonomous agents.

Choose the smallest necessary implementation. Track applicable gates and require exact-SHA evidence. A commit, a generated status report, a workflow definition, or a draft PR is not successful end-to-end CI/CD.

GitHub Actions are CI_QUOTA_SUSPENDED. Do not dispatch, rerun, enable automatic triggers, or claim a fresh CI PASS until the owner explicitly confirms restored capacity. Run local or read-only checks where available; record CI as DEFERRED / NOT_RUN. A full-flow release remains blocked where CI is applicable.

Never copy private LyonPro sources, tokens, protected job data, or private connector credentials into this public repository. Access to GitHub/Notion/Google/CLI is verified separately per current session and account.

Sources: .cicd/config.json, .cicd/verify.py, docs/CI-CD-FULL-FLOW.md.