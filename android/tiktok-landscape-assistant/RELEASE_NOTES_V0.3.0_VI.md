# Landscape Episode Assistant — v0.3.0-alpha (E2E engineering preview)

## What's new
- Full Auto Next now has two independent Quick Settings tiles: **TikTok Auto Next** toggle and a **Next Episode** user-request tile. Neither draws an overlay on the fullscreen video.
- A bounded, fail-closed parser recognises **Tập X/Y**, **Episode X of Y** for explicit end-of-series protection. When X=Y, it does **not** advance to another video, and attempts Pause when an actual Pause control is exposed.
- Player progress detection now accepts Accessibility `RangeInfo` or a tightly contextualized `00:30 / 02:00` label.
- The latest TikTok scan can be viewed under **Làm mới chẩn đoán** after the user returns to app settings. Only status flags and optional episode counts, never video titles/account text, are persisted.
- Default generic swipe remains OFF; fallback timer defaults to 0. No access to TikTok's private API, no root, and no overlay.
- Brightness Lite remains a separate package with its user-verified manual brightness control, not Auto Next.

## Where to start
- Download the **Full** alpha only for compliant test distribution; Full Accessibility APK may still be blocked by Google Play Protect when sideloaded.
- `PLAY_CONSOLE_HANDOFF_VI.md`: owner-only official testing distribution.
- `E2E_TEST_VI.md`: same-device real-player acceptance protocol.
- `PRIVACY_POLICY.md`: exact permissions, local diagnostics, and disclosure.

## Known limitations — do not interpret GitHub CI green as product acceptance
- **Full installation via Play Protect-approved route: NOT VERIFIED.** Old Full `v0.1.0-alpha.7` was blocked on user Android.
- **TikTok RangeInfo/Next controls exposed in landscape: UNKNOWN** until device probe.
- **Correct episode sequence, stop at final episode, fullscreen behavior and auto brightness restore in Full: NOT VERIFIED on device.**
- If TikTok provides no usable progress and no Next control, this build safely does nothing by default.
- The GitHub release's `*-unsigned.aab` is only an intermediate for an owner-controlled Play signing workflow; it is neither installable nor a Play-approved application.

## Evidence and continuity
- User report: Brightness Lite `v0.2.0-alpha` installed successfully and its brightness toggle works.
- Every release must have exact-SHA CI PASS, unsigned bundle build, both debug APKs, SHA-256 checksums, and matching release tag.
- [Open E2E acceptance issue](https://github.com/leminhphap-improvement/practices/issues/1), with task/status boundaries.
