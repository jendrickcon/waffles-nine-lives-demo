package com.waffle.ninelives.game

import kotlin.random.Random

/** What a room is for. `killAll` rooms lock their doors until every enemy is defeated. */
enum class RoomRole(val killAll: Boolean, val title: String, val hint: String) {
    START(false, "The Neighborhood", "Waffle is lost. Find the way home!"),
    KILL(true, "Territorial Dogs!", "Chase them off to open the gate"),
    KEY(false, "Crow Alley", "Grab the shiny key"),
    REWARD(false, "Quiet Garden", "Catch your breath. Something shiny here."),
    BOSS(true, "Duke the Bulldog", "He is blocking the way home!")
}

/**
 * A hand-authored room shape. `rows` is the INSIDE of the room: 10 rows of 18 characters.
 * '.' = floor, 'R' = bush (solid). The outer fence and the doors are added automatically.
 * Slots are tile (column,row) positions where enemies / items may be placed.
 */
class Layout(
    val name: String,
    val rows: List<String>,
    val enemySlots: List<Pair<Int, Int>>,
    val itemSlots: List<Pair<Int, Int>>
)

object Layouts {
    val OPEN = Layout(
        "open",
        listOf(
            "..................",
            "..................",
            "....R.............",
            "..................",
            "..................",
            "..................",
            "..................",
            ".............R....",
            "..................",
            ".................."
        ),
        emptyList(),
        listOf(10 to 6, 14 to 9)
    )

    val PILLARS = Layout(
        "pillars",
        listOf(
            "..................",
            "..................",
            "...RR........RR...",
            "...RR........RR...",
            "..................",
            "..................",
            "...RR........RR...",
            "...RR........RR...",
            "..................",
            ".................."
        ),
        listOf(16 to 3, 16 to 9, 10 to 3, 10 to 9, 12 to 6, 8 to 6),
        listOf(10 to 6, 10 to 3)
    )

    val DIVIDER = Layout(
        "divider",
        listOf(
            "..................",
            "........RR........",
            "........RR........",
            "........RR........",
            "..................",
            "..................",
            "........RR........",
            "........RR........",
            "........RR........",
            ".................."
        ),
        listOf(13 to 3, 16 to 3, 13 to 9, 16 to 9, 15 to 6, 6 to 3, 6 to 9),
        listOf(10 to 6, 13 to 6)
    )

    val CORNERS = Layout(
        "corners",
        listOf(
            "RR..............RR",
            "R................R",
            "..................",
            "..................",
            "..................",
            "..................",
            "..................",
            "..................",
            "R................R",
            "RR..............RR"
        ),
        listOf(6 to 3, 14 to 3, 6 to 9, 14 to 9, 16 to 6, 8 to 6),
        listOf(10 to 6, 12 to 6)
    )

    val ARENA = Layout(
        "arena",
        listOf(
            "..................",
            "..................",
            "....R........R....",
            "..................",
            "..................",
            "..................",
            "..................",
            "....R........R....",
            "..................",
            ".................."
        ),
        listOf(14 to 6),
        listOf(10 to 6)
    )

    /** The three layouts that get shuffled into the middle of the route. */
    val SHUFFLEABLE = listOf(PILLARS, DIVIDER, CORNERS)
    val ALL = listOf(OPEN, PILLARS, DIVIDER, CORNERS, ARENA)
}

class EnemySpawn(val type: EnemyType, val x: Float, val y: Float)

/**
 * One room of the run. Tile characters:
 *  '#' fence (solid)   'R' bush (solid)   '.' floor
 *  'W' west door       'E' east door      (doors are solid while closed)
 */
