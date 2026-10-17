# Universal AI Assistant — portable public starter v0.1

This is a **new independently-authored public starter** for the user-defined Universal AI Assistant vision. It does **not** duplicate private LyonPro orchestration code, impersonate the installed ChatGPT plugins, or claim live cross-model agent execution.

## Architecture
- **Prompt:** task-sized routing, explicit mode/role/skill contracts.
- **Loop:** acceptance criteria, minimal unmet next action, no fabricated completion.
- **Harness:** explicit permitted capabilities, local checkpoint, no assumed OAuth/CI/runners.
- **Graph:** past/current/future plus goal-to-acceptance edges and evidence history.
- **Agent Team:** roles are bounded occupational *contracts*; not automatically invoked independent agents.
- **Packs:** short chat, learning, career, work, engineering, research, agent team, integrations. Products are installable/explainable separately; users need only one.

## Try it (zero dependencies; Python >=3.10)

```bash
python3 universal-ai/assistant.py route --task universal-ai/examples/short.json
python3 universal-ai/assistant.py init --task universal-ai/examples/engineering.json --state ./assistant-state.json
python3 universal-ai/assistant.py resume --state ./assistant-state.json
python3 -m unittest discover -s universal-ai/tests -v
```

The command `record --state FILE --gate AC-1 --status PASS --proof PROOF_FILE` stores a local SHA-256 evidence reference. It **does not** prove a tool invoked, a model executed, peer independence, customer acceptance or production release. The resulting status is `READY_FOR_INDEPENDENT_ACCEPTANCE`, never auto-`COMPLETED_VERIFIED`.

## One core, optional products

| Pack | Minimal useful capability | Live services needed? |
|---|---|---|
| Short Chat | concise responses, uncertainty | no |
| Learning | Mentor + observed learner attempt | no for manual |
| Career | JD/CV-backed preparation | private input, supplied by user |
| Work | PM/BA scoping and decisions | no for manual |
| Engineering | goal -> code/test/evidence handoff | tools only when authorized |
| Research | verify facts and contrary evidence | web only when available |


| Team | Issue-backed role/team handoff and bounded review contracts | no for manual |
| Integrations | capability permissions + provider-neutral adapter boundary | requires external authorization for live execution |

See `catalog.json`, `packs/`, `adapters/`, and `docs/acceptance.md`. For instant chat use `adapters/CHAT-BOOTSTRAP.md`.

## Separation / compatibility
This new public distribution has **no import or runtime dependency** on private `LyonPro/ai-workflow-oschestration` or `leminhphap-improvement/career-training-job` source. Those remain canonical *for their existing engineering products*. The current installed @CI/CD Full Flow 2.3 skill and ChatGPT Workflow plugin are separate host-side capabilities and must be invoked/installed explicitly.

## Trust / deployment scope
Only offline CLI/contract behavior can be validated here. Google OAuth, Gemini Developer API, OmniRoute invocation, GitHub/Notion connectivity, user-specific memory, true multi-agent workers and cross-provider E2E are **NOT IMPLEMENTED** in this starter. Do not expose personal data or secrets in public repositories or issue logs.

## Release/rollback
The parent `practices` repo provides main-only org-scoped GitHub Actions and a separately testable workflow. Release artifacts from this distribution must be inspected at exact SHA; a GitHub Actions green result does not satisfy real audience usability or external provider acceptance.
