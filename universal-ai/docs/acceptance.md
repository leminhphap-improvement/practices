# Product gates and CI/CD Full Flow mapping

## Original product intent
Make a portable, safe, minimal, extensible Universal AI Assistant architecture with four engineering mechanisms, cross-cutting Agent Team/Skills, six audience-domain packs, cross-surface adapters, and source-backed continuous improvement. Avoid a mandatory 21-agent ceremony, forced account connectors and false success.

## Acceptance matrix
1. **TASK/ROUTER:** short chat answers through the low-effort path; high-impact engineering selects limited accountable/reviewer roles.
2. **GOAL/LOOP:** exactly preserved original goal; versioned acceptance; unresolved criteria remain visible; no self-certification.
3. **HARNESS:** no tool calls without explicit capabilities; no credentials/API keys baked into config; checkpoint persists through interruption.
4. **GRAPH:** past/current/future and goal-to-acceptance edges, observed evidence and explicit gaps; lessons can be proposed but not automatically applied.
5. **PACKS:** user-selectable chat/learning/career/work/engineering/research/team/integrations with a common contract.
6. **ADAPTERS:** ChatGPT Web, generic AI web and CLI can accept the same structured task and manual checkpoint; live agent/OAuth/provider execution not claimed.
7. **SECURITY:** reject sensitive-key fields and fabricated independent invocations; owner scopes CI quota and credential boundary.
8. **CI:** unittest suite and provenance checks PASS on exact SHA of work; no claim of user-level or integration E2E from CI alone.

## Verification limits
Static/unit/CLI/packaging can be verified by this GitHub repo. Hosted ChatGPT Free, Gemini web, VS Code OmniRoute, persistent cross-session connectors, accessibility for children/seniors and real autonomous agents require separate authorized real-environment testing. No auto-release to production without explicit acceptance.

## Evidence levels
`LOCAL_FILE` means a local file was hashed, not that its claims were independently verified. `PROVIDER_TRACE` and `REAL_BROWSER` must only be recorded if actual execution is observed; this starter does not provide those runtime connectors. QA perspectives in one chat are not independent agents.

## CI applicability
For this non-production Python starter: Python syntax, unit tests, portable ZIP artifact SHA256, exact-SHA CI are applicable. APK signing, Play Store, cloud deployment and production credential boundary are not applicable. Versioned public release is a separate explicit-goal gate, not automatically authorized by installing a starter.
