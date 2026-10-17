# CI/CD Full Flow — practices (public-safe bootstrap)

## Scope and ownership

- This is an opt-in repo-local setup for the already installed @CI/CD Full Flow 2.3 skill in the invoking ChatGPT account, not an installed organization-wide service.
- Do not mistake this public trial repository for the private core or copy private role registry/source into it.
- Protected engineering repository implementations in the same organization are separately version-pinned; audit them independently rather than duplicating source here.
- The current GitHub Actions quota is suspended. The checked-in workflow is workflow_dispatch only. Do not dispatch until the owner explicitly restores CI authorization/capacity.

## Bounded execution per goal

1. Resolve original user goal, repository, exact base SHA and acceptance matrix.
2. Recover previous state from actually authorized sources, classify production/test scope and credential boundaries.
3. Inspect the target code and use the smallest effective plan and patch.
4. Execute local static/unit/integration checks where available and capture actual observed outputs.
5. Create branch/PR only when authorized; preserve default branch and prior SHA for rollback.
6. When CI quota is restored, run the relevant workflow on the target exact SHA, inspect run/job evidence and repair agent-owned failures.
7. Build/package/release only when goal requires it; validate artifact identity, release target SHA, checksum and original goal.
8. Record PASS only for proven applicable gates; UNKNOWN, NOT_RUN, DEFERRED or open failures must remain explicit.

## Offline bootstrap check

Run `python3 .cicd/verify.py` inside a checked-out repository. Its PASS_STATIC_ONLY result is not hosted CI PASS, plugin runtime installation, or verified user goal acceptance.

## Current target and rollback

- Public practices original main SHA at setup: b2a8c01e45cd2cc8ef6aad1cb6b4ce2459acb780.
- Bootstrap is staged on an isolated automation branch and reviewed through a draft PR, not merged by setup.
- If the proposal is rejected, close the draft PR and delete the branch; the original main remains intact.

## Explicit manual prerequisites

- Owner must explicitly approve restoring GitHub Actions quota before hosted runs.
- Other users must install/obtain access to their own applicable plugin/skills and connectors; this repository cannot transfer installations or OAuth.
- An unrelated build/deploy requires its own application source, target environment, credentials and observed acceptance.
