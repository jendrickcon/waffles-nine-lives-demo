package com.waffle.ninelives.game

import com.waffle.ninelives.progression.LifeTable
import com.waffle.ninelives.progression.LifeUpgrade
import com.waffle.ninelives.progression.RunState
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

enum class GameState { INTRO, PLAYING, LIFE_LOSS, TRANSFORM, TRUE_DEATH, VICTORY }

/**
 * The whole game simulation. It contains NO Android classes, so it runs the same on a phone
 * and in plain unit tests. The game thread calls [update] with a fixed time step.
 *
 * Reading guide (top to bottom): run setup -> input -> player -> enemies -> projectiles ->
 * pickups -> rooms -> life loss / transformation -> effects.
 */
class GameWorld(seed: Long = System.nanoTime()) {
    val input = InputState()
    val player = Player()
    val run = RunState()
    val particles = Particles(GameConfig.MAX_PARTICLES)
    val projectiles = Array(GameConfig.MAX_PROJECTILES) { Projectile() }

    var rooms: List<Room> = emptyList()
        private set
    var roomIndex = 0
        private set
    var state = GameState.INTRO
        private set
    @Volatile
    var paused = false
        private set
    var stateTimer = 0f
        private set
    var time = 0f
        private set
    var shake = 0f
        private set
    var banner = ""
        private set
    var bannerSub = ""
        private set
    var bannerTimer = 0f
        private set
    var lastUpgrade: LifeUpgrade? = null
        private set
    var debugBoxes = false

    private var rng: Random = Random(seed)   // gameplay randomness (seeded, repeatable)
    private val fxRng = Random(7)            // visual-only randomness (never affects gameplay)
    private var attackSerial = 0
    private var transformedThisLife = false

    val currentRoom: Room get() = rooms[roomIndex]
    val abilityUnlocked: Boolean get() = run.has(LifeTable.SPIRIT_POUNCE.id)

    init {
        startRun(seed)
    }

    // ------------------------------------------------------------------ run setup

    private fun startRun(seed: Long) {
        rng = Random(seed)
        run.reset()
        rooms = RoomFactory.buildRoute(rng)
        roomIndex = 0
        attackSerial = 0
        player.reset(run.maxHealth)
        clearProjectiles()
        particles.clear()
        stateTimer = 0f
        shake = 0f
        bannerTimer = 0f
        lastUpgrade = null
        paused = false
        input.clearEdges()
        input.releaseMove()
        enterRoom(0, true)
        state = GameState.INTRO
    }

    /** Start a brand-new run immediately (skips the intro screen). */
    fun restart() {
        startRun(System.nanoTime())
        state = GameState.PLAYING
        showBanner(RoomRole.START.title, RoomRole.START.hint, 2.5f)
    }

    /** Called by the Activity when the app goes to the background. */
    fun pauseForLifecycle() {
        if (isPausable()) paused = true
        input.releaseMove()
        input.clearEdges()
    }

    private fun isPausable() =
        state == GameState.PLAYING || state == GameState.LIFE_LOSS || state == GameState.TRANSFORM

    // ------------------------------------------------------------------ main update

    fun update(dt: Float) {
        processInput()
        if (paused) return
        time += dt
        particles.update(dt)
        shake = max(0f, shake - 12f * dt)
        if (bannerTimer > 0f) bannerTimer -= dt

        when (state) {
            GameState.PLAYING -> updatePlaying(dt)
            GameState.LIFE_LOSS -> {
                input.clearEdges()
                stateTimer -= dt
                if (stateTimer <= 0f) finishLifeLoss()
            }
            GameState.TRANSFORM -> {
                input.clearEdges()
                stateTimer -= dt
                if (fxRng.nextFloat() < 0.5f) {
                    val a = fxRng.nextFloat() * 2f * PI.toFloat()
                    particles.spawn(
                        player.x, player.y, cos(a) * 50f, sin(a) * 50f - 20f,
                        0.7f, Palette.SPIRIT, 2f
                    )
                }
                if (stateTimer <= 0f) resumeAfterLifeLoss()
            }
            else -> input.clearEdges()
        }
    }

    private fun processInput() {
        if (input.consumeRestart()) {
            restart()
            return
        }
        if (input.consumeResume()) paused = false
        if (input.consumePauseToggle() && isPausable()) paused = !paused
        if (input.consumeTap()) {
            when (state) {
                GameState.INTRO -> {
                    state = GameState.PLAYING
                    input.clearEdges()
                    showBanner(RoomRole.START.title, RoomRole.START.hint, 3f)
                }
                GameState.TRUE_DEATH, GameState.VICTORY -> restart()
                else -> {}
            }
        }
        while (true) {
            val cmd = input.debugQueue.poll() ?: break
            applyDebug(cmd)
        }
    }