class Room(
    val index: Int,
    val role: RoomRole,
    val layout: Layout,
    val hasWest: Boolean,
    val hasEast: Boolean
) {
    private val tiles = Array(GameConfig.ROWS) { CharArray(GameConfig.COLS) { '.' } }
    val enemies = ArrayList<Enemy>()
    val pickups = ArrayList<Pickup>()
    private val spawns = ArrayList<EnemySpawn>()

    var visited = false
    var cleared = false
    var eastLocked = role == RoomRole.KEY
    var entryX = 40f
    var entryY = GameConfig.DOOR_Y

    init {
        val cols = GameConfig.COLS
        val rows = GameConfig.ROWS
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val border = r == 0 || c == 0 || r == rows - 1 || c == cols - 1
                tiles[r][c] = if (border) '#' else {
                    val line = layout.rows.getOrNull(r - 1)
                    if (line != null && c - 1 < line.length && line[c - 1] == 'R') 'R' else '.'
                }
            }
        }
        for (r in GameConfig.DOOR_ROW_A..GameConfig.DOOR_ROW_B) {
            if (hasWest) tiles[r][0] = 'W'
            if (hasEast) tiles[r][cols - 1] = 'E'
        }
    }

    fun tileAt(tx: Int, ty: Int): Char =
        if (tx < 0 || ty < 0 || tx >= GameConfig.COLS || ty >= GameConfig.ROWS) '#' else tiles[ty][tx]

    /** Kill rooms lock BOTH doors until cleared. */
    val lockdown: Boolean get() = role.killAll && !cleared
    val westOpen: Boolean get() = !lockdown
    val eastOpen: Boolean get() = cleared && !eastLocked

    /** Does this tile stop a walking (or, if [flying], a flying) body? */
    fun blocks(tx: Int, ty: Int, flying: Boolean): Boolean = when (tileAt(tx, ty)) {
        '#' -> true
        'R' -> !flying
        'W' -> flying || !westOpen
        'E' -> flying || !eastOpen
        else -> false
    }

    fun blocksProjectile(tx: Int, ty: Int): Boolean = when (tileAt(tx, ty)) {
        '#', 'R' -> true
        'W' -> !westOpen
        'E' -> !eastOpen
        else -> false
    }

    fun addEnemySpawn(type: EnemyType, x: Float, y: Float) {
        spawns.add(EnemySpawn(type, x, y))
    }

    /** Creates fresh enemies from the stored spawn list (first visit). */
    fun spawnEnemies() {
        enemies.clear()
        for (s in spawns) enemies.add(Enemy(s.type, s.x, s.y))
    }

    /** After Waffle loses a life: living enemies go back to their starting spots at full health. */
    fun resetEnemies() {
        for (e in enemies) if (e.alive) e.resetToSpawn()
    }

    fun enemiesAlive(): Int {
        var n = 0
        for (e in enemies) if (e.alive) n++
        return n
    }
}

object RoomFactory {
    private fun cx(tile: Int): Float = (tile + 0.5f) * GameConfig.TILE

    /**
     * START -> KILL -> KEY -> REWARD -> BOSS.
     * The role order is fixed so the key always comes before the locked gate;
     * the middle three layouts are shuffled by the run's seed.
     */
    fun buildRoute(rng: Random): List<Room> {
        val mid = Layouts.SHUFFLEABLE.shuffled(rng)
        val specs = listOf(
            RoomRole.START to Layouts.OPEN,
            RoomRole.KILL to mid[0],
            RoomRole.KEY to mid[1],
            RoomRole.REWARD to mid[2],
            RoomRole.BOSS to Layouts.ARENA
        )
        val total = specs.size
        return specs.mapIndexed { i, spec -> build(i, spec.first, spec.second, total, rng) }
    }

    private fun build(index: Int, role: RoomRole, layout: Layout, total: Int, rng: Random): Room {
        val room = Room(index, role, layout, hasWest = index > 0, hasEast = index < total - 1)
        val slots = layout.enemySlots.shuffled(rng)
        val items = layout.itemSlots
        when (role) {
            RoomRole.KILL -> {
                val dogs = 2 + rng.nextInt(2) // 2 or 3 dogs
                for (i in 0 until dogs) room.addEnemySpawn(EnemyType.DOG, cx(slots[i].first), cx(slots[i].second))
            }
            RoomRole.KEY -> {
                for (i in 0 until 2) room.addEnemySpawn(EnemyType.CROW, cx(slots[i].first), cx(slots[i].second))
                room.pickups.add(Pickup(PickupType.KEY, cx(items[0].first), cx(items[0].second)))
            }
            RoomRole.REWARD -> {
                room.pickups.add(Pickup(PickupType.REWARD, cx(items[0].first), cx(items[0].second)))
                room.pickups.add(Pickup(PickupType.HEART, cx(items[1].first), cx(items[1].second)))
            }
            RoomRole.BOSS -> {
                room.addEnemySpawn(EnemyType.BOSS, cx(slots[0].first), cx(slots[0].second))
            }
            else -> {}
        }
        return room
    }
}
