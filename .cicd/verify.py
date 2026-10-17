#!/usr/bin/env python3
"""Offline CI/CD bootstrap validation; not a replacement for hosted CI or goal E2E."""
import json
import os
import pathlib
import subprocess
import sys

ROOT = pathlib.Path(__file__).resolve().parents[1]
CONFIG = json.loads((ROOT / ".cicd/config.json").read_text(encoding="utf-8"))
WORKFLOW = (ROOT / ".github/workflows/ci-cd-full-flow-manual.yml").read_text(encoding="utf-8")

def check(condition, reason):
    if not condition:
        raise SystemExit('FAIL: ' + reason)

def git(*args):
    return subprocess.check_output(['git', '-C', str(ROOT), *args], text=True).strip()

def main():
    check(CONFIG["schema_version"] == 1, "schema_version")
    check(CONFIG["contract"] == "CI/CD Full Flow 2.3", "contract")
    check(CONFIG["action_policy"] == "ORG_SCOPED_PREFLIGHT", "org-aware quota policy")
    quota=json.loads((ROOT / ".cicd/org-quota-policy.json").read_text(encoding="utf-8"))
    check(quota["organizations"]["leminhphap-improvement"]["evidence"] == "USER_CONFIRMED", "quota evidence")
    check((ROOT / ".cicd/org_quota.py").is_file(), "quota preflight missing")
    check(CONFIG["policies"]["no_automatic_actions"], "automatic actions forbidden")
    check(CONFIG["policies"]["strict_sha"], "exact SHA")
    check("workflow_dispatch:" in WORKFLOW, "manual trigger missing")
    for blocked in ("push:", "pull_request:", "schedule:", "workflow_run:"):
        check(not any(line.lstrip().startswith(blocked) for line in WORKFLOW.splitlines()), "unsafe trigger: " + blocked)
    check("contents: read" in WORKFLOW, "least privileged content scope")
    check("ORIGINAL_USER_GOAL" in CONFIG["required_gate_registry"], "goal gate missing")
    check("CI_EXACT_SHA" in CONFIG["required_gate_registry"], "CI gate missing")
    sha = git("rev-parse", "HEAD")
    check(len(sha) == 40 and all(c in "0123456789abcdef" for c in sha), "git SHA")
    expected = os.getenv("GITHUB_SHA")
    if expected:
        check(expected == sha, "GITHUB_SHA / checkout mismatch")
    expected_repo = os.getenv("GITHUB_REPOSITORY")
    if expected_repo:
        check(expected_repo == CONFIG["repository"], "repository identity mismatch")
    print(json.dumps({"result":"PASS_STATIC_ONLY","repository":CONFIG["repository"],"source_sha":sha,"ci_gate":"NOT_RUN","original_goal":"NOT_VERIFIED"}))

if __name__ == "__main__":
    main()