    private fun updatePlaying(dt: Float) {
        val room = currentRoom
        updatePlayer(dt, room)
        if (state != GameState.PLAYING) return
        updateEnemies(dt, room)
        if (state != GameState.PLAYING) return
        updateProjectiles(dt, room)
        if (state != GameState.PLAYING) return
        updatePickups(room)
        if (state != GameState.PLAYING) return
        updateGate(room)
        checkRoomCleared(room)
        checkTransitions(room)
    }

    // ------------------------------------------------------------------ player

    private fun updatePlayer(dt: Float, room: Room) {
        val p = player
        p.invuln = max(0f, p.invuln - dt)
        p.hitFlash = max(0f, p.hitFlash - dt)
        p.swipeTime = max(0f, p.swipeTime - dt)
        p.swipeCooldown = max(0f, p.swipeCooldown - dt)
        p.pounceCooldown = max(0f, p.pounceCooldown - dt)

        var mx = input.moveX
        var my = input.moveY
        val mag = hypot(mx, my)
        if (mag > 1f) {            // normalize so diagonals are never faster
            mx /= mag
            my /= mag
        }
        val wantAttack = input.consumeAttack()
        val wantAbility = input.consumeAbility()

        // Spirit Pounce in progress: a fixed-direction dash that ignores enemy contact.
        if (p.pounceTime > 0f) {
            p.pounceTime -= dt
            Collision.move(room, p, p.pounceDx * GameConfig.SPIRIT_SPEED * dt, p.pounceDy * GameConfig.SPIRIT_SPEED * dt)
            spiritHits(room)
            if (fxRng.nextFloat() < 0.8f) {
                particles.spawn(p.x, p.y + 2f, 0f, 0f, 0.35f, Palette.SPIRIT, 3f)
            }
            if (p.pounceTime <= 0f) {
                p.pounceTime = 0f
                p.invuln = max(p.invuln, GameConfig.SPIRIT_GRACE)
            }
            return
        }

        val len = hypot(mx, my)
        p.moving = len > 0.05f
        if (len > 0.2f) {
            p.faceX = mx / len
            p.faceY = my / len
        }
        Collision.move(
            room, p,
            (mx * GameConfig.PLAYER_SPEED + p.vx) * dt,
            (my * GameConfig.PLAYER_SPEED + p.vy) * dt
        )
        val k = max(0f, 1f - GameConfig.KNOCKBACK_DECAY * dt)
        p.vx *= k
        p.vy *= k
        if (p.moving) p.walkPhase += dt * 10f

        if (wantAbility && abilityUnlocked && p.pounceCooldown <= 0f) startSpiritPounce()
        if (wantAttack && !p.isPouncing && p.swipeCooldown <= 0f) startSwipe(room)
    }

    private fun startSwipe(room: Room) {
        val p = player
        p.swipeCooldown = GameConfig.SWIPE_COOLDOWN
        p.swipeTime = GameConfig.SWIPE_DURATION
        p.attackId = ++attackSerial
        p.vx += p.faceX * 35f    // tiny forward lunge for feel
        p.vy += p.faceY * 35f
        for (e in room.enemies) {
            if (!e.alive) continue
            val dx = e.x - p.x
            val dy = e.y - p.y
            val d = hypot(dx, dy)
            if (d <= GameConfig.SWIPE_RANGE + e.r) {
                val dot = if (d < 0.001f) 1f else (dx * p.faceX + dy * p.faceY) / d
                if (dot >= GameConfig.SWIPE_CONE_DOT) {
                    hitEnemy(e, GameConfig.SWIPE_DAMAGE, GameConfig.SWIPE_KNOCKBACK)
                }
            }
        }
    }

    private fun startSpiritPounce() {
        val p = player
        p.pounceTime = GameConfig.SPIRIT_TIME
        p.pounceCooldown = GameConfig.SPIRIT_COOLDOWN
        p.pounceDx = p.faceX
        p.pounceDy = p.faceY
        p.attackId = ++attackSerial
        burst(p.x, p.y, Palette.SPIRIT, 10, 70f)
        shake = max(shake, 1.5f)
    }

