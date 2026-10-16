# Google Play official testing handoff — Full Auto Next

## Why this is required
User's Android device blocked the Full sideloaded APK with Google Play Protect because the Full app declares sensitive Accessibility privileges. Google documents automatic blocking for some internet-sideloaded apps with these permissions in Vietnam. Brightness Lite works because it does not request Accessibility. Do not disable Play Protect, fake installer origin, or remove the permission from an APK while secretly retaining controls.

## Distribution choices
**Recommended first:** Google Play Console > **Test and release** > **Internal testing** or **Internal app sharing**, with a verified owner account. See Google help:
- https://support.google.com/googleplay/android-developer/answer/9845334
- https://support.google.com/googleplay/android-developer/answer/9844679
- https://support.google.com/googleplay/android-developer/answer/10964491

### Owner-only one-time steps
1. Sign in to https://play.google.com/console and create the app (unless an app with this package ID is already registered).
2. Review the **Accessibility API** policy. Full uses deterministic automation only: on an observed playback loop, try an explicit Next Episode control. No `isAccessibilityTool` claim is made. Complete the Accessibility Service declaration form with a short screen recording showing in-app prominent disclosure, affirmative consent, Android permission grant, and core feature behavior.
3. Set the privacy-policy URL to a **publicly accessible** copy of `PRIVACY_POLICY.md`; complete applicable App content / Data safety fields accurately.
4. For internal app sharing (where allowed), upload `TikTokLandscapeAssistant-Full-v0.3.0-alpha.apk` from the verified GitHub release. For a normal testing track, sign the **unsigned** release AAB with your own upload keystore in a secure environment. Never send private signing keys to ChatGPT or commit them to GitHub.
5. Add your own Google account as tester, obtain the official opt-in/download URL, and install through Google Play. The platform may still require review or refuse publication; authorization is not guaranteed.
6. Follow `E2E_TEST_VI.md` and report only diagnostics and behavior.

**Important:** A successful Android Gradle build, debug APK, or GitHub Release is **not** permission to sideload a blocked Full app; only actual installation through an accepted legitimate channel and on-device verification close the product gate.
