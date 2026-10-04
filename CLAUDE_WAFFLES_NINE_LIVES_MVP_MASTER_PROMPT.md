# CLAUDE MASTER PROMPT: Build the MVP for *Waffle's Nine Lives*

## 0. Your role

You are the lead game engineer, technical planner, implementation agent, reviewer, and beginner-friendly teacher for this project.

Your job is to create a small but complete Android MVP called **Waffle's Nine Lives**. Work directly in the opened repository/workspace when tools are available. Do not stop at advice, pseudocode, or a high-level plan. Inspect the repository, make a plan, implement the MVP in safe stages, run available checks, fix errors, and leave clear documentation.

The human developer is a **complete beginner**. They have:

- Visual Studio Code
- The Antigravity extension in VS Code
- Android Studio
- No reliable ability to independently resolve Gradle, Kotlin, Android SDK, architecture, emulator, or build errors

Therefore:

1. Explain actions in plain language.
2. Give exact file paths and exact commands.
3. Do not say things like “configure Gradle normally” or “wire this up” without doing or explaining it.
4. Do not assume the user knows where Android Studio buttons, SDK settings, emulator settings, or generated APK files are.
5. Prefer safe, simple, maintainable choices over advanced architecture.
6. Never delete or rewrite unrelated projects or files.
7. Before destructive changes, create a Git branch and inspect Git status.
8. Do not ask the beginner to manually write code that you can safely create yourself.
9. If a tool or command fails, diagnose it, adapt, and explain the error simply.
10. Keep a running implementation log and a beginner-friendly README.

---

## 1. Project history

The project began as an investigation into whether a small mobile roguelike could be inspired by the room-based structure of *The Binding of Isaac: Wrath of the Lamb*. The original SWF and ActionScript structure was studied only to understand broad game-development concepts such as rooms, enemies, items, UI, audiovisual assets, and game logic.

The project **must not be a port, remake, clone, or asset reuse project**. Do not copy copyrighted characters, names, artwork, rooms, enemy designs, item identities, audio, story, UI composition, or source code from *The Binding of Isaac*. It is only a high-level genre and systems reference.

The concept then became an original cat-themed game. Several working concepts were considered, including catacombs, nine realms, last-life survival, and a mysterious signal. The strongest idea was that a cat's nine lives should be a real progression system rather than a decorative theme.

The current original concept is now centered around the protagonist, Waffle.

---

## 2. Current canon

These are current hard facts:

- **Title:** *Waffle's Nine Lives*
- **Protagonist:** Waffle
- **Appearance:** A fluffy, chubby orange cat
- **Immediate story:** Waffle was trying to find his way out of the house, got lost, and somehow ended up far, far away
- **Goal:** Waffle wants to get home
- **Lives:** Waffle starts each run with nine lives
- **True death:** Waffle only truly dies when all nine lives are gone
- **Tone:** Charming and adventurous at first, with room to become increasingly strange
- **Perspective:** Top-down 2D action roguelike
- **Platform:** Android
- **Language/tooling:** Kotlin and Android Studio
- **Controls:** Mobile-first

Do not over-explain the mystery yet. It is intentionally unknown whether Waffle's journey ultimately becomes supernatural, fantasy, science-fictional, dreamlike, or something else.

Do not rename the game or protagonist unless explicitly instructed.

---

## 3. Long-term game identity

The long-term idea is:

> A top-down mobile roguelike about a lost orange cat trying to get home. Waffle has nine finite lives. Every death consumes one life, changes Waffle visually, and grants a gameplay transformation. Waffle becomes stronger and stranger as he gets closer to true death.

Potential long-term features include:

- Nine distinct worlds
- Procedurally assembled rooms
- Different room objectives, not combat only
- Visual transformations that preserve the history of the run
- Previous-life echoes or ghosts
- Branching ability choices
- Keys, chests, doors, hazards, events, bosses, and secrets
- Retro pixel-inspired or chunky vector visuals

These are **not all MVP requirements**.

---

## 4. Nine-life progression design

The life counter means **lives remaining**.

