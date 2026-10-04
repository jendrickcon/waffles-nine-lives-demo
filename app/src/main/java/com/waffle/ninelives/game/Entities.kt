package com.waffle.ninelives.game

/** Anything with a position and a (square) collision size. `r` is half the box width. */
open class Body {
    var x = 0f
    var y = 0f
    var r = 6f
}

class Player : Body() {
    var vx = 0f
    var vy = 0f
    var faceX = 1f
    var faceY = 0f
    var health = 6
    var invuln = 0f
    var hitFlash = 0f

    var swipeTime = 0f
    var swipeCooldown = 0f
    var attackId = 0          // identifies one attack so an enemy is hit only once per attack

    var pounceTime = 0f
    var pounceCooldown = 0f
    var pounceDx = 1f
    var pounceDy = 0f

    var walkPhase = 0f
    var moving = false

    init {
        r = GameConfig.PLAYER_RADIUS
    }

    val isPouncing: Boolean get() = pounceTime > 0f

    fun reset(maxHealth: Int) {
        health = maxHealth
        vx = 0f; vy = 0f
        faceX = 1f; faceY = 0f
        invuln = 0f; hitFlash = 0f
        swipeTime = 0f; swipeCooldown = 0f
        pounceTime = 0f; pounceCooldown = 0f
        walkPhase = 0f
        moving = false
    }
}

enum class EnemyState { CHASE, WINDUP, LUNGE, RECOVER, HOVER, AIM }

/** Data-driven enemy tuning. Add a new enemy by adding a row here (and a behavior in GameWorld). */
enum class EnemyType(
    val maxHp: Int,
    val radius: Float,
    val speed: Float,
    val flying: Boolean,
    val trigger: Float,      // distance at which a charger starts its wind-up
    val windup: Float,
    val lungeSpeed: Float,
    val lungeTime: Float,
    val recover: Float,
    val initialState: EnemyState
) {
    DOG(3, 6f, 36f, false, 54f, 0.45f, 125f, 0.30f, 0.8f, EnemyState.CHASE),
    CROW(2, 5f, 40f, true, 0f, 0f, 0f, 0f, 0f, EnemyState.HOVER),
    BOSS(14, 11f, 30f, false, 90f, 0.7f, 150f, 0.45f, 1.0f, EnemyState.CHASE)
}

class Enemy(val type: EnemyType, val spawnX: Float, val spawnY: Float) : Body() {
    var hp = type.maxHp
    var alive = true
    var vx = 0f
    var vy = 0f
    var hitFlash = 0f
    var state = type.initialState
    var stateTime = 0f
    var dirX = 1f
    var dirY = 0f
    var cooldown = 1.5f
    var strafe = 1f
    var lastHitBy = -1

    init {
        x = spawnX
        y = spawnY
        r = type.radius
    }

    val enraged: Boolean get() = type == EnemyType.BOSS && hp * 2 <= type.maxHp

    fun resetToSpawn() {
        x = spawnX; y = spawnY
        hp = type.maxHp
        vx = 0f; vy = 0f
        hitFlash = 0f
        state = type.initialState
        stateTime = 0f
        cooldown = 1.5f
        lastHitBy = -1
    }
}

class Projectile {
    var active = false
    var x = 0f
    var y = 0f
    var vx = 0f
    var vy = 0f
    var life = 0f
}

enum class PickupType { HEART, KEY, REWARD, EXIT }

class Pickup(val type: PickupType, val x: Float, val y: Float) {
    var taken = false
}

/** Fixed-size particle pool (a ring buffer: the oldest particle is recycled when full). */
class Particles(val capacity: Int) {
    val x = FloatArray(capacity)
    val y = FloatArray(capacity)
    val vx = FloatArray(capacity)
    val vy = FloatArray(capacity)
    val life = FloatArray(capacity)
    val maxLife = FloatArray(capacity)
    val color = IntArray(capacity)
    val size = FloatArray(capacity)
    private var next = 0

    fun spawn(px: Float, py: Float, pvx: Float, pvy: Float, lifeTime: Float, c: Int, s: Float) {
        val i = next
        next = (next + 1) % capacity
        x[i] = px; y[i] = py
        vx[i] = pvx; vy[i] = pvy
        life[i] = lifeTime; maxLife[i] = lifeTime
        color[i] = c
        size[i] = s
    }

    fun update(dt: Float) {
        val drag = (1f - 3f * dt).coerceAtLeast(0f)
        for (i in 0 until capacity) {
            if (life[i] > 0f) {
                life[i] -= dt
                x[i] += vx[i] * dt
                y[i] += vy[i] * dt
                vx[i] *= drag
                vy[i] *= drag
            }
        }
    }

    fun clear() {
        for (i in 0 until capacity) life[i] = 0f
    }
}
