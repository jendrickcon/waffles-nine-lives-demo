# Waffle's Nine Lives — MVP Plan & Verification Report

This document records what was built, in what order, how it was verified, and
exactly what could and could not be checked in the sandbox this was built in.
It is meant to be read alongside `WAFFLE_CHANGELOG.md` (a plain change log) and
`WAFFLE_BEGINNER_GUIDE.md` (your exact next steps in Android Studio).

## 1. Environment this was built in

The code was written and tested in a Linux sandbox with:

- A real Kotlin compiler (`kotlinc`, 1.9.24) — available and used throughout.
- Java 21.
- **No Android Studio, no Android SDK, no Gradle installation.**
- An outbound network allow-list that includes GitHub, PyPI, npm, and crates.io,
  but **not** `dl.google.com`, `maven.google.com`, `services.gradle.org`, or
  `storage.googleapis.com` — the actual hosts that serve the Android SDK, the
  Android Gradle Plugin (AGP), build-tools, and Gradle itself.

This matters because it shapes everything below: real game-logic testing was
fully possible; a complete, real `./gradlew assembleDebug` was not.

## 2. What was built, in order

1. **Gradle project skeleton** — `settings.gradle.kts`, root and app
   `build.gradle.kts`, `gradle.properties`, `.gitignore`, and a real Gradle
   wrapper jar (fetched from GitHub's mirror of the Gradle source, since
   `services.gradle.org` itself was unreachable). `compileSdk`/`targetSdk` 34,
   `minSdk` 24, Kotlin 1.9.24, AGP 8.5.2, Java 17 target.
