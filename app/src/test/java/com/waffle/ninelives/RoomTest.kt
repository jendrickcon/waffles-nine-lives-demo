package com.waffle.ninelives

import com.waffle.ninelives.game.GameConfig
import com.waffle.ninelives.game.Layouts
import com.waffle.ninelives.game.PickupType
import com.waffle.ninelives.game.Room
import com.waffle.ninelives.game.RoomFactory
import com.waffle.ninelives.game.RoomRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class RoomTest {
    @Test
    fun configSizesAgree() {
        assertEquals(GameConfig.COLS * GameConfig.TILE, GameConfig.VIEW_W, 0.001f)
        assertEquals(GameConfig.ROWS * GameConfig.TILE, GameConfig.VIEW_H, 0.001f)
        assertEquals(6f * GameConfig.TILE, GameConfig.DOOR_Y, 0.001f)
    }

    @Test
    fun layoutsHaveCorrectShape() {
        for (l in Layouts.ALL) {
            assertEquals(l.name, 10, l.rows.size)
            for (row in l.rows) assertEquals(l.name, 18, row.length)
        }
    }

    @Test
    fun everyLayoutIsTraversableAndSlotsAreReachable() {
        for (l in Layouts.ALL) {
            val room = Room(1, RoomRole.KILL, l, hasWest = true, hasEast = true)
            val seen = Array(GameConfig.ROWS) { BooleanArray(GameConfig.COLS) }
            val queue = ArrayDeque<Pair<Int, Int>>()
            queue.add(1 to 5)
            seen[5][1] = true
            while (queue.isNotEmpty()) {
                val (cx, cy) = queue.removeFirst()
                for ((dx, dy) in listOf(1 to 0, -1 to 0, 0 to 1, 0 to -1)) {
                    val nx = cx + dx
                    val ny = cy + dy
                    if (nx < 0 || ny < 0 || nx >= GameConfig.COLS || ny >= GameConfig.ROWS) continue
                    val ch = room.tileAt(nx, ny)
                    if (ch == '#' || ch == 'R' || seen[ny][nx]) continue
                    seen[ny][nx] = true
                    queue.add(nx to ny)
                }
            }
            assertTrue(l.name + " east door lane", seen[5][18] && seen[6][18])
            for ((c, r) in l.enemySlots + l.itemSlots) {
                assertEquals(l.name + " slot " + c + "," + r + " is floor", '.', room.tileAt(c, r))
                assertTrue(l.name + " slot reachable " + c + "," + r, seen[r][c])
            }
        }
    }

    @Test
    fun routeHasExpectedShape() {
        for (seed in 1L..20L) {
            val rooms = RoomFactory.buildRoute(Random(seed))
            assertEquals(5, rooms.size)
            assertEquals(
                listOf(RoomRole.START, RoomRole.KILL, RoomRole.KEY, RoomRole.REWARD, RoomRole.BOSS),
                rooms.map { it.role }
            )
            assertFalse(rooms.first().hasWest)
            assertTrue(rooms.first().hasEast)
            assertTrue(rooms.last().hasWest)
            assertFalse(rooms.last().hasEast)
            assertTrue(rooms[2].eastLocked)
            assertTrue(rooms[2].pickups.any { it.type == PickupType.KEY })
            assertTrue(rooms[3].pickups.any { it.type == PickupType.REWARD })
        }
    }

    @Test
    fun killRoomLocksDoorsUntilCleared() {
        val room = RoomFactory.buildRoute(Random(3))[1]
        room.spawnEnemies()
        assertTrue(room.enemiesAlive() >= 2)
        assertTrue(room.blocks(0, 5, false))   // west door closed
        assertTrue(room.blocks(19, 6, false))  // east door closed
        room.cleared = true
        assertFalse(room.blocks(0, 5, false))
        assertFalse(room.blocks(19, 6, false))
    }

    @Test
    fun lockedGateStaysClosedEvenWhenRoomCleared() {
        val room = RoomFactory.buildRoute(Random(3))[2]
        room.cleared = true
        assertTrue(room.blocks(19, 5, false))
        room.eastLocked = false
        assertFalse(room.blocks(19, 5, false))
    }
}