    private fun spiritHits(room: Room) {
        val p = player
        for (e in room.enemies) {
            if (!e.alive) continue
            if (hypot(e.x - p.x, e.y - p.y) < p.r + e.r + 2f) {
                hitEnemy(e, GameConfig.SPIRIT_DAMAGE, GameConfig.SPIRIT_KNOCKBACK)
            }
        }
    }

    /** Returns true if damage was actually dealt (false while invulnerable or pouncing). */
    private fun damagePlayer(amount: Int, fromX: Float, fromY: Float): Boolean {
        val p = player
        if (state != GameState.PLAYING || p.invuln > 0f || p.isPouncing) return false
        p.health -= amount
        p.invuln = GameConfig.INVULN_TIME
        p.hitFlash = GameConfig.HIT_FLASH_TIME
        var nx = p.x - fromX
        var ny = p.y - fromY
        val d = hypot(nx, ny)
        if (d > 0.001f) {
            nx /= d
            ny /= d
        } else {
            nx = -p.faceX
            ny = -p.faceY
        }
        p.vx = nx * GameConfig.DAMAGE_KNOCKBACK
        p.vy = ny * GameConfig.DAMAGE_KNOCKBACK
        burst(p.x, p.y, Palette.DAMAGE, 8, 80f)
        shake = max(shake, 3f)
        if (p.health <= 0) {
            p.health = 0
            beginLifeLoss()
        }
        return true
    }

    // ------------------------------------------------------------------ enemies

    private fun hitEnemy(e: Enemy, damage: Int, force: Float) {
        // One hit per attack per enemy ("hit window").
        if (!e.alive || e.lastHitBy == player.attackId) return
        e.lastHitBy = player.attackId
        e.hp -= damage
        e.hitFlash = 0.14f
        var nx = e.x - player.x
        var ny = e.y - player.y
        val d = hypot(nx, ny)
        if (d > 0.001f) {
            nx /= d
            ny /= d
        } else {
            nx = player.faceX
            ny = player.faceY
        }
        val resist = if (e.type == EnemyType.BOSS) 0.35f else 1f
        e.vx = nx * force * resist
        e.vy = ny * force * resist
        if (e.type != EnemyType.BOSS && (e.state == EnemyState.WINDUP || e.state == EnemyState.LUNGE)) {
            setState(e, EnemyState.RECOVER) // a good hit staggers a charging enemy
        }
        burst(e.x, e.y, Palette.WHITE, 5, 60f)
        shake = max(shake, 1.5f)
        if (e.hp <= 0) killEnemy(e)
    }

    private fun killEnemy(e: Enemy) {
        e.alive = false
        val c = when (e.type) {
            EnemyType.DOG -> Palette.DOG
            EnemyType.CROW -> Palette.CROW_LIGHT
            else -> Palette.BOSS
        }
        burst(e.x, e.y, c, if (e.type == EnemyType.BOSS) 30 else 14, 90f)
        shake = max(shake, if (e.type == EnemyType.BOSS) 6f else 2.5f)
        val room = currentRoom
        if (e.type != EnemyType.BOSS && rng.nextFloat() < GameConfig.HEART_DROP_CHANCE) {
            var waiting = 0
            for (pk in room.pickups) if (!pk.taken) waiting++
            if (waiting < GameConfig.MAX_PICKUPS) room.pickups.add(Pickup(PickupType.HEART, e.x, e.y))
        }
    }

    private fun setState(e: Enemy, s: EnemyState) {
        e.state = s
        e.stateTime = 0f
    }

    private fun updateEnemies(dt: Float, room: Room) {
        val list = room.enemies
        for (e in list) {
            if (!e.alive) continue
            e.hitFlash = max(0f, e.hitFlash - dt)
            e.stateTime += dt
            // knockback (respects walls)
            if (e.vx != 0f || e.vy != 0f) {
                Collision.move(room, e, e.vx * dt, e.vy * dt, e.type.flying)
                val k = max(0f, 1f - GameConfig.KNOCKBACK_DECAY * dt)
                e.vx *= k
                e.vy *= k
            }
            if (e.type == EnemyType.CROW) updateCrow(e, dt, room) else updateCharger(e, dt, room)

            if (hypot(player.x - e.x, player.y - e.y) < player.r + e.r) {
                damagePlayer(1, e.x, e.y)
                if (state != GameState.PLAYING) return
            }
        }
        // keep enemies from stacking on top of each other
        for (i in 0 until list.size) {
            val a = list[i]
            if (!a.alive) continue
            for (j in i + 1 until list.size) {
                val b = list[j]
                if (!b.alive) continue
                val dx = b.x - a.x
                val dy = b.y - a.y
                val d = hypot(dx, dy)
                val minD = a.r + b.r
                if (d < minD && d > 0.001f) {
                    val push = (minD - d) * 0.5f
                    val nx = dx / d
                    val ny = dy / d
                    Collision.move(room, a, -nx * push, -ny * push, a.type.flying)
                    Collision.move(room, b, nx * push, ny * push, b.type.flying)
                }
            }
        }
    }