2. **Manifest and resources** — a single-activity, landscape-locked,
   fullscreen app; a hand-drawn vector app icon (Waffle's face); strings and a
   theme.
3. **Pure game logic** (`app/src/main/java/com/waffle/ninelives/game/` and
   `progression/`) — deliberately written with **no Android imports** so it can
   be compiled and unit-tested with plain `kotlinc`, independent of any Android
   tooling:
   - `GameConfig.kt` — every tunable number (speeds, cooldowns, timings) in one place.
   - `Palette.kt` — the full original color palette.
   - `Entities.kt` — `Player`, `Enemy`, `EnemyType` (Dog/Crow/Boss tuning table),
     `Projectile`, a fixed-size `Particles` ring buffer.
   - `Collision.kt` — axis-separated box-vs-tile collision, sub-stepped so fast
     movement (Spirit Pounce) can't skip through walls.
   - `Rooms.kt` — five hand-authored room layouts, a `RoomFactory` that builds
     the fixed route **Start → Kill-room → Key-room → Reward-room → Boss**, with
     the three middle layouts shuffled per run by a seeded RNG.
   - `LifeSystem.kt` (in `progression/`) — the nine-lives table (`RunState`,
     `LifeTable`), with lives 8 filled in (Spirit Pounce) and lives 7 down to 1
     left as explicit `TODO` placeholders for future upgrades.
   - `GameWorld.kt` — the whole simulation: player movement, claw swipe, Spirit
     Pounce, dog/boss chase-windup-lunge-recover AI, crow hover-strafe-aim-throw
     AI, projectiles, pickups (heart/key/reward/exit), gate locking/unlocking,
     room transitions, the life-loss → transform → resume sequence, and a small
     debug command queue.
4. **Android-facing layer** (`ui/` and `MainActivity.kt`):
   - `InputState.kt` — a thread-safe bridge between the UI thread (touch/key
     events) and the game thread (edge-triggered buttons, a joystick vector).
   - `TouchControls.kt` — virtual joystick with dead zone, attack/ability/pause
     buttons tracked by pointer ID (so multiple fingers work correctly), a
     pause menu, and a hidden debug panel (hold Pause ~0.8s).
   - `GameView.kt` — a `SurfaceView` that owns exactly one game thread, started
     only when the surface exists **and** the activity is resumed, with a
     fixed 60Hz step and a clamp on huge frame gaps (e.g. after the app was
     backgrounded).
   - `Renderer.kt` — all drawing. Every visual (Waffle, the dog, the crow, the
     boss, bushes, fences, hearts, the key, the silver bell reward, particles,
     the HUD, every overlay screen) is built from basic Canvas shapes — no
     image assets, nothing copied from any other game.
   - `MainActivity.kt` — hosts the view, keeps the screen on, hides system
     bars, forwards lifecycle pause/resume, and adds keyboard controls for
     convenient testing in the emulator or on a keyboard-equipped device.
5. **Unit tests** (`app/src/test/.../*.kt`) — 25 tests covering the life table,
   room generation/reachability, collision against walls, the full life-loss →
   transform → resume sequence, Spirit Pounce unlocking and behaving correctly,
   edge-triggered attacks, pause, room clearing/door locking, the key-and-gate
   puzzle, the reward pickup, enemy reset on death, bounded projectile/particle
   pools, and the boss → exit → victory sequence.

## 3. Verification actually performed, and what it proves

This is the part to read carefully, because "tested" can mean different things.

### 3.1 Pure game logic — compiled and unit-tested for real

All of `game/` and `progression/` was compiled directly with `kotlinc` (no
Android dependency needed, since this code intentionally avoids Android
classes) and run against a small hand-written JUnit-compatible test runner
(since real JUnit/Gradle wasn't available). **All 25 tests passed.** A
separate scripted "bot" was also run through 30 different seeded runs, walking
toward the nearest threat or pickup and attacking in range; it reached victory
in 26/30 runs and never hit an unintended soft-lock in the room/door logic
itself (the 4 timeouts were a limitation of the simple bot's pathing around
obstacle layouts, not a game bug — confirmed by manually walking the same path
frame-by-frame).

### 3.2 Android-facing code — compiled against the real Android API, for real

Although Gradle and the Android SDK couldn't be downloaded, a mirror of the
real `android.jar` for API 34 was obtained from a GitHub-hosted repository
(`github.com/Reginer/aosp-android-jar`, a long-standing public mirror of
Google's own platform stub jars). Every Android-facing Kotlin file —
`MainActivity.kt`, `GameView.kt`, `TouchControls.kt`, `Renderer.kt` — was
compiled directly against this real `android.jar` with `kotlinc`:

```
kotlinc -jvm-target 17 -classpath android.jar  <all source files>  -d out
```

**Result: exit code 0, zero errors, zero warnings**, and a `.class` file was
produced for every class, including `MainActivity`, `GameView`, its inner
`GameThread`, `Renderer`, and `TouchControls`. This is genuine confirmation
that the code calls real Android APIs (`SurfaceView`, `Canvas`, `MotionEvent`,
`Activity`, `WindowManager`, etc.) correctly — it is not a guess or a "this
looks right" judgment.

### 3.3 Resources and manifest — compiled and linked against real Android resources

The legacy `aapt` tool (v1, from Ubuntu's own package archive — not the SDK
Gradle uses, which is `aapt2`, but a real resource compiler from the same
Android project) was installed from `archive.ubuntu.com`, along with the real
`android-framework-res` package (the actual Android framework resource APK).
Using these, the project's manifest and every resource file
(`strings.xml`, `themes.xml`, the vector `ic_launcher.xml`) were compiled and
linked for real:

```
aapt package -M AndroidManifest.xml -S res -I framework-res.apk -F resources.ap_ -J gen
```

**Result: exit code 0**, a real `resources.ap_` package was produced, and a
correct `R.java` was generated:

```java
public final class R {
    public static final class drawable { public static final int ic_launcher = ...; }
    public static final class string   { public static final int app_name = ...; }
    public static final class style    { public static final int Theme_Waffle = ...; }
}
```

These IDs match exactly what the manifest and code reference
(`@drawable/ic_launcher`, `@string/app_name`, `@style/Theme.Waffle`), which
confirms the resources are structurally valid. (One harmless note: this older
`aapt` tool expects a `package=` attribute directly on `<manifest>`, which is
the *old* style. The project correctly uses the *modern* style — `namespace`
in `app/build.gradle.kts` — which is what current Android Studio/AGP expects
and requires. A temporary, throwaway copy of the manifest with `package=`
added was used only to satisfy this older validation tool; the real project
file was never changed to the old style.)

### 3.4 What was *not* and could not be verified here

- **No real Gradle build ran.** `services.gradle.org` (Gradle's own
  distribution server) returned a network-level `host_not_allowed` error from
  the sandbox's proxy — Gradle itself could not be downloaded, so `./gradlew`
  could not run at all, in either `testDebugUnitTest` or `assembleDebug` form.
- **No Android Gradle Plugin (AGP) ran.** AGP is fetched from
  `maven.google.com` (via Gradle's `google()` repository), which was also
  unreachable.
- **No `d8`/dex compiler was available**, so Kotlin `.class` files were never
  converted to `.dex`, and **no `.apk` file of any kind was produced.** An
  attempt was made to install a dexer from Ubuntu's package archive
  (`google-android-build-tools-32.0.0-installer`); it failed for the same
  reason — its post-install step tries to download the real build-tools zip
  from `dl.google.com`, which returned `403 Forbidden` from the sandbox's
  proxy. A search for a GitHub-hosted mirror of `d8`/R8 came up empty: every
  public mirror found (`github.com/Col-E/r8`, `github.com/johnsonlee/r8`)
  redistributes the same prebuilt jars from Google's own
  `storage.googleapis.com` bucket, which is also outside the sandbox's
  allow-list.
- **No emulator and no physical device were available**, so no on-device or
  in-emulator smoke test was possible. There is no video, screenshot, or
  runtime log of the app actually running on Android.

**Bottom line:** every part of this MVP that could be checked without a full
Android SDK and Gradle toolchain *was* checked, with real tools and real
pass/fail results, not assumptions. The one remaining step — a real
`./gradlew assembleDebug` (and ideally `testDebugUnitTest`) inside Android
Studio, which has its own working internet access to Google's and Gradle's
servers — has not been done, and this document does not claim otherwise.

## 4. Likely outcomes when you run the real build

Given the strength of the checks above, the most likely scenarios are:

- **Most likely:** `./gradlew assembleDebug` and `testDebugUnitTest` succeed
  with little or no change needed. The logic and Android API usage are already
  confirmed correct; Gradle/AGP mainly add resource packaging, dexing, and
  signing on top of code that's already been checked.
- **Possible:** a small Gradle/AGP configuration mismatch (for example, if
  your installed Android Studio ships a different default AGP/Gradle pairing
  than the versions pinned here). These are usually one-line fixes that
  Android Studio itself suggests via a yellow "Fix" banner.
- **Unlikely but possible:** something version-specific that only a real AGP
  build would catch (e.g. a resource-merging rule `aapt2` enforces but `aapt`
  v1 doesn't). `WAFFLE_BEGINNER_GUIDE.md` explains what to do if Android
  Studio reports an error: copy the exact red error text back to Claude (in
  Claude Code, or a new chat with this project re-attached) and it can be
  fixed directly, the same way the issues already found in this session were
  fixed.

## 5. Design notes worth knowing

- **Fixed-step simulation.** `GameWorld.update(dt)` is always called with a
  fixed `1/60s` step from `GameView`'s own thread, with frame-time clamping
  and a cap of 5 catch-up steps per frame, so the game can never "explode"
  after the app is paused and resumed.
- **Everything bounded.** Projectiles and particles use fixed-size pools
  (`MAX_PROJECTILES`, `MAX_PARTICLES`) with oldest-recycling, so long play
  sessions can't leak memory or slow down.
- **Seeded rooms.** `RoomFactory.buildRoute(rng)` takes a `Random`, so the same
  seed always produces the same room order — useful for testing and later for
  daily-seed-style features, without being required for the MVP.
- **The life table is forward-compatible.** `LifeTable` in `LifeSystem.kt`
  already defines the *shape* of lives 7 through 1 (fixed / choose-one-of-two /
  choose-one-of-three), with empty `options` lists and `TODO` comments. Adding
  a new upgrade later is a matter of filling in that list — no other file
  needs to change.
