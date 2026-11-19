# Poll Alert

An Android app that rings and vibrates when an **iClicker poll opens**, so you
never miss the join window while you're in class. **You tap "Join" yourself** —
the app only alerts you. It does not tap, respond, or interact with iClicker.

## What it does

- Runs a lightweight accessibility service that watches on-screen text while the
  iClicker app is in front.
- When a join prompt appears, it plays a sound and vibrates once (with a 20s
  cooldown so it won't nag on the same poll).
- Sound and vibration are each togglable, and there's a "Test alert" button.

## Install

1. Download `PollAlert-debug.apk` (in this repo / release).
2. Copy it to your Android phone and open it.
3. Allow "install from unknown sources" if prompted (this is a debug build, not
   from the Play Store).

## Set up (one time)

1. Open **Poll Alert**.
2. Tap **Open accessibility settings**.
3. Find **Poll Alert watcher** and turn it on.
4. Back in the app, the status should read **✅ Watching for polls**.
5. Tap **Test alert** to confirm you can hear/feel it.

## Use in class

- Keep the **iClicker Student app open** (foreground) during class.
- When a poll opens, your phone rings/vibrates — look up and tap **Join**.

## Tuning detection

If your iClicker screen uses different button text, edit `POLL_KEYWORDS` in
`app/src/main/java/com/example/pollalert/PollWatchService.kt`. The defaults are
`Join`, `Join Poll`, `Join Activity`. You can also widen `TARGET_PACKAGES` if
your iClicker app's package name differs.

## Changing the sound

See [SOUND.md](SOUND.md). A short placeholder beep ships with the build; replace
`app/src/main/res/raw/ding.wav` with your own.

## Build from source

Requires JDK 17 and the Android SDK (platform 34, build-tools 34.0.0).

```bash
./gradlew assembleDebug
# output: app/build/outputs/apk/debug/app-debug.apk
```

## Design note

This is deliberately an **alert**, not an auto-clicker. It keeps the actual poll
response in your hands, so your participation reflects you actually being there
and choosing to join.