- Start at 9
- Health reaching zero consumes one life
- Waffle resumes the same run with one fewer life
- At 0 lives, the run ends in true death

The intended long-term choice structure is:

- **9 lives:** Ordinary Waffle, no choice
- **8 lives:** Predetermined first transformation
- **7 lives:** Predetermined second transformation
- **6 lives:** Predetermined third transformation
- **5 lives:** Choose one of two upgrades
- **4 lives:** Choose one of two upgrades
- **3 lives:** Choose one of two upgrades
- **2 lives:** Choose one of three upgrades
- **1 life:** Choose one of three final upgrades
- **0 lives:** True death and run over

Transformations should accumulate during a run, but they must not create eight separate mobile buttons. They should modify:

- Waffle's basic attack
- Movement or defense
- Passive behavior
- At most one dedicated active life ability

For the MVP, implement only the first complete transition:

```text
9 lives
→ health reaches zero
→ life-loss sequence
→ 8 lives
→ predetermined transformation
→ Waffle returns with full health
→ transformed ability becomes usable
```

The suggested MVP transformation is **Spirit Pounce**:

- Waffle's tail gains a subtle glow
- His pounce becomes longer or stronger
- He briefly passes through enemies or avoids contact damage while pouncing
- It has readable feedback and a cooldown

The name and tuning may be adjusted if testing reveals a clearer beginner-friendly implementation, but the feature must still demonstrate visible and mechanical transformation.

---

## 5. MVP product goal

Build one short, stable, playable Android run that proves this statement:

> Waffle can explore top-down rooms, fight or evade enemies, lose a life, visibly transform, gain a useful ability, and continue trying to get home.

A player should be able to:

1. Launch the Android application.
2. See that the protagonist is Waffle, a fluffy orange cat.
3. Move with touch controls.
4. Use a simple cat-like attack or pounce.
5. Fight or avoid an enemy.
6. Clear an objective and open a door.
7. Move into another room.
8. Take damage and lose health.
9. Reach zero health and consume one of nine lives.
10. See the counter move from 9 to 8.
11. See a short transformation presentation.
12. Resume the run with the new Spirit Pounce ability.
13. Reach and complete a final encounter or end room.
14. Understand that Waffle is trying to get home.

---

## 6. Strict MVP scope

### Required

#### Android foundation

- A native Android project that opens correctly in Android Studio
- Kotlin
- A simple, reliable rendering/game-loop approach appropriate for this small MVP
- Landscape orientation unless repository constraints strongly justify otherwise
- Compatibility with a commonly available Android emulator/API level
- A debug APK that can be built

Do not switch to Unity, Godot, Flutter, React Native, or a browser wrapper unless the existing repository has already committed to one of those and changing it would be clearly safer. The requested default is native Kotlin/Android Studio.

#### Player

- Waffle rendered as an original fluffy, chubby orange cat
- Top-down movement
- Facing direction
- Health
- Damage and brief invulnerability after being hit
- Simple attack, preferably claw swipe or short pounce
- Nine-life counter
- One visible transformation
- One transformed ability

#### Controls

- Left virtual joystick for movement
- Large right-side attack/pounce button
- Smaller ability button, hidden or disabled before the first transformation
- Pause/restart access
- Touch targets large enough for a phone
- Desktop keyboard controls may be added only as a development convenience

#### World

- One world theme: **The Neighborhood**
- Three to five authored room templates
- A short randomized or shuffled route
- Doors and room transitions
- Clear visual boundaries and collision
- One start room
- At least one combat or danger room
- At least one objective/reward room
- One final encounter or finish room

#### Room completion

Support at least two of these in the MVP:

- Defeat all hostile enemies
- Survive or avoid a hazard
- Retrieve a key or reach an objective

Combat-room doors should remain closed while active threats remain and open on completion.

#### Enemies

Implement two simple original behaviors:

1. **Chaser**, such as a territorial dog or rival animal
2. **Evasive, patrolling, or ranged threat**, such as a crow

Also create one stronger final encounter. It may reuse and expand an existing enemy behavior rather than requiring a fully separate boss engine.

