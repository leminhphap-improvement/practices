# CI/CD Full Flow — practices (public-safe bootstrap)

## Scope and ownership

- This is an opt-in repo-local setup for the already installed @CI/CD Full Flow 2.3 skill in the invoking ChatGPT account, not an installed organization-wide service.
- Do not mistake this public trial repository for the private core or copy private role registry/source into it.
- Protected engineering repository implementations in the same organization are separately version-pinned; audit them independently rather than duplicating source here.
- Quota is scoped to the BILLING OWNER / organization. Per user confirmation (2026-10-17), LyonPro is exhausted but leminhphap-improvement is available; do not carry the LyonPro suspension into this repo. This workflow remains manual-only. A user report is not an API-verified remaining balance.

## Bounded execution per goal

1. Resolve original user goal, repository, exact base SHA and acceptance matrix.
2. Recover previous state from actually authorized sources, classify production/test scope and credential boundaries.
3. Inspect the target code and use the smallest effective plan and patch.
4. Execute local static/unit/integration checks where available and capture actual observed outputs.
5. Create branch/PR only when authorized; preserve default branch and prior SHA for rollback.
6. Before CI use .cicd/org_quota.py to check the specific owner and target repository via read-only billing/permissions APIs when accessible; never infer exhaustion from a 403, 404, missing runs or empty budget response. Only select a repository verified to exist in the permitted org, and never silently migrate code or credentials. Then execute CI against exact SHA and inspect actual run/job evidence.
7. Build/package/release only when goal requires it; validate artifact identity, release target SHA, checksum and original goal.
8. Record PASS only for proven applicable gates; UNKNOWN, NOT_RUN, DEFERRED or open failures must remain explicit.

## Offline bootstrap check

Run `python3 .cicd/verify.py` inside a checked-out repository. Its PASS_STATIC_ONLY result is not hosted CI PASS, plugin runtime installation, or verified user goal acceptance.

## Current target and rollback

- Public practices original main SHA at setup: b2a8c01e45cd2cc8ef6aad1cb6b4ce2459acb780.
- Bootstrap is staged on an isolated automation branch and reviewed through a draft PR, not merged by setup.
- If the proposal is rejected, close the draft PR and delete the branch; the original main remains intact.

## Workflow availability and acceptance order

GitHub workflow_dispatch workflows must exist on the repository default branch to be invoked normally. Since this workflow is intentionally manual-only, it cannot serve as a new PR pre-merge hosted check while it lives only on the proposal branch.

Safe order: (1) peer-review the PR and run offline tests; (2) merge to this available org's default branch with exact SHA recorded when authorized; (3) run read-only per-org preflight (including Actions policy, budgets and usage when permitted); (4) dispatch the manual workflow on that exact SHA; (5) inspect run/job evidence and relevant release/artifact gates; (6) mark original goal complete only with all applicable gates PASS.

Before an actual successful CI run the CI gate remains NOT_RUN. Even a preflight PASS does not prove remaining quota or successful runner allocation. Do not add automatic triggers merely to probe quota.

## Explicit manual prerequisites

- The LyonPro quota suspension remains scoped only to LyonPro. This organization's owner has confirmed capacity; the read-only preflight must still run per target repository and classify contradictory evidence.
- Other users must install/obtain access to their own applicable plugin/skills and connectors; this repository cannot transfer installations or OAuth.
- An unrelated build/deploy requires its own application source, target environment, credentials and observed acceptance.
