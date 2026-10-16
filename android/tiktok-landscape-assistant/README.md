> **Installation warning (October 2026):** Google Play Protect blocked the previous Full APK on an Android device because internet-sideloaded apps with Accessibility permission may be classified as high risk. The app cannot safely sidestep Play Protect. See [SECURITY_INSTALL_VI.md](SECURITY_INSTALL_VI.md) for a least-privilege **Brightness Lite** APK and official options for distributing the Full app. Lite **does not include Auto Next**.

# TikTok Landscape Assistant (Android, alpha)

**Goal:** keep TikTok's own fullscreen landscape player unobstructed, automatically advance when the video loops, and temporarily maximize screen brightness. Portrait TikTok remains untouched.

This is an **experimental Android project**. GitHub Actions builds an installable debug-signed APK; pre-releases are for personal testing only and are **NOT validated on a physical TikTok landscape player**. TikTok's player may not expose its progress and next-episode actions to Accessibility; behavior varies by version, region, video type and device. There is no publicly supported TikTok episode-control API used here. **End of series cannot be determined reliably by generic swiping.** TikTok may restrict third-party automation; no promise of non-detection is made.

## Features implemented in source

- Quick Settings tile toggles service logic; no overlay or floating icon on TikTok.
- Accessibility service limited at runtime to TikTok packages `com.zhiliaoapp.musically` and `com.ss.android.ugc.trill` in **landscape**.
- If a semantic progress bar is accessible, detects end-to-start loop and tries a labeled Next Episode button.
- Optional **explicitly enabled** fallback swipe if no Next control is found; default OFF because it can navigate to unrelated videos.
- Optional fixed-duration timer if the player exposes no progress. Default `0` disables risky timer; duration is entered in seconds (15–7200).
- Brightness 255/255 for enabled landscape TikTok, after WRITE_SETTINGS permission; saves previous brightness level and auto/manual mode and restores when leaving, turning off, or destroying the service.
- Settings UI is separate from TikTok. No recording, remote API, injection, or root.

**Not implemented:** real-time adjustable seek bar / episode list from TikTok; guaranteed detection of last episode; verified TikTok behavior on a physical device. These require first proving TikTok exposes relevant controls in landscape. No TikTok bypass or anti-detection scheme.

## Release variants

- `LandscapeBrightnessLite-v0.2.0-alpha.apk`: user-initiated brightness-only Quick Settings tile. No AccessibilityService, TikTok screen access, automatic landscape detection, or Auto Next.
- `TikTokLandscapeAssistant-Full-v0.2.0-alpha.apk`: the original experimental landscape Auto Next; declares sensitive Accessibility API, now explicitly disclosed/consented and scoped to TikTok packages. Play Protect **may still block sideload installs**. Official review/appeal, not protection bypass, is the correct path.
- Release assets include `SHA256SUMS.txt` for both APKs.

## Download APK from GitHub CI / pre-release

1. See [INSTALL_VI.md](INSTALL_VI.md) for Android permissions and usage.
2. GitHub **Releases** has pre-release APKs only after a successful explicit `release(android):` commit on this branch. Download the **chosen variant** (`LandscapeBrightnessLite-v0.2.0-alpha.apk` or `TikTokLandscapeAssistant-Full-v0.2.0-alpha.apk`) plus `SHA256SUMS.txt` from the same release and verify checksum if possible.
3. If the pre-release has not been published, go to **Actions → TikTok Landscape Android APK → a successful run for this branch → Artifacts → tiktok-landscape-assistant-debug-apk**. The artifact ZIP contains the APK and checksum.
4. Each CI APK uses a runner-generated debug signing key: installing a different build over an older one may require uninstalling the older version first.

## Build locally (requires network for first-time Android dependencies)

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

Run pure-Java decision tests: `sh verification/test-end-detector.sh`. GitHub CI verifies source tests, Android build, debug signing and artifact checksums on its exact commit. These build checks **do not validate playback/auto-next behavior on a physical TikTok device**.

## CI provenance

Workflow: `.github/workflows/tiktok-landscape-android.yml` on the feature branch. It uses JDK 17, Gradle 8.9, Android SDK 35, pure-Java loop detection tests, `assembleDebug`, APK integrity/checksum, and upload-artifact. For status readback it publishes the exact commit SHA in GitHub commit statuses. CI is allowed for this repository; quota limits from unrelated GitHub organizations must not be assumed here.