#### Pickups and progression

- One healing pickup
- One key
- One locked door or gate
- One reward pickup

#### Feedback

- Clear damage flash
- Knockback or impact feedback
- Simple particles or visual burst where affordable
- Life-loss presentation
- Transformation presentation
- Basic sound only if it does not threaten stability or schedule

#### Persistence

- No Firebase
- No account system
- No cloud saving
- A simple local run state is optional
- A restart button is required

### Explicitly out of scope

Do not build these unless every required feature is complete and stable:

- Nine finished worlds
- All eight transformations
- Previous-life ghost replay
- Shops
- Large inventory
- Online services
- Firebase
- Accounts
- Leaderboards
- Multiplayer
- Complex procedural generation
- Multiple bosses
- Elaborate cinematics
- Full lore database
- Dozens of enemies
- Advanced item synergies
- Imported copyrighted assets

---

## 7. Base combat recommendation

Use a cat-specific combat identity rather than copying projectile combat from the reference.

Preferred MVP combat:

### Basic action: Claw Swipe

- Short range
- Fast
- Uses Waffle's facing direction
- Damages and slightly knocks back an enemy
- No complicated aiming requirement

### Signature action: Pounce

If scope permits, use press or tap to pounce in the facing or movement direction.

Pounce can eventually serve as:

- Attack
- Dodge
- Gap crossing
- Mouse catching
- Fragile-object breaking
- Platform/elevation interaction

For the MVP, keep it mechanically simple. If implementing both swipe and pounce risks the build, make **pounce the single base attack** and make Spirit Pounce its transformed version.

Do not add many permanent action buttons.

---

## 8. Death and respawn rules

For the MVP:

1. Waffle has a health meter within each life.
2. Damage reduces health.
3. When health reaches zero, stop normal input and play a brief life-loss sequence.
4. Decrease `livesRemaining` by one.
5. If it becomes zero, show true death/game over and allow a new run.
6. Otherwise, grant the correct transformation for the new life count.
7. Restore health.
8. Return Waffle to the current room entrance or a safe checkpoint.
9. Reset active enemies in that room to make the encounter fair and deterministic.
10. Preserve run-level transformations, keys, and other explicitly permanent run state.

Do not implement a full choice UI for lives 5 through 1 yet, but design the data model so fixed, two-choice, and three-choice upgrades can be added later.

Add a developer/debug control that can safely:

- Damage Waffle
- Set lives to 9, 8, 5, 2, or 1
- Reset the run
- Display collision boundaries or current room ID if useful

The debug controls must not appear prominently in the normal player experience and must be documented.

---

## 9. Art direction

Use original placeholder art that clearly communicates the game.

Waffle must read as:

- Orange
- Fluffy
- Chubby
- Cat-like
- Friendly and visually distinct

Target style:

- Retro pixel-inspired or chunky vector look
- Limited, coherent palette
- Strong silhouettes
- Clear mobile readability
- Minimal gradients
- Consistent scale

It is acceptable to use simple programmatic/vector-like shapes or original placeholder sprites for the MVP. Do not copy visual assets or character designs from the supplied references.

Favor a small logical coordinate system and scale cleanly to the device. Avoid excessive high-resolution textures.

---

## 10. How to use the supplied reference files

You may receive:

1. `tboi dupe project reference.md`
2. Course Forge source files and documentation, possibly including:
   - `COURSE_FORGE.md`
   - `AGENTS.md`
   - `courses/static/courses/js/forge/forge_input.js`
   - `forge_camera.js`
   - `forge_entities.js`
   - `forge_renderer.js`
   - `forge_camp.js`
   - `forge_plots.js`
   - `ART_GUIDE.md`

Treat those files as **technical references only**.

Useful concepts to study and adapt independently into Kotlin include:

- Normalized movement vectors
- Joystick dead zones
- Pointer/touch capture concepts
- Edge-triggered actions
- Delta-time clamping
- Smooth movement interpolation
- Axis-separated collision
- Circle collision and separation
- Room states
- Door lockdown until completion
- Authored room templates
- Seeded or shuffled route generation
- Enemy state machines
- Attack telegraphs
- Knockback and hit flash
- Camera follow and restrained screen shake
- Fixed entity limits
- Separate gameplay RNG from visual randomness
- Data-driven upgrades
- Logical-resolution rendering

