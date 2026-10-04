# Changelog

All entries are from the single session that created this MVP. Newest at top.

## Verification pass (after the free-message limit interruption)

- Confirmed the workspace was fully intact after the interruption — nothing
  was lost or needed to be recreated.
- Confirmed `Renderer.kt` (691 lines) was already complete and well-formed.
- Obtained a real Android API 34 `android.jar` from a GitHub-hosted mirror
  (`github.com/Reginer/aosp-android-jar`) and compiled every Android-facing
  Kotlin file against it: **0 errors, 0 warnings**, `.class` files produced for
  `MainActivity`, `GameView` (+ its `GameThread`), `TouchControls`, `Renderer`.
- Re-ran all 25 pure-Kotlin unit tests to confirm nothing had regressed: **25
  passed, 0 failed.**
- Installed `aapt` (v1) and the real `android-framework-res` package from
  Ubuntu's own archive, and used them to really compile and link the app's
  manifest and resources against real Android framework resources: **0
  errors**, correct `R.java` generated.
- Attempted a full dex/APK build. Confirmed, with real error output, that:
  - `services.gradle.org` (Gradle's distribution server) is blocked by the
    sandbox's network proxy (`host_not_allowed`).
  - `dl.google.com` (the Android SDK/build-tools server) is also blocked
    (`403 Forbidden`), confirmed by watching an apt-based build-tools
    installer fail at exactly that download step.
  - No usable prebuilt `d8`/dex compiler exists on any host the sandbox can
    reach; the two GitHub mirrors found both redistribute the same jar from
    Google's blocked cloud storage bucket.
  - **Conclusion: no `.apk` could be produced in this sandbox.** This is a
    sandbox networking limitation, not a problem with the code.
- Wrote this file, `README.md`, `WAFFLE_MVP_PLAN.md`, and
  `WAFFLE_BEGINNER_GUIDE.md`.
- Packaged the full project as a zip for download.

## Initial build

- Set up the Gradle project (`settings.gradle.kts`, root and app
  `build.gradle.kts`, `gradle.properties`, `.gitignore`), fetched a real
  Gradle wrapper jar from GitHub, and wrote the manifest, strings, theme, and
  a hand-drawn vector app icon.
- Wrote the nine-lives progression system (`LifeSystem.kt`): `RunState`,
  the `LifeTable` with lives 8 (Spirit Pounce) filled in and 7–1 stubbed for
  later.
- Wrote the pure game-logic layer: tunable config, color palette, entity
  types (player/enemy/projectile/particles), five room layouts plus a
  seeded room-route factory (Start → Kill → Key → Reward → Boss), and
  axis-separated tile collision.
- Wrote `GameWorld.kt`: movement, claw swipe, Spirit Pounce, dog/boss AI
  (chase → windup → lunge → recover), crow AI (hover/strafe/aim/throw),
  projectiles, pickups, gate locking, room transitions, and the life-loss →
  transform → resume sequence.
- Compiled the pure logic with `kotlinc` directly (no Android dependency) —
  clean compile.
- Wrote 25 unit tests covering lives, rooms, collision, combat, pickups,
  pause, and the full death/transform/victory flow. Found and fixed 3 tests
  that had timing mistakes of their own (not game bugs) — all 25 pass.
- Ran a simple scripted "bot" through 30 seeded runs to sanity-check that a
  run can actually be completed end-to-end; found and fixed a bug in the
  *bot's* door-targeting math (not a game bug) and reached 26/30 wins with
  the rest being bot-pathing limitations, not soft-locks.
- Wrote the Android-facing layer: `InputState.kt` (thread-safe touch/keyboard
  bridge), `TouchControls.kt` (joystick, buttons, pause menu, debug panel),
  `GameView.kt` (SurfaceView + fixed-step game thread), `Renderer.kt` (all
  original shape-based art and UI), and `MainActivity.kt` (hosts the view,
  lifecycle, keyboard dev controls).
- This is the point the session was interrupted by the free-message limit;
  everything above was already on disk and has been confirmed intact.
