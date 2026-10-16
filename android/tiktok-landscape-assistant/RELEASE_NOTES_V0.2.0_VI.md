# v0.2.0-alpha — Android Play Protect hardening

## Reported
User-supplied Android screenshot: Google Play Protect actively blocked installation of `v0.1.0-alpha.7`, explaining that the app may request sensitive data access. Google documents this as a possible automatic block for internet-sideloaded APKs declaring Accessibility.

## Changed
- Added **Brightness Lite** as a standalone app package `dev.lyonpro.tiktoklandscape.lite`, *without* AccessibilityService, screen-reading, SMS, network, or TikTok control. This variant offers manual Quick Settings brightness-on/restore and asks only to modify system settings.
- **Full Auto Next alpha** remains a separate APK/package `dev.lyonpro.tiktoklandscape`; its Accessibility Service now scopes events to TikTok packages. UI includes explicit prominent disclosure and an affirmative consent gate before enabling or requesting Accessibility.
- CI compiles both APKs and statically rejects an Accessibility Service/Internet/SMS in Lite manifest. Uploads SHA256 checksums and release assets.
- Documentation explains **DO NOT disable Play Protect** and honest feature boundaries.

## Security and product gates
- Existing Java playback-loop unit tests: CI expected; must check exact release SHA.
- Android build and APK integrity: CI expected; must check exact release SHA.
- Lite manifest privilege checks: CI expected; must check exact release SHA.
- APK installation through Play Protect on user's actual Android: **NOT VERIFIED**.
- TikTok landscape auto Next, episode ordering and end-of-series stop: **NOT VERIFIED**.
- Full Auto Next through official Google Play distribution: **NOT PUBLISHED / NO APPROVAL**.

## Installation
See [SECURITY_INSTALL_VI.md](SECURITY_INSTALL_VI.md). Download the **Lite** APK if your immediate priority is the Quick Settings brightness tile. It cannot auto-scroll. The **Full** APK still requires Accessibility and may remain blocked by Play Protect when sideloaded. Use an official policy-compliant distribution/review or Play Protect appeal for the Full app, not workarounds.

## Official references
- https://developers.google.com/android/play-protect/warning-dev-guidance
- https://support.google.com/googleplay/android-developer/answer/10964491
