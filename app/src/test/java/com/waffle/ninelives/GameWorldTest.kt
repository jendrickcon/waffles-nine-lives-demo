package com.waffle.ninelives

import com.waffle.ninelives.game.DebugCommand
import com.waffle.ninelives.game.GameConfig
import com.waffle.ninelives.game.GameState
import com.waffle.ninelives.game.GameWorld
import com.waffle.ninelives.game.PickupType
import com.waffle.ninelives.game.RoomRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.hypot

class GameWorldTest {
    private val dt = GameConfig.STEP

    private fun newWorld(): GameWorld {
        val w = GameWorld(1234L)
        w.input.requestTap()
        w.update(dt)
        assertEquals(GameState.PLAYING, w.state)
        return w
    }

    private fun run(w: GameWorld, seconds: Float) {
        repeat((seconds / dt).toInt()) { w.update(dt) }
    }

    private fun killWaffle(w: GameWorld) {
        repeat(w.run.maxHealth) { w.input.debugQueue.add(DebugCommand.DAMAGE) }
        w.update(dt)
    }

    @Test
    fun startsAtIntroThenPlays() {
        val w = GameWorld(1L)
        assertEquals(GameState.INTRO, w.state)
        assertEquals(9, w.run.livesRemaining)
        w.input.requestTap()
        w.update(dt)
        assertEquals(GameState.PLAYING, w.state)
    }

    @Test
    fun wallsStopWaffle() {
        val w = newWorld()
        w.input.moveX = -1f
        run(w, 3f)
        assertTrue(w.player.x >= GameConfig.TILE + w.player.r - 0.01f)
        w.input.moveX = 0f
        w.input.moveY = -1f
        run(w, 3f)
        assertTrue(w.player.y >= GameConfig.TILE + w.player.r - 0.01f)
    }

    @Test
    fun diagonalIsNotFaster() {
        val w = newWorld()
        w.player.x = 150f; w.player.y = 60f
        w.input.moveX = 1f; w.input.moveY = 1f
        repeat(30) { w.update(dt) } // exactly half a second
        val d = hypot(w.player.x - 150f, w.player.y - 60f)
        assertEquals(GameConfig.PLAYER_SPEED * 0.5f, d, 0.8f)
    }

    @Test
    fun zeroHealthMovesNineToEightAndTransforms() {
        val w = newWorld()
        assertFalse(w.abilityUnlocked)
        killWaffle(w)
        assertEquals(GameState.LIFE_LOSS, w.state)
        assertEquals(9, w.run.livesRemaining) // counter changes at the END of the life-loss sequence
        run(w, GameConfig.LIFE_LOSS_TIME + 0.1f)
        assertEquals(8, w.run.livesRemaining)
        assertEquals(GameState.TRANSFORM, w.state)
        assertTrue(w.abilityUnlocked)
        run(w, GameConfig.TRANSFORM_TIME + 0.1f)
        assertEquals(GameState.PLAYING, w.state)
        assertEquals(w.run.maxHealth, w.player.health)
        assertEquals(8, w.run.livesRemaining) // transformation is granted once, lives not lost again
    }

    @Test
    fun abilityLockedBeforeAndWorksAfterTransformation() {
        val w = newWorld()
        w.input.pressAbility()
        w.update(dt)
        assertFalse(w.player.isPouncing)

        w.input.debugQueue.add(DebugCommand.LIVES_8)
        w.update(dt)
        assertTrue(w.abilityUnlocked)
        w.input.pressAbility()
        w.update(dt)
        assertTrue(w.player.isPouncing)
        assertTrue(w.player.pounceCooldown > 0f)
        val startX = w.player.x
        run(w, GameConfig.SPIRIT_TIME + 0.05f)
        assertFalse(w.player.isPouncing)
        assertTrue(w.player.x > startX + 20f)
    }

    @Test
    fun attackIsEdgeTriggered() {
        val w = newWorld()
        w.input.pressAttack()
        w.update(dt)
        val firstId = w.player.attackId
        assertEquals(1, firstId)
        run(w, 0.5f) // no new press -> no new attack
        assertEquals(1, w.player.attackId)
    }

    @Test
    fun trueDeathAndRestart() {
        val w = newWorld()
        w.input.debugQueue.add(DebugCommand.LIVES_1)
        w.update(dt)
        killWaffle(w)
        run(w, GameConfig.LIFE_LOSS_TIME + 0.1f)
        assertEquals(GameState.TRUE_DEATH, w.state)
        assertEquals(0, w.run.livesRemaining)
        w.input.requestTap()
        w.update(dt)
        assertEquals(GameState.PLAYING, w.state)
        assertEquals(9, w.run.livesRemaining)
        assertFalse(w.abilityUnlocked)
    }

