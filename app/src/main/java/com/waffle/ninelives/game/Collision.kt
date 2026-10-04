package com.waffle.ninelives.game

import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max

/**
 * Axis-separated box-vs-tile collision. Bodies are squares of half-width `r`.
 * We move in small sub-steps so fast dashes can never skip through a wall,
 * and we resolve X first, then Y, so bodies slide along walls instead of sticking.
 */
object Collision {
    private const val MAX_STEP = 4f
    private const val EPS = 0.001f

    /** Moves [b] by (dx,dy). Returns true if it bumped into something. */
    fun move(room: Room, b: Body, dx: Float, dy: Float, flying: Boolean = false): Boolean {
        val biggest = max(abs(dx), abs(dy))
        if (biggest == 0f) return false
        val steps = max(1, ceil(biggest / MAX_STEP).toInt())
        val sx = dx / steps
        val sy = dy / steps
        var bumped = false
        for (i in 0 until steps) {
            if (sx != 0f) {
                b.x += sx
                if (resolveX(room, b, sx, flying)) bumped = true
            }
            if (sy != 0f) {
                b.y += sy
                if (resolveY(room, b, sy, flying)) bumped = true
            }
        }
        return bumped
    }

    private fun resolveX(room: Room, b: Body, sx: Float, flying: Boolean): Boolean {
        val t = GameConfig.TILE
        val minTx = floor((b.x - b.r) / t).toInt()
        val maxTx = floor((b.x + b.r - EPS) / t).toInt()
        val minTy = floor((b.y - b.r) / t).toInt()
        val maxTy = floor((b.y + b.r - EPS) / t).toInt()
        var hit = false
        var best = 0f
        for (ty in minTy..maxTy) {
            for (tx in minTx..maxTx) {
                if (room.blocks(tx, ty, flying)) {
                    val candidate = if (sx > 0f) tx * t - b.r else (tx + 1) * t + b.r
                    if (!hit || (sx > 0f && candidate < best) || (sx < 0f && candidate > best)) {
                        best = candidate
                        hit = true
                    }
                }
            }
        }
        if (hit) b.x = best
        return hit
    }

    private fun resolveY(room: Room, b: Body, sy: Float, flying: Boolean): Boolean {
        val t = GameConfig.TILE
        val minTx = floor((b.x - b.r) / t).toInt()
        val maxTx = floor((b.x + b.r - EPS) / t).toInt()
        val minTy = floor((b.y - b.r) / t).toInt()
        val maxTy = floor((b.y + b.r - EPS) / t).toInt()
        var hit = false
        var best = 0f
        for (ty in minTy..maxTy) {
            for (tx in minTx..maxTx) {
                if (room.blocks(tx, ty, flying)) {
                    val candidate = if (sy > 0f) ty * t - b.r else (ty + 1) * t + b.r
                    if (!hit || (sy > 0f && candidate < best) || (sy < 0f && candidate > best)) {
                        best = candidate
                        hit = true
                    }
                }
            }
        }
        if (hit) b.y = best
        return hit
    }
}
