# Beginner's Guide: Opening and Running Waffle's Nine Lives

This guide assumes you have never done this before. Follow it top to bottom.
It explains exactly what to click, what you should see, and what to do if
something goes wrong.

**Why this guide is needed at all:** the project was built and checked very
thoroughly in a sandbox that could compile all the Kotlin code against the
real Android API and link all the resources — but that sandbox could not
reach Google's or Gradle's own servers, so it could never run the *last* step
(`./gradlew assembleDebug`) that actually produces an installable `.apk` file.
That step needs to happen once, on your computer, inside Android Studio. See
`WAFFLE_MVP_PLAN.md` for the full detail on what was and wasn't already
verified.

---

## Step 1 — Unzip the project

1. Find the file you downloaded, `waffles-nine-lives.zip`.
2. Right-click it and choose **Extract All** (Windows) or double-click it
   (Mac) to unzip it.
3. Put the resulting `waffles-nine-lives` folder somewhere easy to find, like
   your Desktop or a `Projects` folder. **Don't** leave it inside your
   Downloads folder long-term, and don't put it inside another zip.

## Step 2 — Open the project in Android Studio

1. Open **Android Studio**.
2. On the welcome screen, click **Open** (if a previous project opens
   automatically instead, go to **File → Open**).
3. Browse to and select the **`waffles-nine-lives`** folder itself (the one
   containing `build.gradle.kts` and the `app` folder) — not a file inside it.
4. Click **OK**.

## Step 3 — Let Gradle sync (this is the important part)

After opening, Android Studio will show a progress bar at the bottom labeled
something like "Gradle Sync" or "Syncing project with Gradle files...". This
is Android Studio downloading the Android SDK pieces, the Android Gradle
Plugin, and Gradle itself — all from servers that **were blocked in the
sandbox this project was built in, but should work fine on your own
computer** as long as you have a normal internet connection.

- This can take anywhere from 30 seconds to several minutes the first time.
- **Just wait for it to finish.** Don't close the window or click around
  while it's running.
- You'll know it's done when the progress bar disappears and the project
  files appear in the left-hand panel.

**If sync fails with a red error banner:** read the troubleshooting section
near the bottom of this guide before doing anything else.

## Step 4 — Run the unit tests (optional but recommended)

This confirms the game logic really works on your machine, the same way it
was already confirmed in the sandbox (25/25 passing).

1. In the left-hand project panel, navigate to:
   `app > src > test > java > com.waffle.ninelives`
2. Right-click the `com.waffle.ninelives` folder (the one containing
   `GameWorldTest.kt`, `LifeSystemTest.kt`, `RoomTest.kt`).
3. Choose **Run 'Tests in com.waffle.ninelives'**.
4. A panel opens at the bottom showing each test. You should see **25 tests,
   all green** with a message like "Tests passed: 25 of 25".

If any test fails here but didn't in the sandbox, something about your local
Kotlin/Gradle version differs — copy the red error text and share it (see
"If something goes wrong" below).

## Step 5 — Run the app

You need either a **physical Android phone** or an **emulator** (a virtual
phone that runs on your computer).

### Option A: Use a physical Android phone (usually easier)

1. On your phone, go to **Settings → About phone**, find **Build number**,
   and tap it 7 times. This unlocks **Developer options**.
2. Go to **Settings → Developer options** and turn on **USB debugging**.
3. Plug your phone into your computer with a USB cable.
4. A popup may appear on your phone asking to allow USB debugging from this
   computer — tap **Allow**.
5. In Android Studio, look at the toolbar near the top. There's a dropdown
   (it might say "app" and a device name) — click it and select your phone.
6. Click the green **▶ Run** button (or press Shift+F10).

### Option B: Use an emulator (a virtual phone)

1. In Android Studio's toolbar, open **Tools → Device Manager** (or click the
   phone-shaped icon in the toolbar).
2. Click **Create device** (or **+**).
3. Pick any modern phone (e.g. "Pixel 7") and click **Next**.
4. Pick a system image — any recent one (e.g. "UpsideDownCake"/API 34) — and
   click **Download** next to it if needed, then **Next**, then **Finish**.
5. Back in the main toolbar, select your new virtual device from the device
   dropdown.
6. Click the green **▶ Run** button (or press Shift+F10).

### What you should see

The app builds (watch the progress bar at the bottom — this is the real
`assembleDebug` step), installs, and launches. You should see:

1. A title screen with Waffle's face, the title, and "TAP TO START".
2. Tapping starts the game: a joystick on the lower-left, an attack button
   on the lower-right, a pause circle top-right.
3. Walking into the neighboring room, fighting dogs, finding a key, opening
   a locked gate, finding the silver bell, and reaching Duke the boss.
4. Losing all your hearts the first time should trigger a "LIFE LOST" screen,
   then a transformation screen introducing **Spirit Pounce**, after which a
   new ability button appears next to Attack.

If you see all of that, **the build succeeded for real** — this guide's job
is done, and you have a genuinely working build of the MVP.

## Step 6 — Where to find the `.apk` file afterward

Once **Step 5** has run successfully at least once, Android Studio will have
created a real `.apk` file at:

```
waffles-nine-lives/app/build/outputs/apk/debug/app-debug.apk
```

You can copy this file to another Android phone and install it directly
(you may need to allow "install from unknown sources" the first time).

---

## If something goes wrong

Android Studio is usually good about explaining errors. Here's what to do:

1. **Read the red text.** Gradle/Android Studio error messages usually say
   exactly what's wrong (a missing SDK component, a version mismatch, etc.),
   and often show a blue/yellow clickable link to fix it automatically (e.g.
   "Install missing platform and sync project").
2. **Try the suggested quick fix first.** Click any blue underlined text in
   the error message — these are usually safe, automatic fixes.
3. **If you're stuck, copy the full error text** (there's usually a "Copy"
   button, or you can select and copy it by hand) and share it with Claude —
   either in **Claude Code** opened on this same project folder (best, since
   it can edit files directly and re-run the build itself), or in a new chat
   with this project's files attached. Paste the exact error text; that's
   usually all that's needed to fix it directly.
4. **Common first-time hiccups** (not bugs in this project, just normal
   first-run Android Studio behavior):
   - *"SDK location not found"* → Android Studio will prompt you to install
     the Android SDK automatically the very first time you open any project;
     just follow its prompts.
   - *A specific "platform 34 not installed" message* → **Tools → SDK
     Manager**, check the box for **Android 14.0 ("UpsideDownCake")**, click
     **Apply**.
   - *Very first Gradle sync takes a long time or seems stuck* → this is
     normal; it's downloading several hundred MB the first time only. Check
     your internet connection and just let it run.

## A note on the `references/` folder

The project folder also contains a `references/` directory with the original
research material used while designing the game's systems (not shipped in
the app itself, and not needed to build or run it). You can ignore or delete
it — it has no effect on the build.
