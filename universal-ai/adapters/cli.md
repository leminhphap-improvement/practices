# CLI adapter (local, no external services)
`python3 universal-ai/assistant.py route --task universal-ai/examples/short.json`
`python3 universal-ai/assistant.py init --task universal-ai/examples/engineering.json --state assistant-state.json`
`python3 universal-ai/assistant.py resume --state assistant-state.json`
Checkpoints remain local with 0600 permissions. This program is **not** an OmniRoute connector. To add one, build a permissioned adapter that verifies provider identity, records actual invocation/response, and enforces idempotency + credential isolation.
