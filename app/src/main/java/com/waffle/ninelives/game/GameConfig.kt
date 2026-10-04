package com.waffle.ninelives.game

/**
 * Every tunable number lives here. Change a value, rebuild, and play.
 * Distances are in "logical units" (one tile = 16 units). The whole room is 320 x 192 units
 * and is scaled to fit any phone screen.
 */
object GameConfig {
    // ---- World size ----
    const val TILE = 16f
    const val COLS = 20
    const val ROWS = 12
    const val VIEW_W = 320f   // COLS * TILE
    const val VIEW_H = 192f   // ROWS * TILE
    const val DOOR_ROW_A = 5  // doors are two tiles tall (rows 5 and 6) ...
    const val DOOR_ROW_B = 6
    const val DOOR_Y = 96f    // ... so the middle of a door is y = 96

    // ---- Game loop ----
    const val STEP = 1f / 60f          // fixed simulation step
    const val MAX_FRAME_TIME = 0.1f    // clamp huge gaps (pause/resume) to avoid teleporting
    const val MAX_PROJECTILES = 32
    const val MAX_PARTICLES = 160
    const val MAX_PICKUPS = 8

    // ---- Waffle ----
    const val PLAYER_RADIUS = 6f
    const val PLAYER_SPEED = 78f
    const val KNOCKBACK_DECAY = 9f
    const val INVULN_TIME = 1.0f
    const val HIT_FLASH_TIME = 0.2f
    const val DAMAGE_KNOCKBACK = 150f

    // Claw Swipe (base attack)
    const val SWIPE_RANGE = 20f
    const val SWIPE_CONE_DOT = 0.25f
    const val SWIPE_DURATION = 0.16f
    const val SWIPE_COOLDOWN = 0.32f
    const val SWIPE_DAMAGE = 1
    const val SWIPE_KNOCKBACK = 130f

    // Spirit Pounce (unlocked at 8 lives)
    const val SPIRIT_TIME = 0.24f
    const val SPIRIT_SPEED = 240f
    const val SPIRIT_COOLDOWN = 2.6f
    const val SPIRIT_DAMAGE = 2
    const val SPIRIT_KNOCKBACK = 170f
    const val SPIRIT_GRACE = 0.3f

    // ---- Lives ----
    const val LIFE_LOSS_TIME = 1.5f
    const val TRANSFORM_TIME = 2.6f
    const val RESPAWN_INVULN = 1.5f
    const val ROOM_ENTER_INVULN = 0.5f

    // ---- Pickups ----
    const val PICKUP_RADIUS = 8f
    const val HEART_HEAL = 2
    const val REWARD_MAX_HEALTH_BONUS = 2
    const val HEART_DROP_CHANCE = 0.2f

    // ---- Enemies ----
    const val CROW_AIM_TIME = 0.5f
    const val FEATHER_SPEED = 70f
    const val FEATHER_LIFE = 4f
    const val FEATHER_RADIUS = 3f

    // ---- Touch controls ----
    const val JOY_DEAD_ZONE = 0.18f

    // ---- Room doors: how close to the edge counts as "walked through" ----
    const val DOOR_TRIGGER_MARGIN = 9f
}
