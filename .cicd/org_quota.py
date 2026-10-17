#!/usr/bin/env python3
"""Read-only GitHub Actions org-quota router. Usage reports are not balance reports."""
import argparse, json, re, subprocess, sys
from datetime import datetime, timezone
from pathlib import Path
VALID = re.compile(r"^[A-Za-z0-9][A-Za-z0-9-]{0,38}$")

def api(path):
    try:
        p = subprocess.run(["gh","api","--method","GET",path],capture_output=True,text=True,timeout=20)
        if p.returncode: return None, "API_UNAVAILABLE_OR_UNAUTHORIZED"
        return json.loads(p.stdout), None
    except (FileNotFoundError,subprocess.TimeoutExpired,ValueError):
        return None,"API_UNAVAILABLE"

def budget_exhausted(data):
    entries=data if isinstance(data,list) else data.get("budgets",[]) if isinstance(data,dict) else []
    for entry in entries:
        if not isinstance(entry,dict) or entry.get("prevent_further_usage") is not True: continue
        product=str(entry.get("budget_product_sku","")).lower()
        if "action" not in product: continue
        cap=entry.get("budget_amount")
        spent=entry.get("spent_amount",entry.get("amount_spent"))
        if isinstance(cap,(int,float)) and isinstance(spent,(int,float)) and spent>=cap: return True
    return False

def classify(org,policy,repo=None,get=api):
    if not VALID.fullmatch(org): raise ValueError("INVALID_ORG")
    record=policy.get("organizations",{}).get(org,{})
    status=record.get("status","UNKNOWN"); evidence=record.get("evidence","NONE")
    if status not in ("AVAILABLE","EXHAUSTED","UNKNOWN"): raise ValueError("INVALID_STATUS")
    if evidence!="USER_CONFIRMED": status="UNKNOWN"
    checks=[]
    if status=="EXHAUSTED":
        return dict(organization=org,status=status,evidence=evidence,checks=["NO_BILLABLE_PROBE"],allowed_to_dispatch=False)
    if repo:
        data,error=get("/repos/"+org+"/"+repo)
        if error or not isinstance(data,dict) or data.get("full_name","").lower()!=(org+"/"+repo).lower():
            return dict(organization=org,status="UNKNOWN",evidence="REPOSITORY_NOT_VERIFIED",checks=[error or "WRONG_REPO"],allowed_to_dispatch=False)
        checks.append("EXACT_REPOSITORY_VERIFIED")
    permissions,error=get("/orgs/"+org+"/actions/permissions")
    if not error and isinstance(permissions,dict) and permissions.get("enabled_repositories")=="none":
        return dict(organization=org,status="UNKNOWN",evidence="ACTIONS_DISABLED",checks=checks+["DISABLED"],allowed_to_dispatch=False)
    checks.append("PERMISSIONS_"+(error or "READ"))
    budgets,error=get("/organizations/"+org+"/settings/billing/budgets?per_page=100")
    if not error and budget_exhausted(budgets):
        return dict(organization=org,status="EXHAUSTED",evidence="API_BUDGET_HARD_CAP",checks=checks+["HARD_CAP_REACHED"],allowed_to_dispatch=False)
    checks.append("BUDGET_"+(error or "NO_PROVEN_HARD_CAP"))
    usage,error=get("/organizations/"+org+"/settings/billing/usage/summary?product=Actions")
    checks.append("USAGE_"+(error or "HISTORY_NOT_REMAINING_QUOTA"))
    return dict(organization=org,status=status,evidence=evidence if status!="UNKNOWN" else "INSUFFICIENT_EVIDENCE",
                checks=checks,allowed_to_dispatch=status=="AVAILABLE")

def main():
    p=argparse.ArgumentParser()
    p.add_argument("--policy",default=str(Path(__file__).with_name("org-quota-policy.json")))
    p.add_argument("--org",action="append",required=True)
    p.add_argument("--repo",required=True)
    p.add_argument("--require-available",action="store_true")
    args=p.parse_args()
    if not VALID.fullmatch(args.repo): p.error("invalid repository basename")
    policy=json.loads(Path(args.policy).read_text(encoding="utf-8"))
    observations=[classify(o,policy,args.repo) for o in args.org]
    allowed=next((x for x in observations if x["allowed_to_dispatch"]),None)
    print(json.dumps(dict(schema_version=1,checked_at_utc=datetime.now(timezone.utc).isoformat(),
            target_repo=args.repo,organizations=observations,selected_org=allowed["organization"] if allowed else None,
            work_relocation="FORBIDDEN_WITHOUT_AUTHORIZATION",actions_dispatched=False),indent=2))
    return 0 if allowed or not args.require_available else 2
if __name__=="__main__": sys.exit(main())