    @Test
    fun repeatedDeathNeverGoesNegative() {
        val w = newWorld()
        repeat(15) {
            if (w.state == GameState.PLAYING) killWaffle(w)
            run(w, GameConfig.LIFE_LOSS_TIME + GameConfig.TRANSFORM_TIME + 0.2f)
        }
        assertTrue(w.run.livesRemaining >= 0)
        assertEquals(GameState.TRUE_DEATH, w.state)
    }

    @Test
    fun pauseFreezesTheGame() {
        val w = newWorld()
        w.input.requestPauseToggle()
        w.update(dt)
        assertTrue(w.paused)
        val x = w.player.x
        w.input.moveX = 1f
        run(w, 1f)
        assertEquals(x, w.player.x, 0.0001f)
        w.input.requestResume()
        w.update(dt)
        assertFalse(w.paused)
    }

    @Test
    fun clearingKillRoomOpensDoorAndWalkingEastEntersNextRoom() {
        val w = newWorld()
        w.debugJumpToRoom(1)
        assertEquals(1, w.roomIndex)
        val room = w.currentRoom
        assertEquals(RoomRole.KILL, room.role)
        assertTrue(room.lockdown)
        room.enemies.forEach { it.alive = false }
        w.update(dt)
        assertTrue(room.cleared)
        w.player.x = GameConfig.VIEW_W - 40f
        w.player.y = GameConfig.DOOR_Y
        w.input.moveX = 1f
        var frames = 0
        while (w.roomIndex == 1 && frames < 300) { w.update(dt); frames++ }
        assertEquals(2, w.roomIndex)
        assertTrue(w.player.x < 60f) // arrived safely near the west door
    }

    @Test
    fun keyUnlocksGateAndRewardRaisesMaxHealth() {
        val w = newWorld()
        w.debugJumpToRoom(2)
        val room = w.currentRoom
        room.enemies.forEach { it.alive = false }
        assertFalse(room.cleared)
        val key = room.pickups.first { it.type == PickupType.KEY }
        w.player.x = key.x; w.player.y = key.y
        w.update(dt)
        assertTrue(w.run.hasKey)
        assertTrue(room.cleared)
        assertTrue(room.eastLocked)
        w.player.x = GameConfig.VIEW_W - 30f
        w.player.y = GameConfig.DOOR_Y
        w.update(dt)
        assertFalse(room.eastLocked)
        assertFalse(w.run.hasKey)

        w.debugJumpToRoom(3)
        val reward = w.currentRoom.pickups.first { it.type == PickupType.REWARD }
        w.player.x = reward.x; w.player.y = reward.y
        w.update(dt)
        assertEquals(8, w.run.maxHealth)
    }

    @Test
    fun deathResetsEnemiesAndKeepsRunProgress() {
        val w = newWorld()
        w.debugJumpToRoom(1)
        val room = w.currentRoom
        val e = room.enemies[0]
        e.hp = 1
        e.x += 5f
        killWaffle(w)
        var frames = 0
        while (w.state != GameState.PLAYING && frames < 1000) { w.update(dt); frames++ }
        assertEquals(GameState.PLAYING, w.state)
        assertEquals(e.spawnX, e.x, 0.001f)
        assertEquals(e.type.maxHp, e.hp)
        assertEquals(1, w.roomIndex)
    }

    @Test
    fun projectileAndParticleCountsStayBounded() {
        val w = newWorld()
        w.debugJumpToRoom(4) // boss room
        w.player.invuln = 1000f
        run(w, 20f)
        assertTrue(w.projectiles.size == GameConfig.MAX_PROJECTILES)
        assertTrue(w.particles.capacity == GameConfig.MAX_PARTICLES)
    }

    @Test
    fun bossDefeatSpawnsExitAndExitWins() {
        val w = newWorld()
        w.debugJumpToRoom(4)
        val room = w.currentRoom
        room.enemies.forEach { it.alive = false }
        w.update(dt)
        val exit = room.pickups.first { it.type == PickupType.EXIT }
        w.player.x = exit.x; w.player.y = exit.y
        w.update(dt)
        assertEquals(GameState.VICTORY, w.state)
    }
}
