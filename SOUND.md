# Changing the alert sound

The app plays `app/src/main/res/raw/ding.wav`. A short placeholder beep ships
with the build so "Test alert" works immediately.

To use your own ding:

1. Put your file at `app/src/main/res/raw/` named exactly `ding` with a
   supported extension — `ding.mp3`, `ding.wav`, or `ding.ogg`.
2. Delete the old `ding.wav` if you're adding `ding.mp3` (only one `ding.*`
   may exist, or the build fails on a duplicate resource).
3. Keep the name lowercase with no spaces or dashes — Android resource names
   allow only lowercase letters, digits, and underscores.
4. Rebuild: `./gradlew assembleDebug`.

Any short clip works; 1–2 seconds is ideal.
