#!/usr/bin/env python3
"""Portable, standard-library assistant contract. No LLM or external tools are invoked."""
import argparse
import hashlib
import json
import os
from pathlib import Path
import tempfile
from datetime import datetime, timezone

ROOT=Path(__file__).resolve().parent
KINDS={"short_chat","learning","career","work","engineering","research","team","integrations"}
EVIDENCE={"UNKNOWN","USER_REPORTED","LOCAL_FILE","PROVIDER_TRACE","REAL_BROWSER","USER_ACCEPTED"}
STATES={"PENDING","PASS","FAIL","UNKNOWN","NOT_APPLICABLE"}
FORBIDDEN_KEYS={"api_key","access_token","refresh_token","password","secret","private_key","raw_transcript","cookie","credential"}
class ContractError(ValueError): pass

def now():
    return datetime.now(timezone.utc).isoformat()

def load_json(path):
    with Path(path).open(encoding="utf-8") as handle:
        return json.load(handle)

def check_clean(value):
    if isinstance(value,dict):
        for key,item in value.items():
            if str(key).lower() in FORBIDDEN_KEYS: raise ContractError("SENSITIVE_FIELD_FORBIDDEN:"+str(key))
            check_clean(item)
    elif isinstance(value,list):
        for item in value: check_clean(item)

def catalog():
    return load_json(ROOT/"catalog.json")

def choose(task):
    if not isinstance(task,dict): raise ContractError("TASK_OBJECT_REQUIRED")
    check_clean(task)
    kind=task.get("kind")
    if kind not in KINDS: raise ContractError("EXPLICIT_KIND_REQUIRED")
    goal=task.get("goal")
    if not isinstance(goal,str) or not goal.strip(): raise ContractError("GOAL_REQUIRED")
    impact=task.get("impact","low")
    if impact not in ("low","medium","high"): raise ContractError("INVALID_IMPACT")
    allowed=task.get("capabilities",[])
    if not isinstance(allowed,list) or any(not isinstance(x,str) for x in allowed):
        raise ContractError("INVALID_CAPABILITIES")
    defined=catalog()["packs"][kind]
    selected_roles=[defined["accountable"]]
    if impact=="high" and kind in ("work","career","engineering","team","integrations"):
        selected_roles+=defined["reviewers"]
    selected_roles=list(dict.fromkeys(selected_roles))
    return {"mode":defined["mode"],"pack":kind,"effort":"minimal" if impact=="low" else "bounded" if impact=="medium" else "evidence_intensive",
            "skills":defined["skills"],"roles":selected_roles,
            "team_mode":"ROLE_CONTRACT_ONLY" if len(selected_roles)>1 else "SINGLE_ASSISTANT",
            "agent_invocations":"NOT_EXECUTED","tools":allowed,
            "principles":["PROMPT","LOOP","HARNESS","GRAPH"],
            "human_owner":"final_decision"}

def initial(task):
    route=choose(task)
    criteria=task.get("acceptance")
    if not isinstance(criteria,list) or not criteria or any(not isinstance(x,str) or not x.strip() for x in criteria):
        raise ContractError("EXPLICIT_ACCEPTANCE_REQUIRED")
    if len(set(criteria))!=len(criteria): raise ContractError("DUPLICATE_ACCEPTANCE")
    result={"schema_version":1,"original_goal":task["goal"],"task":task,"route":route,
        "started_at":now(),"updated_at":now(),"execution_status":"NOT_EXECUTED",
        "acceptance":[{"id":"AC-"+str(i+1),"criterion":criterion,"status":"PENDING","evidence":[]} for i,criterion in enumerate(criteria)],
        "graph":{"past":[],"current":["goal_defined"],"future":["perform_authorized_work","verify_acceptance"],
            "edges":[{"from":"goal","to":"AC-"+str(i+1),"type":"REQUIRES"} for i in range(len(criteria))]},
        "lessons":[],"checkpoint":{"next_action":"perform_first_unmet_criterion","source":"LOCAL_STATE_ONLY"}}
    return result

def atomic_save(path,obj):
    path=Path(path)
    path.parent.mkdir(parents=True,exist_ok=True)
    data=json.dumps(obj,indent=2,ensure_ascii=False)+"\n"
    fd,tmp=tempfile.mkstemp(prefix=".assistant-",dir=str(path.parent))
    try:
        os.fchmod(fd,0o600)
        with os.fdopen(fd,"w",encoding="utf-8") as handle:handle.write(data)
        os.replace(tmp,path)
    finally:
        if os.path.exists(tmp):os.unlink(tmp)