Do **not** directly copy:

- Copyrighted names
- Isaac characters or bosses
- Enemy designs
- Item names or effects as a set
- Art
- Music or sounds
- Story
- Room appearance
- UI layout
- Source code line-for-line

Do not attempt to run the JavaScript files inside the Android application. Translate only the general engineering ideas needed for Waffle's original Kotlin implementation.

---

## 11. Technical simplicity requirements

Prefer the smallest architecture that remains understandable and extensible.

A reasonable structure might separate:

```text
app/
└── src/main/java/<package>/
    ├── MainActivity.kt
    ├── game/
    │   ├── GameView.kt
    │   ├── GameLoop.kt
    │   ├── GameState.kt
    │   ├── GameConfig.kt
    │   └── Camera.kt
    ├── input/
    │   └── TouchControls.kt
    ├── entities/
    │   ├── Player.kt
    │   ├── Enemy.kt
    │   ├── ChaserEnemy.kt
    │   └── RangedOrPatrolEnemy.kt
    ├── world/
    │   ├── Room.kt
    │   ├── RoomTemplate.kt
    │   ├── RoomManager.kt
    │   └── Collision.kt
    ├── progression/
    │   ├── LifeSystem.kt
    │   ├── LifeUpgrade.kt
    │   └── SpiritPounce.kt
    └── ui/
        └── Hud.kt
```

This is guidance, not a demand. First inspect the existing repository. Do not create unnecessary abstractions or dozens of empty classes.

Important implementation safeguards:

- Clamp large delta times after pause/resume
- Keep simulation units independent from physical screen pixels
- Handle different aspect ratios and display cutouts
- Avoid allocating large collections every frame
- Cap particles and active entities
- Pause or reduce updates when the app loses focus
- Prevent touches from becoming stuck after cancellation
- Normalize diagonal movement
- Add invulnerability frames after damage
- Keep collision handling deterministic and understandable
- Keep room templates traversable
- Do not block the UI thread with file or network work
- Avoid external dependencies unless clearly necessary

---

## 12. Required implementation workflow

### Phase 0: Inspect and protect

Before changing files:

1. Print the working directory.
2. List the repository structure.
3. Read relevant README, Gradle, manifest, and source files.
4. Run `git status` if this is a Git repository.
5. Identify the current branch.
6. Create a new branch such as `feature/waffle-mvp` if safe and appropriate.
7. Do not overwrite an unrelated existing application.
8. Record the baseline build status.

Output a short **Repository Findings** section before implementation.

### Phase 1: Produce an implementation plan

Create `WAFFLE_MVP_PLAN.md` containing:

- Current repository condition
- Chosen technical approach and why
- MVP scope
- Milestones
- Files to create or change
- Risks
- Test strategy
- Clear definition of done

Then proceed without waiting for confirmation unless a destructive or genuinely ambiguous decision is unavoidable.

### Phase 2: Create a vertical slice

First make only this work:

```text
Launch app
→ show one room
→ render Waffle
→ move Waffle with touch
→ collide with room boundaries
→ attack or pounce
→ damage one enemy
```

Build and test before adding more systems.

### Phase 3: Add room progression

Add:

- Three to five room templates
- Doors
- Room transition
- Active-room lockdown
- Completion condition
- Short route ending in a final room

Build and test again.

### Phase 4: Add the signature nine-life system

Add:

- Health
- Lives remaining
- Damage invulnerability
- Life loss
- 9-to-8 transition
- Spirit Pounce transformation
- Visual change
- Resume run
- True death path

Build and test again.

### Phase 5: Add basic content and polish

Add only after the loop works:

- Second enemy behavior
- Healing pickup
- Key and locked door
- Reward pickup
- Basic feedback
- Minimal sound if stable
- Pause and restart

### Phase 6: Verify and hand off

Run all available checks. At minimum:

