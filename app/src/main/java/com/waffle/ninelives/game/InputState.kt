package com.waffle.ninelives.game

import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean

enum class DebugCommand { DAMAGE, LIVES_9, LIVES_8, LIVES_5, LIVES_2, LIVES_1, RESET_RUN, TOGGLE_BOXES }

/**
 * The bridge between the touch screen (UI thread) and the game loop (game thread).
 * Buttons are "edge triggered": a press is remembered until the game consumes it exactly once.
 */
class InputState {
    @Volatile var moveX = 0f
    @Volatile var moveY = 0f

    private val attack = AtomicBoolean(false)
    private val ability = AtomicBoolean(false)
    private val pauseToggle = AtomicBoolean(false)
    private val resume = AtomicBoolean(false)
    private val restart = AtomicBoolean(false)
    private val tap = AtomicBoolean(false)

    val debugQueue = ConcurrentLinkedQueue<DebugCommand>()

    fun pressAttack() = attack.set(true)
    fun pressAbility() = ability.set(true)
    fun requestPauseToggle() = pauseToggle.set(true)
    fun requestResume() = resume.set(true)
    fun requestRestart() = restart.set(true)
    fun requestTap() = tap.set(true)

    fun consumeAttack(): Boolean = attack.getAndSet(false)
    fun consumeAbility(): Boolean = ability.getAndSet(false)
    fun consumePauseToggle(): Boolean = pauseToggle.getAndSet(false)
    fun consumeResume(): Boolean = resume.getAndSet(false)
    fun consumeRestart(): Boolean = restart.getAndSet(false)
    fun consumeTap(): Boolean = tap.getAndSet(false)

    /** Forget any queued button presses (used while the game is not in normal play). */
    fun clearEdges() {
        attack.set(false)
        ability.set(false)
    }

    /** Let go of the joystick (touch released, cancelled, or app paused). */
    fun releaseMove() {
        moveX = 0f
        moveY = 0f
    }
}