    /** Dog and boss: chase -> wind-up (telegraph) -> lunge -> recover. */
    private fun updateCharger(e: Enemy, dt: Float, room: Room) {
        val t = e.type
        val dx = player.x - e.x
        val dy = player.y - e.y
        val dist = max(0.001f, hypot(dx, dy))
        val rage = if (e.enraged) 1.35f else 1f
        when (e.state) {
            EnemyState.CHASE -> {
                e.dirX = dx / dist
                e.dirY = dy / dist
                Collision.move(room, e, e.dirX * t.speed * rage * dt, e.dirY * t.speed * rage * dt)
                if (dist < t.trigger && e.stateTime > 0.35f) setState(e, EnemyState.WINDUP)
            }
            EnemyState.WINDUP -> {
                if (e.stateTime < t.windup * 0.6f) { // aim, then commit to a direction
                    e.dirX = dx / dist
                    e.dirY = dy / dist
                }
                if (e.stateTime >= t.windup / rage) setState(e, EnemyState.LUNGE)
            }
            EnemyState.LUNGE -> {
                val bumped = Collision.move(
                    room, e, e.dirX * t.lungeSpeed * rage * dt, e.dirY * t.lungeSpeed * rage * dt
                )
                if (bumped || e.stateTime >= t.lungeTime) {
                    setState(e, EnemyState.RECOVER)
                    if (t == EnemyType.BOSS) {
                        shake = max(shake, 3f)
                        if (e.enraged) fireRing(e)
                    }
                }
            }
            EnemyState.RECOVER -> {
                if (e.stateTime >= t.recover) setState(e, EnemyState.CHASE)
            }
            else -> setState(e, EnemyState.CHASE)
        }
    }

    /** Crow: keeps its distance, strafes, telegraphs, then throws a feather. */
    private fun updateCrow(e: Enemy, dt: Float, room: Room) {
        val dx = player.x - e.x
        val dy = player.y - e.y
        val dist = max(0.001f, hypot(dx, dy))
        e.dirX = dx / dist
        e.dirY = dy / dist
        e.cooldown -= dt
        if (e.state == EnemyState.AIM) {
            if (e.stateTime >= GameConfig.CROW_AIM_TIME) {
                fireFeather(e.x, e.y, dx / dist, dy / dist, GameConfig.FEATHER_SPEED)
                e.cooldown = 2.0f + rng.nextFloat()
                setState(e, EnemyState.HOVER)
            }
            return
        }
        var mvx = 0f
        var mvy = 0f
        if (dist < 52f) {
            mvx = -dx / dist
            mvy = -dy / dist
        } else if (dist > 88f) {
            mvx = dx / dist
            mvy = dy / dist
        }
        mvx += -dy / dist * e.strafe * 0.7f
        mvy += dx / dist * e.strafe * 0.7f
        val ml = hypot(mvx, mvy)
        if (ml > 1f) {
            mvx /= ml
            mvy /= ml
        }
        val bumped = Collision.move(room, e, mvx * e.type.speed * dt, mvy * e.type.speed * dt, true)
        if (bumped || e.stateTime > 2.5f) {
            e.strafe = -e.strafe
            e.stateTime = 0f
        }
        if (e.cooldown <= 0f && dist < 170f) setState(e, EnemyState.AIM)
    }

    // ------------------------------------------------------------------ projectiles

    private fun fireFeather(x: Float, y: Float, dirX: Float, dirY: Float, speed: Float) {
        for (pr in projectiles) {
            if (!pr.active) {
                pr.active = true
                pr.x = x; pr.y = y
                pr.vx = dirX * speed
                pr.vy = dirY * speed
                pr.life = GameConfig.FEATHER_LIFE
                return
            }
        }
        // Pool full: skip. This is what keeps projectile count bounded.
    }

    private fun fireRing(e: Enemy) {
        val n = 8
        for (i in 0 until n) {
            val a = i * (2f * PI.toFloat() / n)
            fireFeather(e.x, e.y, cos(a), sin(a), 60f)
        }
    }

