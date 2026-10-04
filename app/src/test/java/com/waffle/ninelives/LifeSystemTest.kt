package com.waffle.ninelives

import com.waffle.ninelives.progression.LifeTable
import com.waffle.ninelives.progression.RunState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LifeSystemTest {
    @Test
    fun startsWithNineLivesAndNoUpgrades() {
        val run = RunState()
        assertEquals(9, run.livesRemaining)
        assertFalse(run.has(LifeTable.SPIRIT_POUNCE.id))
    }

    @Test
    fun losingFirstLifeGrantsSpiritPounceExactlyOnce() {
        val run = RunState()
        assertEquals(8, run.loseLife())
        val first = run.grantFixedFor(8)
        assertNotNull(first)
        assertTrue(run.has(LifeTable.SPIRIT_POUNCE.id))
        assertNull(run.grantFixedFor(8)) // never granted twice
    }

    @Test
    fun livesNeverGoNegative() {
        val run = RunState()
        repeat(20) { run.loseLife() }
        assertEquals(0, run.livesRemaining)
    }

    @Test
    fun resetRestoresNineLivesAndClearsUpgrades() {
        val run = RunState()
        run.loseLife()
        run.grantFixedFor(8)
        run.hasKey = true
        run.maxHealth = 10
        run.reset()
        assertEquals(9, run.livesRemaining)
        assertFalse(run.has(LifeTable.SPIRIT_POUNCE.id))
        assertFalse(run.hasKey)
        assertEquals(RunState.START_MAX_HEALTH, run.maxHealth)
    }

    @Test
    fun debugLivesKeepsEarnedUpgrades() {
        val run = RunState()
        run.setLivesDebug(5)
        assertEquals(5, run.livesRemaining)
        assertTrue(run.has(LifeTable.SPIRIT_POUNCE.id))
        run.setLivesDebug(9)
        assertFalse(run.has(LifeTable.SPIRIT_POUNCE.id))
    }
}
