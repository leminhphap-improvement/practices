# E2E test plan — TikTok landscape Auto Next (v0.3.0 alpha)

**Scope and evidence classes:**
- User-confirmed on real device: Brightness Lite installs and manual 100% brightness toggle works (Oct 2026).
- GitHub CI proves build, unit tests, and packaging **only**; no runtime TikTok control is proven.
- Full APK still declares Android Accessibility and may be blocked by Play Protect when sideloaded. **Do not disable Play Protect**.

## Prerequisite: compliant channel to test Full
1. Use an existing verified Google Play Console account (or create/verify it as the account owner). The assistant has no access to the account and cannot publish for you.
2. Choose an official **Internal testing** track or **Internal app sharing** for authorized testers. Declare Accessibility usage and follow Google's policy. Do not claim `isAccessibilityTool=true` unless the app's primary purpose truly qualifies as disability support.
3. If using internal app sharing, the authorized uploader can upload the debug APK from the GitHub release; for a normal track, a properly signed Android App Bundle with the correct version is normally preferred. CI also emits an **unsigned AAB** for signing through your own secure release process.
4. Obtain the official Google Play installation/opt-in link and install via the platform-approved path. **Release creation on GitHub alone does not satisfy this.**

## Test in one bounded session on the device
1. Start from TikTok portrait: verify app does not change brightness or send gestures (portrait untouched).
2. In Full app, read the prominent Accessibility disclosure, consent only if acceptable; grant Android Accessibility and Modify system settings. Add Quick Settings tiles `TikTok Auto Next` and `Next Episode`.
3. Enable Auto Next, keep fallback swipe OFF and Timer=0. Open a known series with **at least three episodes** and rotate to landscape fullscreen.
4. Watch the first episode loop once. Observe whether the app clicks the correct **Next Episode** button instead of replaying. If not, open Full app > **Làm mới chẩn đoán**. Report `Scan`, `Progress RangeInfo`, `Nút Next Episode`, `Tập/Tổng`, `Hành động`. **Do not send TikTok account details**.
5. From landscape, open Quick Settings > `Next Episode`, close the shade within 10 seconds. Verify manually requesting Next goes to correct episode. If diagnostics say `NO_SAFE_NEXT_CONTROL`, don't switch to generic swipe automatically.
6. Test last episode if the UI explicitly exposes `Tập X/Y`. When `X=Y`, confirm no gesture advances to unrelated content, and verify Pause if TikTok exposes it. If the counter is absent/ambiguous, mark final-stop as **UNKNOWN**.
7. Leave landscape / close TikTok; ensure brightness returns to prior mode and value. Return to portrait and confirm no automated action.
8. Disable `TikTok Auto Next` via Quick Settings; confirm the service stops. Revoke Accessibility and check the system works normally.

## Explicit acceptance matrix
| Criterion | Expected evidence |
|---|---|
| Correct next episode | Device video before/after (recording optional, scrub identifying details) |
| No action in portrait | On-device comparison |
| No overlays | Landscape fullscreen screenshot |
| Brightness max and restore | System brightness slider before/during/after |
| Last episode stops | Explicit episode index plus no advance and player pause |
| No unwanted swipe | Fallback default OFF, diagnostics `NO_SAFE_NEXT_CONTROL` |
| Install not blocked | Installation through allowed official channel |
| Local privacy | Source/manifest test, user review |

**Decision:** If `Progress RangeInfo = NO`, `Nút Next Episode = NO` and no valid timer label, the app cannot determine when to press Next through Accessibility on that TikTok version. Treat that as a **capability gap**, not a successful E2E outcome.

## Return packet (one message)
- Android version, TikTok version (Settings > About where available), TikTok package/version if visible.
- Copy of the 5 diagnostic lines from Full app.
- Installation outcome, and whether episode 1→2, 2→3, last-episode stop, portrait untouched, and brightness restore passed.
- Do **not** send account passwords, OTPs, private messages or unrelated on-screen contents.