    private fun clearProjectiles() {
        for (pr in projectiles) pr.active = false
    }

    private fun updateProjectiles(dt: Float, room: Room) {
        val t = GameConfig.TILE
        for (pr in projectiles) {
            if (!pr.active) continue
            pr.x += pr.vx * dt
            pr.y += pr.vy * dt
            pr.life -= dt
            if (pr.life <= 0f) {
                pr.active = false
                continue
            }
            val tx = floor(pr.x / t).toInt()
            val ty = floor(pr.y / t).toInt()
            if (room.blocksProjectile(tx, ty)) {
                pr.active = false
                burst(pr.x, pr.y, Palette.CROW_LIGHT, 3, 30f)
                continue
            }
            if (hypot(pr.x - player.x, pr.y - player.y) < GameConfig.FEATHER_RADIUS + player.r) {
                if (damagePlayer(1, pr.x, pr.y)) pr.active = false
                if (state != GameState.PLAYING) return
            }
        }
    }

    // ------------------------------------------------------------------ pickups, gate, rooms

    private fun updatePickups(room: Room) {
        val p = player
        for (pk in room.pickups) {
            if (pk.taken) continue
            if (hypot(pk.x - p.x, pk.y - p.y) > GameConfig.PICKUP_RADIUS + p.r) continue
            when (pk.type) {
                PickupType.HEART -> if (p.health < run.maxHealth) {
                    p.health = min(run.maxHealth, p.health + GameConfig.HEART_HEAL)
                    pk.taken = true
                    burst(pk.x, pk.y, Palette.HEART, 8, 50f)
                }
                PickupType.KEY -> {
                    run.hasKey = true
                    pk.taken = true
                    burst(pk.x, pk.y, Palette.KEY, 10, 60f)
                    showBanner("Got the key!", "Take it to the locked gate", 3f)
                }
                PickupType.REWARD -> {
                    run.maxHealth += GameConfig.REWARD_MAX_HEALTH_BONUS
                    p.health = run.maxHealth
                    pk.taken = true
                    burst(pk.x, pk.y, Palette.SILVER, 14, 70f)
                    showBanner("Silver Bell!", "Max health up. Fully healed!", 3f)
                }
                PickupType.EXIT -> {
                    pk.taken = true
                    state = GameState.VICTORY
                    burst(pk.x, pk.y, Palette.ORANGE, 30, 100f)
                    return
                }
            }
        }
    }

    /** Walking up to the locked east gate with the key unlocks it for good. */
    private fun updateGate(room: Room) {
        if (room.eastLocked && room.cleared && run.hasKey &&
            player.x > GameConfig.VIEW_W - 3f * GameConfig.TILE &&
            kotlin.math.abs(player.y - GameConfig.DOOR_Y) < 2f * GameConfig.TILE
        ) {
            room.eastLocked = false
            run.hasKey = false
            burst(GameConfig.VIEW_W - 8f, GameConfig.DOOR_Y, Palette.LOCK, 14, 70f)
            showBanner("Gate unlocked!", "", 2f)
        }
    }

    private fun checkRoomCleared(room: Room) {
        if (room.cleared) return
        val done = when (room.role) {
            RoomRole.KILL, RoomRole.BOSS -> room.enemiesAlive() == 0
            RoomRole.KEY -> room.pickups.any { it.type == PickupType.KEY && it.taken }
            else -> true
        }
        if (!done) return
        room.cleared = true
        if (room.role == RoomRole.BOSS) {
            room.pickups.add(Pickup(PickupType.EXIT, GameConfig.VIEW_W / 2f, GameConfig.DOOR_Y))
            showBanner("The cat flap!", "Duke gave way. Squeeze through!", 4f)
        } else if (room.role == RoomRole.KILL) {
            showBanner("Room clear!", "The gate is open", 2f)
        }
    }

    private fun checkTransitions(room: Room) {
        val m = GameConfig.DOOR_TRIGGER_MARGIN
        if (room.hasEast && room.eastOpen && player.x > GameConfig.VIEW_W - m) {
            enterRoom(roomIndex + 1, true)
        } else if (room.hasWest && room.westOpen && player.x < m) {
            enterRoom(roomIndex - 1, false)
        }
    }

