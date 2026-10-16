# Privacy policy — Landscape Episode Assistant (experimental)

**Last updated:** 17 October 2026.

This app is an independent Android companion app, **not affiliated with or endorsed by TikTok**. It consists of two separate applications:

- **Landscape Brightness Lite** (`dev.lyonpro.tiktoklandscape.lite`): manually toggles device screen brightness using `WRITE_SETTINGS`, after your explicit permission. It has no Accessibility Service, no TikTok monitoring, and no Auto Next.
- **Landscape Episode Assistant Full** (`dev.lyonpro.tiktoklandscape`): when enabled, uses Android Accessibility Service to inspect *visible* player controls, seek progress, and episode labels **only while TikTok is in landscape** and to attempt one clearly defined action: clicking a visible Next Episode control after video loops. Optional swiping or timer requires separate user opt-in. `WRITE_SETTINGS` restores the previous brightness level/mode.

## Data access, storage, and sharing

- The Full Accessibility service can technically observe text and control descriptions of the active TikTok screen while permitted by Android. This could include sensitive content if shown in the same player view. The app **does not save or upload that text**.
- The app stores only local configuration, whether explicit consent was given, brightness settings before override, and **small diagnostic flags** (whether progress / Next are exposed; optional current and total episode numbers; the most recent local action and timestamp).
- No screenshots, screen recording, web connections, personal identifiers, login credentials, contacts, microphone, analytics, advertising, telemetry, or remote server upload is implemented.
- Configuration and diagnostic flags are stored in private Android app storage (`SharedPreferences`) and removed if the app is uninstalled or app data is cleared. No data is sold or shared with third parties.

## Your controls

- All automation is off until explicit consent plus activation; Android Accessibility permission is granted separately through system Settings.
- Turn off automation using app controls or the Quick Settings tile. Revoke Accessibility in Android Settings at any time; remove `Modify system settings` permission to stop further brightness writes.
- The app does not modify TikTok or its internal files; it cannot guarantee TikTok exposes enough controls, that it will navigate to the correct episode, that it will detect the last episode, or that TikTok permits third-party automation.
- Users retain control over installed apps and permissions.

This is a source-available experimental build. For questions or security reports, use the repository's GitHub Issues at https://github.com/leminhphap-improvement/practices/issues.
