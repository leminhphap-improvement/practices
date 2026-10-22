# Guided Onboarding — Start From an Empty Workspace

Use this when no authoritative goal, project, or checkpoint exists. Do not force an interview when an existing project can be recovered.

## Interaction contract
- Ask one high-value question per turn and adapt to the answer; do not use a long intake form by default.
- Begin with the outcome the user wants and why it matters.
- Clarify only critical missing details: deliverable, observable acceptance, constraints/exclusions, audience/environment, starting point/assets, sources/tools, collaboration style, answer language/format, autonomy/approval boundaries, privacy and continuity.
- Offer examples or choices when they reduce ambiguity.
- Separate confirmed facts, assumptions and unknowns.
- Do not declare requirements 100% clear just because a fixed question list is exhausted. Summarize a compact goal charter and ask the user to confirm/correct it when material ambiguity remains.
- Until the charter is confirmed, limit work to reversible discovery and label assumptions.

## First artifact: Project Instructions
After charter confirmation, produce a complete draft based on PROJECT-INTRO-TEMPLATE.md, keep it under 8,000 characters with a safety margin, and report its character count. The user must paste it into the AI host's Project Instructions because a plugin/repository cannot edit host UI settings directly.

Project Instructions are a routing layer, not the full architecture. Keep implementation details, domain packs, schemas, task graphs, acceptance evidence and checkpoints in the selected repository.

## Next action
1. Inspect any supplied repository or ZIP before asking the user to repeat known information.
2. Resolve/create the target repository or a portable starter bundle.
3. Preserve the immutable goal charter and derive measurable acceptance criteria.
4. Create/adapt AI-ARCHITECTURE-WORKFLOW.md, the domain pack and .ai-workflow state.
5. Run local conformance tests and authorized exact-SHA CI; report runtime/host limits honestly.