    private fun enterRoom(index: Int, fromWest: Boolean) {
        roomIndex = index
        val room = rooms[index]
        val p = player
        p.x = if (fromWest) 2.5f * GameConfig.TILE else GameConfig.VIEW_W - 2.5f * GameConfig.TILE
        p.y = GameConfig.DOOR_Y
        p.vx = 0f
        p.vy = 0f
        p.pounceTime = 0f
        p.invuln = max(p.invuln, GameConfig.ROOM_ENTER_INVULN)
        room.entryX = p.x
        room.entryY = p.y
        clearProjectiles()
        if (!room.visited) {
            room.visited = true
            room.spawnEnemies()
            room.cleared = when (room.role) {
                RoomRole.KEY -> false
                RoomRole.KILL, RoomRole.BOSS -> room.enemies.isEmpty()
                else -> true
            }
            if (index > 0) showBanner(room.role.title, room.role.hint, 2.5f)
        }
    }

    // ------------------------------------------------------------------ lives

    private fun beginLifeLoss() {
        state = GameState.LIFE_LOSS
        stateTimer = GameConfig.LIFE_LOSS_TIME
        input.clearEdges()
        input.releaseMove()
        burst(player.x, player.y, Palette.ORANGE, 24, 90f)
        shake = max(shake, 5f)
    }

    private fun finishLifeLoss() {
        val lives = run.loseLife()
        if (lives <= 0) {
            state = GameState.TRUE_DEATH
            return
        }
        val upgrade = run.grantFixedFor(lives)
        if (upgrade != null) {
            lastUpgrade = upgrade
            transformedThisLife = true
            state = GameState.TRANSFORM
            stateTimer = GameConfig.TRANSFORM_TIME
        } else {
            resumeAfterLifeLoss()
        }
    }

    private fun resumeAfterLifeLoss() {
        val room = currentRoom
        val p = player
        p.health = run.maxHealth
        p.x = room.entryX
        p.y = room.entryY
        p.vx = 0f
        p.vy = 0f
        p.pounceTime = 0f
        p.pounceCooldown = 0f
        p.swipeCooldown = 0f
        p.invuln = GameConfig.RESPAWN_INVULN
        p.hitFlash = 0f
        clearProjectiles()
        room.resetEnemies()
        input.clearEdges()
        state = GameState.PLAYING
        val msg = if (transformedThisLife) "${lastUpgrade?.name ?: "New power"} ready!" else ""
        transformedThisLife = false
        showBanner("${run.livesRemaining} lives left", msg, 2.5f)
    }

    // ------------------------------------------------------------------ debug

    private fun applyDebug(cmd: DebugCommand) {
        when (cmd) {
            DebugCommand.DAMAGE -> if (state == GameState.PLAYING) {
                player.health -= 1
                player.hitFlash = GameConfig.HIT_FLASH_TIME
                burst(player.x, player.y, Palette.DAMAGE, 6, 60f)
                if (player.health <= 0) {
                    player.health = 0
                    beginLifeLoss()
                }
            }
            DebugCommand.LIVES_9 -> setLivesDebug(9)
            DebugCommand.LIVES_8 -> setLivesDebug(8)
            DebugCommand.LIVES_5 -> setLivesDebug(5)
            DebugCommand.LIVES_2 -> setLivesDebug(2)
            DebugCommand.LIVES_1 -> setLivesDebug(1)
            DebugCommand.RESET_RUN -> restart()
            DebugCommand.TOGGLE_BOXES -> debugBoxes = !debugBoxes
        }
    }

    private fun setLivesDebug(lives: Int) {
        run.setLivesDebug(lives)
        if (state == GameState.PLAYING) player.health = run.maxHealth
        showBanner("DEBUG: lives = ${run.livesRemaining}", "", 1.5f)
    }

    /** Developer helper (used by tests, not by the UI): teleport to a room. */
    fun debugJumpToRoom(index: Int) {
        if (index in rooms.indices) enterRoom(index, true)
    }

    // ------------------------------------------------------------------ effects

    private fun showBanner(text: String, sub: String, seconds: Float) {
        banner = text
        bannerSub = sub
        bannerTimer = seconds
    }

    private fun burst(x: Float, y: Float, color: Int, count: Int, speed: Float) {
        for (i in 0 until count) {
            val a = fxRng.nextFloat() * 2f * PI.toFloat()
            val s = speed * (0.4f + 0.6f * fxRng.nextFloat())
            particles.spawn(x, y, cos(a) * s, sin(a) * s, 0.35f + 0.35f * fxRng.nextFloat(), color, 1.5f + fxRng.nextFloat() * 1.5f)
        }
    }
}
