package com.waffle.ninelives.progression

/**
 * Nine-life progression data.
 *
 * This file is pure Kotlin (no Android classes) so it can be unit-tested on a normal computer.
 *
 * How it works:
 *  - "livesRemaining" counts DOWN from 9. Reaching 0 is true death.
 *  - Every life count has a LifeStage that says what Waffle gets when he ARRIVES at that count.
 *  - MVP: only the stage for 8 lives has content (Spirit Pounce). The other stages exist as
 *    empty placeholders so fixed / two-choice / three-choice upgrades can be added later
 *    just by filling in their `options` lists.
 */
enum class ChoiceKind { NONE, FIXED, CHOOSE_ONE_OF_TWO, CHOOSE_ONE_OF_THREE }

data class LifeUpgrade(val id: String, val name: String, val description: String)

data class LifeStage(val livesRemaining: Int, val kind: ChoiceKind, val options: List<LifeUpgrade>)

object LifeTable {
    val SPIRIT_POUNCE = LifeUpgrade(
        id = "spirit_pounce",
        name = "Spirit Pounce",
        description = "Waffle's tail glows. Pounce through danger! Use the new button."
    )

    private val stageMap: Map<Int, LifeStage> = mapOf(
        9 to LifeStage(9, ChoiceKind.NONE, emptyList()),
        // ---- Predetermined transformations ----
        8 to LifeStage(8, ChoiceKind.FIXED, listOf(SPIRIT_POUNCE)),
        7 to LifeStage(7, ChoiceKind.FIXED, emptyList()), // TODO (post-MVP)
        6 to LifeStage(6, ChoiceKind.FIXED, emptyList()), // TODO (post-MVP)
        // ---- Choose one of two ----
        5 to LifeStage(5, ChoiceKind.CHOOSE_ONE_OF_TWO, emptyList()), // TODO
        4 to LifeStage(4, ChoiceKind.CHOOSE_ONE_OF_TWO, emptyList()), // TODO
        3 to LifeStage(3, ChoiceKind.CHOOSE_ONE_OF_TWO, emptyList()), // TODO
        // ---- Choose one of three ----
        2 to LifeStage(2, ChoiceKind.CHOOSE_ONE_OF_THREE, emptyList()), // TODO
        1 to LifeStage(1, ChoiceKind.CHOOSE_ONE_OF_THREE, emptyList()), // TODO
        // ---- True death ----
        0 to LifeStage(0, ChoiceKind.NONE, emptyList())
    )

    fun stageFor(lives: Int): LifeStage =
        stageMap[lives] ?: LifeStage(lives, ChoiceKind.NONE, emptyList())
}

/** Everything that must survive a death within one run (but is wiped by Restart). */
class RunState {
    companion object {
        const val START_LIVES = 9
        const val START_MAX_HEALTH = 6 // health points; 2 points = 1 heart on screen
    }

    var livesRemaining = START_LIVES
        private set
    var maxHealth = START_MAX_HEALTH
    var hasKey = false

    private val unlocked = HashSet<String>()

    fun has(id: String): Boolean = unlocked.contains(id)

    fun reset() {
        livesRemaining = START_LIVES
        maxHealth = START_MAX_HEALTH
        hasKey = false
        unlocked.clear()
    }

    /** Consumes one life. Never goes below zero. Returns the new count. */
    fun loseLife(): Int {
        if (livesRemaining > 0) livesRemaining -= 1
        return livesRemaining
    }

    /**
     * Grants the predetermined upgrade for arriving at [lives], if there is one.
     * Returns the upgrade only the FIRST time it is granted (so it can never be granted twice).
     */
    fun grantFixedFor(lives: Int): LifeUpgrade? {
        val stage = LifeTable.stageFor(lives)
        if (stage.kind != ChoiceKind.FIXED || stage.options.size != 1) return null
        val upgrade = stage.options[0]
        return if (unlocked.add(upgrade.id)) upgrade else null
    }

    /** Developer helper: jump to a life count and hold every upgrade Waffle "would have" earned. */
    fun setLivesDebug(lives: Int) {
        livesRemaining = lives.coerceIn(0, START_LIVES)
        unlocked.clear()
        for (l in (START_LIVES - 1) downTo livesRemaining) grantFixedFor(l)
    }
}
