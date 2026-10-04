# Waffle's Nine Lives

An original top-down 2D action roguelike for Android. Waffle, a fluffy orange cat,
got lost far from home and has to fight, pounce, and explore his way back —
with nine real lives standing between him and true death.

This is an **MVP (minimum viable product)**: one run, five rooms, two enemy types,
one boss, and the first of Waffle's nine-lives transformations (Spirit Pounce).

---

## What's in this project

```
waffles-nine-lives/
├── app/
│   ├── build.gradle.kts              Android app module config
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/waffle/ninelives/
│       │   │   ├── MainActivity.kt        the one screen; keyboard dev controls
│       │   │   ├── game/                  pure game logic (no Android classes)
│       │   │   │   ├── GameConfig.kt          every tunable number
│       │   │   │   ├── Palette.kt             all colors
│       │   │   │   ├── Entities.kt            Player, Enemy, Projectile, Particles
│       │   │   │   ├── Rooms.kt               room layouts + the 5-room route
│       │   │   │   ├── Collision.kt           box-vs-tile collision
│       │   │   │   ├── InputState.kt          touch/keyboard → game bridge
│       │   │   │   └── GameWorld.kt           the simulation itself
│       │   │   ├── progression/
│       │   │   │   └── LifeSystem.kt          the nine-lives table and RunState
│       │   │   └── ui/
│       │   │       ├── GameView.kt            SurfaceView + game thread
│       │   │       ├── TouchControls.kt        joystick, buttons, pause menu
│       │   │       └── Renderer.kt             all drawing (original shape-art)
│       │   └── res/                       strings, theme, app icon
│       └── test/java/com/waffle/ninelives/  25 unit tests (see below)
├── build.gradle.kts, settings.gradle.kts, gradle.properties, gradlew(.bat)
├── WAFFLE_MVP_PLAN.md            what was built, in what order, and why
├── WAFFLE_CHANGELOG.md           a plain log of what changed in this session
├── WAFFLE_BEGINNER_GUIDE.md      exact Android Studio steps, written for you
└── references/                   the original research material (not shipped in the app)
```

Nothing in this project copies characters, art, music, code, or room/enemy designs
from any other game. Everything — art (drawn with shapes in code), layouts, names,
and systems — is original to Waffle's Nine Lives.

## The nine lives, in brief

Waffle starts a run with **9 lives** and some starting health (hearts). Losing all
his health costs **one life** and resets his health and the room's living enemies,
but keeps his run progress (keys, unlocked upgrades, max health). Reaching **0
lives** is true death and restarts the run from 9.

In this MVP, arriving at **8 lives** (i.e. losing the first life) permanently
unlocks **Spirit Pounce** — a short invulnerable dash that damages and knocks back
anything Waffle passes through. The life table in `LifeSystem.kt` already has
placeholders for lives 7 down to 1, ready for future upgrades (fixed, choose-one-
of-two, choose-one-of-three) without changing any other code.

## How to open this (short version)

See **WAFFLE_BEGINNER_GUIDE.md** for the exact, step-by-step version. Short version:
open the `waffles-nine-lives` folder in Android Studio, let it sync Gradle, plug in
a phone (or start an emulator), and click Run. Full detail, including what to do if
something goes wrong, is in that guide.

## Important: what has and hasn't been verified

This project was built and heavily checked in a Linux sandbox **without Android
Studio, the Android SDK, or Gradle's own download servers available** (the network
in that sandbox blocks Google's and Gradle's hosts). That means:

- ✅ All **25 unit tests** for the game logic (lives, rooms, collisions, combat,
  pickups, state transitions) really compiled and ran, with real pass/fail results.
- ✅ Every Android-facing Kotlin file (`MainActivity`, `GameView`, `TouchControls`,
  `Renderer`) was **compiled against a real Android API 34 `android.jar`** with
  zero errors and zero warnings — genuine proof the code is syntactically and
  type-correct against the real Android API surface.
- ✅ The manifest and all resource XML files (strings, theme, the vector app icon)
  were **compiled and linked with the real `aapt` resource tool** against real
  Android framework resources, with zero errors, producing a correct `R.java`.
- ❌ **`./gradlew assembleDebug` has not been run, and no `.apk` file exists.**
  Producing a real, installable APK needs Gradle's own distribution and the
  Android Gradle Plugin from Google's Maven repository, and both of those hosts
  were unreachable in the sandbox. This is explained in full, with exact error
  output, in `WAFFLE_MVP_PLAN.md`.

**You will need to run the real build yourself in Android Studio** — this is a
normal "File → Open" and "Run" away, and `WAFFLE_BEGINNER_GUIDE.md` walks through
it exactly. Given how much of the code has already been verified against the real
Android API and resource tools, the most likely remaining issues (if any) are
small Gradle/AGP configuration details, not logic bugs.

## Controls

**Touch (on a phone):** left-side joystick to move, bottom-right round button to
attack (claw swipe), smaller button above it for Spirit Pounce (appears once
unlocked), top-right circle to pause. Hold the pause button for about a second to
reveal a developer debug panel (damage self, jump to a life count, reset run,
toggle collision boxes).

**Keyboard (useful in the Android Studio emulator):** WASD or arrow keys to move,
Space or J to claw swipe, K for Spirit Pounce, P or Esc to pause, Enter to
start/continue past a message screen.
