package com.waffle.ninelives.ui

import android.graphics.RectF
import android.view.MotionEvent
import com.waffle.ninelives.game.DebugCommand
import com.waffle.ninelives.game.GameConfig
import com.waffle.ninelives.game.GameState
import com.waffle.ninelives.game.GameWorld
import kotlin.math.hypot

class DebugButton(val label: String, val command: DebugCommand) {
    val rect = RectF()
}

/**
 * Virtual joystick + buttons. All positions are in SCREEN PIXELS (not game units).
 * Each finger is tracked by its pointer id, so you can move and attack at the same time,
 * and a cancelled touch (call, gesture, rotation) always releases everything.
 */
class TouchControls(private val density: Float) {
    // ---- layout (set by layout()) ----
    var screenW = 0f
    var screenH = 0f
    val joyMaxR = 60f * density
    var joyHomeX = 0f
    var joyHomeY = 0f
    val attackR = 46f * density
    var attackX = 0f
    var attackY = 0f
    val abilityR = 30f * density
    var abilityX = 0f
    var abilityY = 0f
    val pauseR = 20f * density
    var pauseX = 0f
    var pauseY = 0f
    val resumeRect = RectF()
    val restartRect = RectF()
    val debugButtons = listOf(
        DebugButton("DMG", DebugCommand.DAMAGE),
        DebugButton("L9", DebugCommand.LIVES_9),
        DebugButton("L8", DebugCommand.LIVES_8),
        DebugButton("L5", DebugCommand.LIVES_5),
        DebugButton("L2", DebugCommand.LIVES_2),
        DebugButton("L1", DebugCommand.LIVES_1),
        DebugButton("RESET", DebugCommand.RESET_RUN),
        DebugButton("BOX", DebugCommand.TOGGLE_BOXES)
    )

    // ---- live touch state (drawn by the renderer) ----
    @Volatile var joyActive = false
    @Volatile var joyOx = 0f
    @Volatile var joyOy = 0f
    @Volatile var joyKx = 0f
    @Volatile var joyKy = 0f
    @Volatile var attackDown = false
    @Volatile var abilityDown = false
    @Volatile var pauseDown = false
    @Volatile var debugVisible = false

    private var joyId = -1
    private var attackId = -1
    private var abilityId = -1
    private var pauseId = -1
    private var pauseDownAt = 0L
    private var debugFired = false

    fun layout(w: Float, h: Float) {
        val d = density
        screenW = w
        screenH = h
        val marginX = 48f * d
        val marginY = 36f * d
        joyHomeX = marginX + joyMaxR
        joyHomeY = h - marginY - joyMaxR
        attackX = w - marginX - attackR
        attackY = h - marginY - attackR
        abilityX = attackX - 34f * d
        abilityY = attackY - attackR - abilityR - 8f * d
        pauseX = w - marginX - pauseR
        pauseY = 14f * d + pauseR

        val cx = w / 2f
        val cy = h / 2f
        resumeRect.set(cx - 170f * d, cy - 30f * d, cx - 10f * d, cy + 30f * d)
        restartRect.set(cx + 10f * d, cy - 30f * d, cx + 170f * d, cy + 30f * d)

        val bw = 56f * d
        val gap = 4f * d
        val total = debugButtons.size * (bw + gap) - gap
        var left = (w - total) / 2f
        for (b in debugButtons) {
            b.rect.set(left, 6f * d, left + bw, 6f * d + 34f * d)
            left += bw + gap
        }
    }

    fun onTouch(e: MotionEvent, world: GameWorld) {
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val i = e.actionIndex
                pointerDown(e.getPointerId(i), e.getX(i), e.getY(i), world)
            }
            MotionEvent.ACTION_MOVE -> {
                for (i in 0 until e.pointerCount) pointerMove(e.getPointerId(i), e.getX(i), e.getY(i), world)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                pointerUp(e.getPointerId(e.actionIndex), world)
            }
            MotionEvent.ACTION_CANCEL -> cancelAll(world)
        }
    }

    private fun inCircle(x: Float, y: Float, cx: Float, cy: Float, r: Float) = hypot(x - cx, y - cy) <= r

    private fun pointerDown(id: Int, x: Float, y: Float, world: GameWorld) {
        val input = world.input

        if (world.paused) {
            if (resumeRect.contains(x, y)) input.requestResume()
            else if (restartRect.contains(x, y)) input.requestRestart()
            return
        }
        if (debugVisible) {
            for (b in debugButtons) {
                if (b.rect.contains(x, y)) {
                    input.debugQueue.add(b.command)
                    return
                }
            }
        }
        if (inCircle(x, y, pauseX, pauseY, pauseR * 1.4f)) {
            pauseId = id
            pauseDown = true
            pauseDownAt = System.currentTimeMillis()
            debugFired = false
            return
        }
        val st = world.state
        if (st == GameState.INTRO || st == GameState.TRUE_DEATH || st == GameState.VICTORY) {
            input.requestTap()
            return
        }
        if (inCircle(x, y, attackX, attackY, attackR * 1.15f) && attackId == -1) {
            attackId = id
            attackDown = true
            input.pressAttack()
            return
        }
        if (world.abilityUnlocked && inCircle(x, y, abilityX, abilityY, abilityR * 1.25f) && abilityId == -1) {
            abilityId = id
            abilityDown = true
            input.pressAbility()
            return
        }
        if (x < screenW * 0.5f && joyId == -1) {
            joyId = id
            joyActive = true
            joyOx = x; joyOy = y
            joyKx = x; joyKy = y
        }
    }

    private fun pointerMove(id: Int, x: Float, y: Float, world: GameWorld) {
        if (id != joyId) return
        var dx = x - joyOx
        var dy = y - joyOy
        val d = hypot(dx, dy)
        if (d > joyMaxR) {
            dx = dx / d * joyMaxR
            dy = dy / d * joyMaxR
        }
        joyKx = joyOx + dx
        joyKy = joyOy + dy
        val mag = (hypot(dx, dy) / joyMaxR).coerceAtMost(1f)
        if (mag < GameConfig.JOY_DEAD_ZONE) {
            world.input.releaseMove()
        } else {
            val scaled = (mag - GameConfig.JOY_DEAD_ZONE) / (1f - GameConfig.JOY_DEAD_ZONE)
            val len = hypot(dx, dy)
            world.input.moveX = dx / len * scaled
            world.input.moveY = dy / len * scaled
        }
    }

    private fun pointerUp(id: Int, world: GameWorld) {
        when (id) {
            joyId -> {
                joyId = -1
                joyActive = false
                world.input.releaseMove()
            }
            attackId -> {
                attackId = -1
                attackDown = false
            }
            abilityId -> {
                abilityId = -1
                abilityDown = false
            }
            pauseId -> {
                pauseId = -1
                pauseDown = false
                if (!debugFired) world.input.requestPauseToggle()
            }
        }
    }

    /** Call on touch-cancel and when the app pauses so nothing stays "stuck". */
    fun cancelAll(world: GameWorld) {
        joyId = -1; attackId = -1; abilityId = -1; pauseId = -1
        joyActive = false; attackDown = false; abilityDown = false; pauseDown = false
        world.input.releaseMove()
    }

    /** Called every frame from the game thread: holding Pause for ~0.8s toggles the debug panel. */
    fun tick(nowMs: Long) {
        if (pauseId != -1 && !debugFired && nowMs - pauseDownAt >= 800L) {
            debugFired = true
            debugVisible = !debugVisible
        }
    }
}
