# Rotate 360

Simple Android app that forces the screen to rotate in all 4 directions
(0°, 90°, 180°, 270°) — including upside-down portrait, which most phones block.

## How it works
An invisible 0x0 overlay window requests an orientation (`FULL_SENSOR` etc.),
and the system follows it for every app. It needs the "Display over other apps"
permission. No root required.

## Modes
- Auto 360° (all 4 directions, ignores the system auto-rotate lock)
- Portrait, Landscape, Reverse portrait, Reverse landscape (fixed)
- Stop (back to system default)

## Build APK on GitHub
1. Push this project to a GitHub repo (keep the `.github/workflows` folder).
2. Open the Actions tab -> `Build Android APK` -> Run workflow.
3. Download the `Rotate360-debug-apk` artifact, extract it, install `app-debug.apk`.