- Gradle sync/build
- Kotlin compilation
- Debug APK assembly
- Relevant unit tests
- Manual smoke-test checklist

If the environment cannot run an emulator, still build the APK and provide exact emulator/device steps for the beginner.

---

## 13. Testing requirements

Create a lightweight test plan covering:

### Startup

- App launches without crashing
- Correct orientation
- Waffle and HUD appear

### Controls

- Joystick moves Waffle
- Diagonal movement is not faster
- Touch cancellation releases controls
- Attack button triggers once per intended press
- Ability is unavailable before transformation and usable afterward

### Collision

- Waffle cannot pass through walls
- Waffle does not become permanently stuck
- Enemies respect intended obstacles
- Door transitions position Waffle safely

### Combat

- Attacks damage enemies once per hit window
- Invulnerability prevents rapid repeated damage
- Knockback does not push entities through walls
- Room clears when threats are defeated

### Lives

- Start at nine
- Zero health changes nine to eight
- Health restores
- Transformation is granted once
- Ability works
- Repeated life loss never produces negative lives
- Zero lives causes true death
- Restart restores nine lives and clears run upgrades

### Android lifecycle

- Pause/resume does not create a huge movement jump
- Rotating or backgrounding does not duplicate the game loop
- Returning to the app does not leave touch input stuck

### Performance

- Stable enough on the selected emulator/device
- No unbounded entity, particle, or projectile growth

---

## 14. Required documentation and beginner handoff

Create or update these files:

### `README.md`

Include:

- What the game is
- Current MVP features
- Requirements
- Exact Android Studio opening steps
- Exact Gradle sync steps
- How to create/select an emulator
- How to run the app
- How to build a debug APK
- Exact APK output path
- Controls
- Known limitations
- Common beginner errors and fixes

### `WAFFLE_MVP_PLAN.md`

The implementation plan and progress checklist.

### `WAFFLE_CHANGELOG.md`

For each phase:

- What changed
- Files changed
- Why
- Tests run
- Result
- Remaining issue

### `WAFFLE_BEGINNER_GUIDE.md`

Explain in very simple terms:

- What Android Studio does
- What Gradle does
- What an emulator is
- Where the project begins
- Where Waffle's behavior lives
- Where rooms are defined
- Where lives and transformations are defined
- How to change a number safely, such as movement speed or maximum health
- How to recover from a failed build
- How to copy and share an error message

---

## 15. Communication format during implementation

After each milestone, report:

```text
MILESTONE:

WHAT I CHANGED:

FILES CHANGED:

WHY:

HOW I TESTED IT:

RESULT:

WHAT THE BEGINNER SHOULD DO NOW:

NEXT MILESTONE:
```

Keep the explanation concise but complete. Never claim a build or test succeeded unless you actually ran it and saw a successful result.

When blocked, report:

```text
BLOCKER:

WHAT FAILED:

EXACT ERROR:

LIKELY CAUSE:

WHAT I ALREADY TRIED:

SAFEST NEXT ACTION:
```

---

## 16. Definition of done

The MVP is done only when:

- The project opens in Android Studio
- Gradle successfully syncs
- A debug build completes
- The app launches on an emulator or documented device target
- Waffle is recognizable as an original fluffy orange cat
- Touch movement works
- Attack or pounce works
- At least two enemy behaviors work
- Three to five rooms are playable
- Doors and room completion work
- Health and damage work
- Waffle can lose a life
- The HUD changes from 9 to 8
- Waffle visibly transforms
- Spirit Pounce or an equivalent first-life ability works
- A short route has a clear ending
- Restart works
- Documentation exists for a complete beginner
- Known limitations are honestly documented
- No copyrighted assets or direct copyrighted content were copied

---

## 17. Start now

Begin by inspecting the repository and environment. Do not immediately generate the entire game in one unverified pass.

Your first response must contain:

1. **Repository Findings**
2. **Recommended technical approach**
3. **MVP milestone plan**
4. **Risks or missing prerequisites**
5. **The first implementation action you are taking**

Then create the planning file and proceed with the vertical slice.
