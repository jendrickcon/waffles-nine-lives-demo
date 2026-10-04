package com.waffle.ninelives.ui

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import com.waffle.ninelives.game.Enemy
import com.waffle.ninelives.game.EnemyState
import com.waffle.ninelives.game.EnemyType
import com.waffle.ninelives.game.GameConfig
import com.waffle.ninelives.game.GameState
import com.waffle.ninelives.game.GameWorld
import com.waffle.ninelives.game.Palette
import com.waffle.ninelives.game.PickupType
import com.waffle.ninelives.game.Room
import com.waffle.ninelives.game.RoomRole
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

/**
 * Draws everything. The game world is drawn in "game units" (320 x 192) and scaled to fit the screen;
 * touch controls are drawn afterwards in real screen pixels.
 * All art is original and made from simple shapes (no image files).
 */
class Renderer {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rect = RectF()
    private val path = Path()
    private val shakeRng = Random(99)
    private var alphaMul = 255 // global fade used for the life-loss "ghost" effect

    init {
        paint.typeface = Typeface.DEFAULT_BOLD
    }

    // ------------------------------------------------------------------ drawing helpers

    private fun col(color: Int): Int {
        if (alphaMul >= 255) return color
        val a = (color ushr 24) * alphaMul / 255
        return Palette.alpha(color, a)
    }

    private fun fillRect(c: Canvas, x: Float, y: Float, w: Float, h: Float, color: Int) {
        paint.style = Paint.Style.FILL
        paint.color = col(color)
        c.drawRect(x, y, x + w, y + h, paint)
    }

    private fun fillCircle(c: Canvas, cx: Float, cy: Float, r: Float, color: Int) {
        paint.style = Paint.Style.FILL
        paint.color = col(color)
        c.drawCircle(cx, cy, r, paint)
    }