def validate(state):
    check_clean(state)
    if state.get("schema_version")!=1 or state.get("route",{}).get("agent_invocations")!="NOT_EXECUTED":
        raise ContractError("UNTRUSTED_STATE")
    for item in state.get("acceptance",[]):
        if item.get("status") not in STATES:raise ContractError("INVALID_GATE")
        if item.get("status")=="PASS" and not item.get("evidence"):
            raise ContractError("PASS_WITHOUT_EVIDENCE")
        for record in item.get("evidence",[]):
            if record.get("class") not in EVIDENCE: raise ContractError("INVALID_EVIDENCE")
            if record.get("class")=="LOCAL_FILE" and not record.get("sha256"):
                raise ContractError("MISSING_LOCAL_SHA")
    return state

def record(state,gate,status,proof=None):
    validate(state)
    if status not in ("PASS","FAIL","UNKNOWN","NOT_APPLICABLE"):raise ContractError("INVALID_STATUS")
    if status=="NOT_APPLICABLE":raise ContractError("ORIGINAL_CRITERION_CANNOT_BE_WAIVED")
    found=next((x for x in state["acceptance"] if x["id"]==gate),None)
    if found is None: raise ContractError("UNKNOWN_ACCEPTANCE_GATE")
    if status=="PASS":
        if not proof:raise ContractError("PASS_REQUIRES_LOCAL_PROOF")
        path=Path(proof)
        if not path.is_file():raise ContractError("PROOF_FILE_MISSING")
        payload=path.read_bytes()
        if not payload:raise ContractError("EMPTY_PROOF")
        stamp={"class":"LOCAL_FILE","path":path.name,"sha256":hashlib.sha256(payload).hexdigest(),"recorded_at":now(),
               "limitation":"LOCAL_FILE_IS_NOT_PROVIDER_OR_USER_ACCEPTANCE"}
        found["evidence"].append(stamp)
    else:
        found["evidence"].append({"class":"USER_REPORTED","recorded_at":now(),"limitation":"NOT_INDEPENDENTLY_VERIFIED"})
    found["status"]=status
    state["graph"]["past"].append({"gate":gate,"status":status,"at":now()})
    remaining=[x["id"] for x in state["acceptance"] if x["status"]!="PASS"]
    state["graph"]["current"]=[remaining[0]] if remaining else ["independent_acceptance_pending"]
    state["graph"]["future"]=remaining[1:] if remaining else ["independent_acceptance"]
    state["updated_at"]=now()
    state["checkpoint"]["next_action"]=remaining[0] if remaining else "request_user_acceptance"
    return state

def summary(state):
    validate(state)
    missing=[x["id"] for x in state["acceptance"] if x["status"] not in ("PASS","NOT_APPLICABLE")]
    all_local=not missing and bool(state["acceptance"])
    return {"state":"READY_FOR_INDEPENDENT_ACCEPTANCE" if all_local else "INCOMPLETE",
            "completed_gate_count":sum(1 for x in state["acceptance"] if x["status"]=="PASS"),
            "total_gates":len(state["acceptance"]),"next_action":state["checkpoint"]["next_action"],
            "missing_gates":missing,"runtime":"NO_AUTONOMOUS_EXECUTION",
            "product_acceptance":"NOT_VERIFIED","checkpoint_source":"LOCAL_FILE"}

def main(argv=None):
    p=argparse.ArgumentParser(description="Portable assistant starter; no background execution")
    sub=p.add_subparsers(dest="command",required=True)
    for name in ("route","init"):
        s=sub.add_parser(name);s.add_argument("--task",required=True)
        if name=="init":s.add_argument("--state",required=True)
    s=sub.add_parser("resume");s.add_argument("--state",required=True)
    s=sub.add_parser("record");s.add_argument("--state",required=True)
    s.add_argument("--gate",required=True);s.add_argument("--status",required=True)
    s.add_argument("--proof")
    args=p.parse_args(argv)
    try:
        if args.command=="route":out=choose(load_json(args.task))
        elif args.command=="init":
            state=initial(load_json(args.task));atomic_save(args.state,state);out=summary(state)
        else:
            state=validate(load_json(args.state))
            if args.command=="record":
                record(state,args.gate,args.status,args.proof);atomic_save(args.state,state)
            out=summary(state)
    except (ContractError,ValueError,KeyError,OSError) as e:
        p.exit(2,"CONTRACT_ERROR: "+str(e)+"\n")
    print(json.dumps(out,ensure_ascii=False,indent=2))
if __name__=="__main__":main()
