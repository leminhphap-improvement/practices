# TikTok Landscape Assistant (Android, alpha)

**Goal:** keep TikTok's own fullscreen landscape player unobstructed, automatically advance when the video loops, and temporarily maximize screen brightness. Portrait TikTok remains untouched.

This is an **experimental Android source project**, NOT a validated release APK. TikTok's player may not expose its progress and next-episode actions to Accessibility; behavior varies by version, region, video type and device. There is no publicly supported TikTok episode-control API used here. **End of series cannot be determined reliably by generic swiping.** TikTok may restrict third-party automation; no promise of non-detection is made.

## Features implemented in source

- Quick Settings tile toggles service logic; no overlay or floating icon on TikTok.
- Accessibility service limited at runtime to TikTok packages `com.zhiliaoapp.musically` and `com.ss.android.ugc.trill` in **landscape**.
- If a semantic progress bar is accessible, detects end-to-start loop and tries a labeled Next Episode button.
- Optional **explicitly enabled** fallback swipe if no Next control is found; default OFF because it can navigate to unrelated videos.
- Optional fixed-duration timer if the player exposes no progress. Default `0` disables risky timer; duration is entered in seconds (15–7200).
- Brightness 255/255 for enabled landscape TikTok, after WRITE_SETTINGS permission; saves previous brightness level and auto/manual mode and restores when leaving, turning off, or destroying the service.
- Settings UI is separate from TikTok. No recording, remote API, injection, or root.

**Not implemented:** real-time adjustable seek bar / episode list from TikTok; guaranteed detection of last episode; installation APK; verified TikTok behavior on a physical device. These require first proving TikTok exposes relevant controls in landscape. No TikTok bypass or anti-detection scheme.

## Build (requires network for first-time Android dependencies)

Prerequisites: Android Studio (Android Gradle Plugin 8.6.1), JDK 17, Android SDK 35, Android device API 26+.

1. Open this project directory in Android Studio; allow Gradle sync and install recommended SDK packages. This repository intentionally does not ship a Gradle wrapper binary.
2. Build > Build APK(s), or with installed Gradle 8.7+ run `gradle :app:assembleDebug`.
3. Debug APK output: `app/build/outputs/apk/debug/app-debug.apk`. For private sideloading, install this on your own device (enable the Android "Install unknown apps" permission for your file manager, if requested).
4. Android Settings > Accessibility > Downloaded apps > TikTok Landscape Assistant > On.
5. Open the app > grant **Modify system settings** permission (brightness) > toggle Auto Next and Max Brightness.
6. Pull down Quick Settings twice > Edit > add **TikTok Auto Next** tile. Tap tile to enable/disable.
7. Open TikTok and play a landscape movie. Leave the fallback timer and swipe OFF initially. Check that auto brightness is restored when returning to portrait/closing TikTok.
8. If automatic Next does not work because TikTok doesn't expose the player, a per-video timer (seconds) and fallback swipe can be tried **only after confirming actual gesture behavior manually**; these cannot guarantee next episode or stop at the last one.

## Privacy / security

Accessibility is a sensitive, user-granted Android permission. This app may read **visible UI text** to find only the video progress/next episode; it does not transmit it and asks for no internet permission. The active check is scoped to TikTok package and landscape. Disable Accessibility permission at any time from Android Settings. System brightness has a stored recovery snapshot but forced shutdown/permission revocation may prevent immediate restoration; reopen and tap **Restore brightness** once permission is granted.

## Validation

Run pure-Java decision tests: `sh verification/test-end-detector.sh`. Full Android build, signed APK and on-device acceptance remain unverified until Android SDK/Gradle and physical TikTok playback are available.