    private fun ringCircle(c: Canvas, cx: Float, cy: Float, r: Float, width: Float, color: Int) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = width
        paint.color = col(color)
        c.drawCircle(cx, cy, r, paint)
        paint.style = Paint.Style.FILL
    }

    private fun fillOval(c: Canvas, cx: Float, cy: Float, rx: Float, ry: Float, color: Int) {
        paint.style = Paint.Style.FILL
        paint.color = col(color)
        rect.set(cx - rx, cy - ry, cx + rx, cy + ry)
        c.drawOval(rect, paint)
    }

    private fun line(c: Canvas, x1: Float, y1: Float, x2: Float, y2: Float, w: Float, color: Int) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = w
        paint.strokeCap = Paint.Cap.ROUND
        paint.color = col(color)
        c.drawLine(x1, y1, x2, y2, paint)
        paint.style = Paint.Style.FILL
    }

    private fun triangle(
        c: Canvas, x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float, color: Int
    ) {
        paint.style = Paint.Style.FILL
        paint.color = col(color)
        path.reset()
        path.moveTo(x1, y1)
        path.lineTo(x2, y2)
        path.lineTo(x3, y3)
        path.close()
        c.drawPath(path, paint)
    }

    private fun text(c: Canvas, s: String, x: Float, y: Float, size: Float, color: Int, align: Paint.Align = Paint.Align.CENTER) {
        paint.style = Paint.Style.FILL
        paint.textSize = size
        paint.textAlign = align
        paint.color = col(color)
        c.drawText(s, x, y, paint)
    }

    // ------------------------------------------------------------------ main entry

    fun draw(canvas: Canvas, world: GameWorld, controls: TouchControls) {
        val w = canvas.width.toFloat()
        val h = canvas.height.toFloat()
        alphaMul = 255
        canvas.drawColor(Palette.LETTERBOX)

        val scale = min(w / GameConfig.VIEW_W, h / GameConfig.VIEW_H)
        val ox = (w - GameConfig.VIEW_W * scale) / 2f
        val oy = (h - GameConfig.VIEW_H * scale) / 2f

        canvas.save()
        canvas.translate(ox, oy)
        canvas.scale(scale, scale)
        canvas.clipRect(0f, 0f, GameConfig.VIEW_W, GameConfig.VIEW_H)
        if (world.shake > 0f) {
            canvas.translate(
                (shakeRng.nextFloat() - 0.5f) * 2f * world.shake,
                (shakeRng.nextFloat() - 0.5f) * 2f * world.shake
            )
        }
        drawRoom(canvas, world)
        drawPickups(canvas, world)
        drawEnemies(canvas, world)
        drawProjectiles(canvas, world)
        drawPlayer(canvas, world)
        drawParticles(canvas, world)
        if (world.debugBoxes) drawDebugBoxes(canvas, world)
        drawHud(canvas, world)
        drawOverlays(canvas, world)
        canvas.restore()

        drawControls(canvas, world, controls)
    }

    // ------------------------------------------------------------------ room

    private fun drawRoom(c: Canvas, world: GameWorld) {
        val room = world.currentRoom
        val t = GameConfig.TILE
        for (ty in 0 until GameConfig.ROWS) {
            for (tx in 0 until GameConfig.COLS) {
                val x = tx * t
                val y = ty * t
                val ch = room.tileAt(tx, ty)
                when (ch) {
                    '#' -> drawFence(c, x, y)
                    'W', 'E' -> drawDoorTile(c, room, ch, tx, ty, x, y)
                    else -> {
                        drawFloor(c, tx, ty, x, y)
                        if (ch == 'R') drawBush(c, x, y)
                    }
                }
            }
        }
    }

    private fun drawFloor(c: Canvas, tx: Int, ty: Int, x: Float, y: Float) {
        fillRect(c, x, y, GameConfig.TILE, GameConfig.TILE, if ((tx + ty) % 2 == 0) Palette.FLOOR_A else Palette.FLOOR_B)
        if ((tx * 7 + ty * 13) % 11 == 0) {
            fillRect(c, x + 4f, y + 9f, 2f, 1f, Palette.FLOOR_SPECK)
            fillRect(c, x + 10f, y + 4f, 1f, 1f, Palette.FLOOR_SPECK)
        }
    }

    private fun drawFence(c: Canvas, x: Float, y: Float) {
        val t = GameConfig.TILE
        fillRect(c, x, y, t, t, Palette.FENCE)
        var px = 0f
        while (px < t) {
            fillRect(c, x + px, y, 1f, t, Palette.FENCE_DARK)
            px += 4f
        }
        fillRect(c, x, y, t, 2f, Palette.FENCE_LIGHT)
    }

    private fun drawBush(c: Canvas, x: Float, y: Float) {
        val t = GameConfig.TILE
        fillOval(c, x + t / 2f, y + t - 3f, 7f, 2.5f, Palette.SHADOW)
        fillCircle(c, x + 5f, y + 9f, 5.5f, Palette.BUSH_DARK)
        fillCircle(c, x + 11f, y + 9f, 5.5f, Palette.BUSH_DARK)
        fillCircle(c, x + 8f, y + 6f, 6f, Palette.BUSH)
        fillCircle(c, x + 6f, y + 5f, 1.5f, Palette.alpha(Palette.WHITE, 60))
    }

    private fun drawDoorTile(c: Canvas, room: Room, ch: Char, tx: Int, ty: Int, x: Float, y: Float) {
        val t = GameConfig.TILE
        drawFloor(c, tx, ty, x, y)
        val open = if (ch == 'W') room.westOpen else room.eastOpen
        if (open) {
            val dir = if (ch == 'W') -1f else 1f
            val cx = x + t / 2f
            val cy = y + t / 2f
            triangle(c, cx - dir * 3f, cy - 4f, cx - dir * 3f, cy + 4f, cx + dir * 4f, cy, Palette.alpha(Palette.LOCK, 150))
        } else {
            fillRect(c, x + 1f, y, t - 2f, t, Palette.GATE)
            var py = 3f
            while (py < t) {
                fillRect(c, x + 1f, y + py, t - 2f, 1f, Palette.GATE_DARK)
                py += 4f
            }
            fillRect(c, x + 1f, y, 2f, t, Palette.GATE_DARK)
            fillRect(c, x + t - 3f, y, 2f, t, Palette.GATE_DARK)
            if (ch == 'E' && room.eastLocked && ty == GameConfig.DOOR_ROW_A) {
                val lx = x + t / 2f
                val ly = y + t
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 1.5f
                paint.color = col(Palette.LOCK)
                rect.set(lx - 3f, ly - 8f, lx + 3f, ly - 2f)
                c.drawArc(rect, 180f, 180f, false, paint)
                paint.style = Paint.Style.FILL
                fillRect(c, lx - 4.5f, ly - 5f, 9f, 7f, Palette.LOCK)
                fillCircle(c, lx, ly - 2f, 1f, Palette.GATE_DARK)
            }
        }
    }

    // ------------------------------------------------------------------ pickups

    private fun drawHeart(c: Canvas, cx: Float, cy: Float, s: Float, color: Int) {
        fillCircle(c, cx - s * 0.5f, cy - s * 0.25f, s * 0.55f, color)
        fillCircle(c, cx + s * 0.5f, cy - s * 0.25f, s * 0.55f, color)
        triangle(c, cx - s * 1.02f, cy - s * 0.05f, cx + s * 1.02f, cy - s * 0.05f, cx, cy + s, color)
    }

    private fun drawPickups(c: Canvas, world: GameWorld) {
        for (pk in world.currentRoom.pickups) {
            if (pk.taken) continue
            val bob = sin(world.time * 4f + pk.x) * 1.5f
            val x = pk.x
            val y = pk.y + bob
            when (pk.type) {
                PickupType.HEART -> {
                    fillOval(c, x, pk.y + 7f, 5f, 1.8f, Palette.SHADOW)
                    drawHeart(c, x, y, 5f, Palette.HEART)
                }
                PickupType.KEY -> {
                    fillOval(c, x, pk.y + 8f, 5f, 1.8f, Palette.SHADOW)
                    ringCircle(c, x - 3f, y - 2f, 3.5f, 2f, Palette.KEY)
                    line(c, x, y, x + 6f, y + 6f, 2f, Palette.KEY)
                    line(c, x + 4f, y + 4f, x + 6f, y + 2f, 1.5f, Palette.KEY)
                    fillCircle(c, x - 4f, y - 3f, 1f, Palette.alpha(Palette.WHITE, 160))
                }
                PickupType.REWARD -> {
                    fillOval(c, x, pk.y + 8f, 6f, 2f, Palette.SHADOW)
                    fillCircle(c, x, y - 1f, 5.5f, Palette.SILVER_DARK)
                    fillCircle(c, x - 0.5f, y - 1.5f, 5f, Palette.SILVER)
                    fillRect(c, x - 6f, y + 2f, 12f, 2.5f, Palette.SILVER_DARK)
                    fillCircle(c, x, y + 6f, 1.6f, Palette.SILVER_DARK)
                    fillCircle(c, x - 2f, y - 3f, 1.2f, Palette.WHITE)
                    val tw = (sin(world.time * 6f) + 1f) * 0.5f
                    fillCircle(c, x + 7f, y - 6f, 0.8f + tw, Palette.alpha(Palette.WHITE, 200))
                }
                PickupType.EXIT -> {
                    val pulse = 0.5f + 0.5f * sin(world.time * 5f)
                    fillCircle(c, x, pk.y, 15f + pulse * 3f, Palette.alpha(Palette.ORANGE, 60))
                    fillRect(c, x - 9f, pk.y - 10f, 18f, 20f, Palette.GATE)
                    fillRect(c, x - 6f, pk.y - 6f, 12f, 12f, Palette.GATE_DARK)
                    fillRect(c, x - 4f, pk.y - 3f, 8f, 6f, Palette.alpha(Palette.CREAM, 230))
                    text(c, "HOME?", x, pk.y - 14f, 6f, Palette.TEXT)
                }
            }
        }
    }

    // ------------------------------------------------------------------ Waffle

    private fun drawPlayer(c: Canvas, world: GameWorld) {
        val p = world.player
        var x = p.x
        var y = p.y
        val spirit = world.abilityUnlocked
        val oldAlpha = alphaMul

        if (world.state == GameState.LIFE_LOSS) {
            val t = 1f - world.stateTimer / GameConfig.LIFE_LOSS_TIME
            alphaMul = ((1f - t) * 255f).toInt().coerceIn(0, 255)
            y -= t * 22f
        } else if (world.state == GameState.TRANSFORM) {
            return // the transformation screen draws its own big Waffle
        } else if (p.invuln > 0f && !p.isPouncing && ((world.time * 14f).toInt() % 2 == 0)) {
            alphaMul = 110 // blink while invulnerable
        }
        if (p.isPouncing) { // stretch while dashing
            drawWaffle(c, x, y, p.faceX, p.faceY, 1.0f, spirit, p.walkPhase, world.time, false, 1.2f)
        } else {
            drawWaffle(c, x, y, p.faceX, p.faceY, 1.0f, spirit, p.walkPhase, world.time, p.hitFlash > 0f, 1.0f)
        }
        alphaMul = oldAlpha

        // Claw swipe arc
        if (p.swipeTime > 0f) {
            val frac = p.swipeTime / GameConfig.SWIPE_DURATION
            val ang = (atan2(p.faceY, p.faceX) * 180f / PI.toFloat())
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 2.5f
            paint.color = Palette.alpha(Palette.WHITE, (frac * 255f).toInt())
            val r = GameConfig.SWIPE_RANGE
            rect.set(p.x - r, p.y - r, p.x + r, p.y + r)
            c.drawArc(rect, ang - 55f, 110f, false, paint)
            paint.strokeWidth = 1.5f
            rect.set(p.x - r + 4f, p.y - r + 4f, p.x + r - 4f, p.y + r - 4f)
            c.drawArc(rect, ang - 45f, 90f, false, paint)
            paint.style = Paint.Style.FILL
        }
    }

    /** Waffle: a fluffy, chubby orange cat. Drawn facing right and mirrored for left. */
    private fun drawWaffle(
        c: Canvas, x: Float, y: Float, fx: Float, fy: Float, scale: Float,
        spirit: Boolean, phase: Float, time: Float, flash: Boolean, stretch: Float
    ) {
        c.save()
        c.translate(x, y)
        val mirror = if (fx < -0.2f) -1f else 1f
        c.scale(scale * mirror * stretch, scale / (if (stretch > 1f) 1.15f else 1f))
        val bob = sin(phase) * 0.8f
        val headDy = fy * 1.5f

        fillOval(c, 0f, 7f, 8.5f, 2.5f, Palette.SHADOW)
        if (spirit) {
            val pulse = 0.5f + 0.5f * sin(time * 4f)
            fillCircle(c, 0f, 0f, 11f + pulse * 1.5f, Palette.alpha(Palette.SPIRIT, 45))
        }
        // tail
        val tipY = -8f + sin(time * 5f) * 1.5f
        line(c, -7f, 1f, -11f, -3f, 3f, Palette.ORANGE)
        line(c, -11f, -3f, -10f, tipY, 3f, Palette.ORANGE)
        line(c, -10.5f, -1f, -11.5f, -4.5f, 1.2f, Palette.ORANGE_DARK)
        if (spirit) {
            fillCircle(c, -10f, tipY, 5f, Palette.alpha(Palette.SPIRIT, 90))
            fillCircle(c, -10f, tipY, 2.6f, Palette.SPIRIT)
        }
        // paws
        fillOval(c, -4f, 6f + bob, 2.8f, 2f, Palette.CREAM)
        fillOval(c, 4f, 6f - bob, 2.8f, 2f, Palette.CREAM)
        // body
        fillOval(c, 0f, 1f, 8.5f, 6.5f, Palette.ORANGE)
        fillOval(c, 0.5f, 3.5f, 5.5f, 3.4f, Palette.CREAM)
        line(c, -4f, -4f, -3f, -1.5f, 1.2f, Palette.ORANGE_DARK)
        line(c, -1f, -5f, 0f, -2.5f, 1.2f, Palette.ORANGE_DARK)
        line(c, 2f, -4.5f, 3f, -2f, 1.2f, Palette.ORANGE_DARK)
        // head
        val hx = 4f
        val hy = -4f + headDy
        fillCircle(c, hx, hy, 5.8f, Palette.ORANGE)
        triangle(c, hx - 5f, hy - 2f, hx - 3.5f, hy - 8.5f, hx - 0.5f, hy - 4f, Palette.ORANGE)
        triangle(c, hx + 0.5f, hy - 4f, hx + 3.5f, hy - 8.5f, hx + 5f, hy - 2f, Palette.ORANGE)
        triangle(c, hx - 4f, hy - 3f, hx - 3.3f, hy - 6.5f, hx - 1.5f, hy - 4f, Palette.PINK)
        triangle(c, hx + 1.5f, hy - 4f, hx + 3.3f, hy - 6.5f, hx + 4f, hy - 3f, Palette.PINK)
        fillOval(c, hx + 1.5f, hy + 3f, 3.6f, 2.4f, Palette.CREAM)
        val eyeColor = if (spirit) Palette.SPIRIT_DARK else Palette.EYE
        fillCircle(c, hx + 1f, hy - 0.5f, 1.2f, eyeColor)
        fillCircle(c, hx + 4.2f, hy - 0.5f, 1.2f, eyeColor)
        fillCircle(c, hx + 0.6f, hy - 0.9f, 0.4f, Palette.WHITE)
        fillCircle(c, hx + 3.8f, hy - 0.9f, 0.4f, Palette.WHITE)
        fillCircle(c, hx + 5.2f, hy + 1.3f, 0.9f, Palette.PINK)
        line(c, hx + 4.5f, hy + 2f, hx + 8.5f, hy + 1f, 0.5f, Palette.alpha(Palette.WHITE, 200))
        line(c, hx + 4.5f, hy + 2.6f, hx + 8.5f, hy + 3.6f, 0.5f, Palette.alpha(Palette.WHITE, 200))
        if (flash) {
            fillOval(c, 0f, 1f, 8.5f, 6.5f, Palette.alpha(Palette.WHITE, 190))
            fillCircle(c, hx, hy, 5.8f, Palette.alpha(Palette.WHITE, 190))
        }
        c.restore()
    }

    // ------------------------------------------------------------------ enemies

    private fun drawEnemies(c: Canvas, world: GameWorld) {
        for (e in world.currentRoom.enemies) {
            if (!e.alive) continue
            when (e.type) {
                EnemyType.CROW -> drawCrow(c, e, world.time)
                else -> drawDog(c, e, world.time, e.type == EnemyType.BOSS)
            }
            val telegraph = e.state == EnemyState.WINDUP || e.state == EnemyState.AIM
            if (telegraph) {
                val pulse = 0.5f + 0.5f * sin(world.time * 25f)
                ringCircle(c, e.x, e.y, e.r + 3f + pulse * 2f, 1.2f, Palette.RED)
                text(c, "!", e.x, e.y - e.r - 6f, 10f, Palette.RED)
            }
        }
    }

    private fun drawDog(c: Canvas, e: Enemy, time: Float, boss: Boolean) {
        val body = if (boss) Palette.BOSS else Palette.DOG
        val dark = if (boss) Palette.BOSS_DARK else Palette.DOG_DARK
        val light = if (boss) Palette.BOSS_LIGHT else Palette.DOG_LIGHT
        val sc = if (boss) 1.9f else 1f
        val mirror = if (e.dirX < 0f) -1f else 1f
        val stretch = if (e.state == EnemyState.LUNGE) 1.15f else 1f
        c.save()
        c.translate(e.x, e.y)
        c.scale(sc * mirror * stretch, sc)
        fillOval(c, 0f, 5.5f, 8f, 2.4f, Palette.SHADOW)
        val wag = sin(time * 14f) * 2f
        line(c, -7f, -1f, -10f, -4f + wag, 2f, dark)
        fillRect(c, -5f, 3f, 2.5f, 3f, dark)
        fillRect(c, 2f, 3f, 2.5f, 3f, dark)
        fillOval(c, 0f, 0f, 7.5f, 5f, body)
        fillOval(c, 0.5f, 2.2f, 5f, 2.4f, light)
        fillCircle(c, 6f, -2f, 4.5f, body)
        fillOval(c, 10f, -0.5f, 2.8f, 2.1f, light)
        fillCircle(c, 12f, -1f, 1f, Palette.EYE)
        fillOval(c, 4f, -5f, 1.8f, 3f, dark)
        fillCircle(c, 7.5f, -3.4f, 0.9f, Palette.EYE)
        if (boss) {
            triangle(c, 1f, -4.5f, 3f, -4.5f, 2f, -7f, Palette.LOCK)
            triangle(c, 3.5f, -4.5f, 5.5f, -4.5f, 4.5f, -7f, Palette.LOCK)
            fillRect(c, 1f, -4.5f, 4.5f, 1.6f, Palette.GATE_DARK)
        }
        if (e.hitFlash > 0f) {
            fillOval(c, 0f, 0f, 7.5f, 5f, Palette.alpha(Palette.WHITE, 200))
            fillCircle(c, 6f, -2f, 4.5f, Palette.alpha(Palette.WHITE, 200))
        }
        c.restore()
    }

    private fun drawCrow(c: Canvas, e: Enemy, time: Float) {
        val mirror = if (e.dirX < 0f) -1f else 1f
        val hover = sin(time * 6f + e.spawnX) * 1.5f
        fillOval(c, e.x, e.y + 9f, 5f, 1.8f, Palette.SHADOW)
        c.save()
        c.translate(e.x, e.y - 2f + hover)
        c.scale(mirror, 1f)
        val flap = sin(time * 18f) * 3.2f
        fillOval(c, -1f, -3f + flap, 3f, 5f, Palette.CROW_LIGHT)
        fillOval(c, 0f, 0f, 5.5f, 4f, Palette.CROW)
        fillOval(c, -5f, 1f, 3f, 1.6f, Palette.CROW)
        fillCircle(c, 4f, -2f, 2.8f, Palette.CROW)
        triangle(c, 6f, -3f, 10f, -1.5f, 6f, -0.2f, Palette.BEAK)
        fillCircle(c, 4.6f, -2.8f, 0.9f, Palette.WHITE)
        fillCircle(c, 4.8f, -2.8f, 0.4f, Palette.EYE)
        if (e.hitFlash > 0f) fillOval(c, 0f, 0f, 5.5f, 4f, Palette.alpha(Palette.WHITE, 200))
        c.restore()
    }

    private fun drawProjectiles(c: Canvas, world: GameWorld) {
        for (pr in world.projectiles) {
            if (!pr.active) continue
            c.save()
            c.translate(pr.x, pr.y)
            c.rotate(atan2(pr.vy, pr.vx) * 180f / PI.toFloat())
            fillOval(c, 0f, 0f, 4f, 1.6f, Palette.CROW_LIGHT)
            fillOval(c, 1f, 0f, 2f, 0.8f, Palette.WHITE)
            c.restore()
        }
    }

    private fun drawParticles(c: Canvas, world: GameWorld) {
        val ps = world.particles
        for (i in 0 until ps.capacity) {
            val life = ps.life[i]
            if (life <= 0f) continue
            val a = (255f * min(1f, life / ps.maxLife[i])).toInt()
            val s = ps.size[i]
            fillRect(c, ps.x[i] - s / 2f, ps.y[i] - s / 2f, s, s, Palette.alpha(ps.color[i], a))
        }
    }

    private fun drawDebugBoxes(c: Canvas, world: GameWorld) {
        val t = GameConfig.TILE
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 0.6f
        val room = world.currentRoom
        paint.color = Palette.alpha(Palette.RED, 140)
        for (ty in 0 until GameConfig.ROWS) {
            for (tx in 0 until GameConfig.COLS) {
                if (room.blocks(tx, ty, false)) c.drawRect(tx * t, ty * t, (tx + 1) * t, (ty + 1) * t, paint)
            }
        }
        paint.color = Palette.alpha(Palette.SPIRIT, 255)
        val p = world.player
        c.drawRect(p.x - p.r, p.y - p.r, p.x + p.r, p.y + p.r, paint)
        paint.color = Palette.alpha(Palette.LOCK, 255)
        for (e in room.enemies) if (e.alive) c.drawRect(e.x - e.r, e.y - e.r, e.x + e.r, e.y + e.r, paint)
        paint.style = Paint.Style.FILL
        text(
            c,
            "room ${room.index} ${room.role.name} ${room.layout.name} lives=${world.run.livesRemaining} hp=${p.health}/${world.run.maxHealth}",
            4f, GameConfig.VIEW_H - 4f, 6f, Palette.TEXT, Paint.Align.LEFT
        )
    }

    // ------------------------------------------------------------------ HUD

    private fun drawHud(c: Canvas, world: GameWorld) {
        if (world.state == GameState.INTRO) return
        val run = world.run
        val hearts = run.maxHealth / 2
        for (i in 0 until hearts) {
            val hx = 10f + i * 12f
            val hp = world.player.health - i * 2
            drawHeart(c, hx, 9f, 4.2f, Palette.HEART_DARK)
            if (hp >= 2) {
                drawHeart(c, hx, 9f, 4.2f, Palette.HEART)
            } else if (hp == 1) {
                c.save()
                c.clipRect(hx - 6f, 0f, hx, 24f)
                drawHeart(c, hx, 9f, 4.2f, Palette.HEART)
                c.restore()
            }
        }
        // lives counter: a little cat head and the number
        fillCircle(c, 9f, 25f, 5f, Palette.ORANGE)
        triangle(c, 4.5f, 23f, 5.5f, 17.5f, 8f, 21f, Palette.ORANGE)
        triangle(c, 13.5f, 23f, 12.5f, 17.5f, 10f, 21f, Palette.ORANGE)
        fillCircle(c, 7.3f, 25f, 0.8f, Palette.EYE)
        fillCircle(c, 10.7f, 25f, 0.8f, Palette.EYE)
        text(c, "x${run.livesRemaining}", 17f, 29f, 11f, Palette.TEXT, Paint.Align.LEFT)
        if (run.hasKey) {
            ringCircle(c, 44f, 24f, 3f, 1.6f, Palette.KEY)
            line(c, 46.5f, 26f, 52f, 31f, 1.6f, Palette.KEY)
        }
        val room = world.currentRoom
        text(c, "${room.index + 1}/${world.rooms.size}", GameConfig.VIEW_W / 2f, 9f, 6f, Palette.alpha(Palette.TEXT, 190))

        if (room.role == RoomRole.BOSS && world.state == GameState.PLAYING) {
            for (e in room.enemies) {
                if (e.type == EnemyType.BOSS && e.alive) {
                    fillRect(c, 60f, 179f, 200f, 6f, Palette.UI_BG)
                    fillRect(c, 61f, 180f, 198f * e.hp / e.type.maxHp, 4f, Palette.RED)
                    text(c, "DUKE", GameConfig.VIEW_W / 2f, 177f, 6f, Palette.TEXT)
                }
            }
        }
        if (world.bannerTimer > 0f && world.banner.isNotEmpty() && world.state == GameState.PLAYING) {
            val a = (min(1f, world.bannerTimer / 0.4f) * 255f).toInt()
            val hasSub = world.bannerSub.isNotEmpty()
            fillRect(c, 70f, 36f, 180f, if (hasSub) 26f else 17f, Palette.alpha(Palette.UI_BG, a * 200 / 255))
            text(c, world.banner, GameConfig.VIEW_W / 2f, 48f, 10f, Palette.alpha(Palette.TEXT, a))
            if (hasSub) text(c, world.bannerSub, GameConfig.VIEW_W / 2f, 58f, 6.5f, Palette.alpha(Palette.TEXT_DIM, a))
        }
    }

    // ------------------------------------------------------------------ full-screen states

    private fun dim(c: Canvas, alpha: Int) {
        fillRect(c, 0f, 0f, GameConfig.VIEW_W, GameConfig.VIEW_H, Palette.alpha(Palette.LETTERBOX, alpha))
    }

    private fun drawOverlays(c: Canvas, world: GameWorld) {
        val cx = GameConfig.VIEW_W / 2f
        val pulse = 0.55f + 0.45f * sin(world.time * 4f)
        when (world.state) {
            GameState.INTRO -> {
                dim(c, 235)
                drawWaffle(c, 78f, 100f, 1f, 0f, 3.4f, false, 0f, world.time, false, 1f)
                text(c, "WAFFLE'S", 214f, 52f, 18f, Palette.TEXT)
                text(c, "NINE LIVES", 214f, 72f, 18f, Palette.ORANGE)
                text(c, "Waffle was looking for the way out,", 214f, 96f, 7f, Palette.TEXT_DIM)
                text(c, "and now he is far, far from home.", 214f, 106f, 7f, Palette.TEXT_DIM)
                text(c, "Fight, pounce, and find the way back!", 214f, 120f, 7f, Palette.TEXT_DIM)
                text(c, "TAP TO START", 214f, 148f, 10f, Palette.alpha(Palette.TEXT, (pulse * 255f).toInt()))
            }
            GameState.LIFE_LOSS -> {
                val t = 1f - world.stateTimer / GameConfig.LIFE_LOSS_TIME
                dim(c, (t * 170f).toInt())
                text(c, "LIFE LOST", cx, 72f, 16f, Palette.alpha(Palette.DAMAGE, (min(1f, t * 3f) * 255f).toInt()))
                val from = world.run.livesRemaining
                text(c, "$from  >  ${from - 1}", cx, 104f, 24f, Palette.alpha(Palette.TEXT, (min(1f, t * 2.5f) * 255f).toInt()))
            }
            GameState.TRANSFORM -> drawTransform(c, world)
            GameState.TRUE_DEATH -> {
                dim(c, 225)
                text(c, "ALL NINE LIVES USED", cx, 78f, 16f, Palette.DAMAGE)
                text(c, "Waffle is still out there somewhere...", cx, 98f, 8f, Palette.TEXT_DIM)
                text(c, "TAP TO TRY AGAIN", cx, 130f, 10f, Palette.alpha(Palette.TEXT, (pulse * 255f).toInt()))
            }
            GameState.VICTORY -> {
                dim(c, 225)
                drawWaffle(c, cx, 62f, 1f, 0f, 2.6f, world.abilityUnlocked, 0f, world.time, false, 1f)
                text(c, "THE CAT FLAP!", cx, 112f, 16f, Palette.ORANGE)
                text(c, "Waffle squeezed through... and landed somewhere", cx, 128f, 7f, Palette.TEXT_DIM)
                text(c, "that looks almost like home. Almost.", cx, 138f, 7f, Palette.TEXT_DIM)
                text(c, "TAP TO PLAY AGAIN", cx, 164f, 9f, Palette.alpha(Palette.TEXT, (pulse * 255f).toInt()))
            }
            else -> {}
        }
    }

    private fun drawTransform(c: Canvas, world: GameWorld) {
        val cx = GameConfig.VIEW_W / 2f
        val t = 1f - world.stateTimer / GameConfig.TRANSFORM_TIME
        dim(c, 215)
        val changed = t > 0.35f
        if (changed) {
            for (k in 0 until 3) {
                val f = (t * 1.5f + k * 0.33f) % 1f
                ringCircle(c, cx, 98f, f * 95f, 2f, Palette.alpha(Palette.SPIRIT, ((1f - f) * 190f).toInt()))
            }
        }
        drawWaffle(c, cx, 98f, 1f, 0f, 3.6f, changed, t * 6f, world.time, false, 1f)
        if (t in 0.35f..0.5f) {
            val a = ((1f - (t - 0.35f) / 0.15f) * 255f).toInt()
            dim(c, 0)
            fillRect(c, 0f, 0f, GameConfig.VIEW_W, GameConfig.VIEW_H, Palette.alpha(Palette.WHITE, a))
        }
        val lives = world.run.livesRemaining
        text(c, "LIFE LOST   $lives LEFT", cx, 28f, 9f, Palette.TEXT_DIM)
        if (changed) {
            val up = world.lastUpgrade
            text(c, (up?.name ?: "TRANSFORMED").uppercase(), cx, 52f, 17f, Palette.SPIRIT)
            text(c, up?.description ?: "", cx, 164f, 7f, Palette.TEXT)
        }
    }

    // ------------------------------------------------------------------ touch controls (screen pixels)

    private fun drawControls(c: Canvas, world: GameWorld, tc: TouchControls) {
        if (world.state == GameState.INTRO) {
            drawPause(c, tc)
            return
        }
        // joystick
        if (tc.joyActive) {
            fillCircle(c, tc.joyOx, tc.joyOy, tc.joyMaxR, Palette.alpha(Palette.WHITE, 40))
            ringCircle(c, tc.joyOx, tc.joyOy, tc.joyMaxR, 3f, Palette.alpha(Palette.WHITE, 110))
            fillCircle(c, tc.joyKx, tc.joyKy, tc.joyMaxR * 0.42f, Palette.alpha(Palette.WHITE, 140))
        } else {
            ringCircle(c, tc.joyHomeX, tc.joyHomeY, tc.joyMaxR, 3f, Palette.alpha(Palette.WHITE, 45))
            fillCircle(c, tc.joyHomeX, tc.joyHomeY, tc.joyMaxR * 0.42f, Palette.alpha(Palette.WHITE, 40))
        }
        // attack button with a paw print
        val aBase = if (tc.attackDown) Palette.ORANGE_DARK else Palette.ORANGE
        fillCircle(c, tc.attackX, tc.attackY, tc.attackR, Palette.alpha(aBase, 190))
        ringCircle(c, tc.attackX, tc.attackY, tc.attackR, 4f, Palette.alpha(Palette.WHITE, 140))
        val u = tc.attackR / 46f
        fillOval(c, tc.attackX, tc.attackY + 8f * u, 13f * u, 10f * u, Palette.alpha(Palette.CREAM, 235))
        fillCircle(c, tc.attackX - 15f * u, tc.attackY - 6f * u, 5.5f * u, Palette.alpha(Palette.CREAM, 235))
        fillCircle(c, tc.attackX - 5f * u, tc.attackY - 15f * u, 5.5f * u, Palette.alpha(Palette.CREAM, 235))
        fillCircle(c, tc.attackX + 5f * u, tc.attackY - 15f * u, 5.5f * u, Palette.alpha(Palette.CREAM, 235))
        fillCircle(c, tc.attackX + 15f * u, tc.attackY - 6f * u, 5.5f * u, Palette.alpha(Palette.CREAM, 235))
        // ability button (hidden until the first transformation)
        if (world.abilityUnlocked) {
            val ready = world.player.pounceCooldown <= 0f
            val base = if (ready) Palette.SPIRIT_DARK else Palette.BOSS_DARK
            fillCircle(c, tc.abilityX, tc.abilityY, tc.abilityR, Palette.alpha(base, 200))
            ringCircle(c, tc.abilityX, tc.abilityY, tc.abilityR, 3f, Palette.alpha(Palette.SPIRIT, if (ready) 230 else 90))
            if (!ready) {
                val frac = world.player.pounceCooldown / GameConfig.SPIRIT_COOLDOWN
                paint.style = Paint.Style.FILL
                paint.color = Palette.alpha(Palette.LETTERBOX, 150)
                rect.set(tc.abilityX - tc.abilityR, tc.abilityY - tc.abilityR, tc.abilityX + tc.abilityR, tc.abilityY + tc.abilityR)
                c.drawArc(rect, -90f, 360f * frac, true, paint)
            }
            val s = tc.abilityR / 30f
            triangle(c, tc.abilityX - 9f * s, tc.abilityY - 10f * s, tc.abilityX - 9f * s, tc.abilityY + 10f * s, tc.abilityX + 11f * s, tc.abilityY, Palette.alpha(Palette.WHITE, if (ready) 240 else 120))
            triangle(c, tc.abilityX - 17f * s, tc.abilityY - 6f * s, tc.abilityX - 17f * s, tc.abilityY + 6f * s, tc.abilityX - 9f * s, tc.abilityY, Palette.alpha(Palette.SPIRIT, if (ready) 220 else 90))
        }
        drawPause(c, tc)

        if (tc.debugVisible) {
            for (b in tc.debugButtons) {
                paint.style = Paint.Style.FILL
                paint.color = Palette.alpha(Palette.LETTERBOX, 220)
                c.drawRoundRect(b.rect, 6f, 6f, paint)
                text(c, b.label, b.rect.centerX(), b.rect.centerY() + 5f * tc.pauseR / 20f, 13f * tc.pauseR / 20f, Palette.LOCK)
            }
        }
        if (world.paused) drawPauseMenu(c, tc)
    }

    private fun drawPause(c: Canvas, tc: TouchControls) {
        val a = if (tc.pauseDown) 230 else 140
        fillCircle(c, tc.pauseX, tc.pauseY, tc.pauseR, Palette.alpha(Palette.LETTERBOX, 150))
        ringCircle(c, tc.pauseX, tc.pauseY, tc.pauseR, 2.5f, Palette.alpha(Palette.WHITE, a))
        val b = tc.pauseR * 0.42f
        fillRect(c, tc.pauseX - b * 0.9f, tc.pauseY - b, b * 0.6f, b * 2f, Palette.alpha(Palette.WHITE, a + 20))
        fillRect(c, tc.pauseX + b * 0.3f, tc.pauseY - b, b * 0.6f, b * 2f, Palette.alpha(Palette.WHITE, a + 20))
    }

    private fun drawPauseMenu(c: Canvas, tc: TouchControls) {
        fillRect(c, 0f, 0f, tc.screenW, tc.screenH, Palette.alpha(Palette.LETTERBOX, 200))
        val u = tc.pauseR / 20f
        text(c, "PAUSED", tc.screenW / 2f, tc.screenH / 2f - 56f * u, 34f * u, Palette.TEXT)
        for (pair in listOf(tc.resumeRect to "RESUME", tc.restartRect to "RESTART")) {
            paint.style = Paint.Style.FILL
            paint.color = Palette.alpha(if (pair.second == "RESUME") Palette.ORANGE else Palette.BOSS_DARK, 240)
            c.drawRoundRect(pair.first, 14f * u, 14f * u, paint)
            text(c, pair.second, pair.first.centerX(), pair.first.centerY() + 8f * u, 22f * u, Palette.TEXT)
        }
    }
}
