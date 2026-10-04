package com.waffle.ninelives.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.waffle.ninelives.game.GameConfig
import com.waffle.ninelives.game.GameWorld

/**
 * The screen the game draws on. It owns ONE game thread that runs a fixed-step loop.
 * The thread starts only when both the surface exists AND the activity is resumed,
 * and it is always stopped before it can be started again, so loops can never double up.
 */
class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    val world = GameWorld()
    private val controls = TouchControls(context.resources.displayMetrics.density)
    private val renderer = Renderer()

    private var loop: GameThread? = null
    private var surfaceReady = false
    private var hostResumed = false

    init {
        holder.addCallback(this)
        isFocusable = true
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        surfaceReady = true
        startLoopIfReady()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        controls.layout(width.toFloat(), height.toFloat())
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        surfaceReady = false
        stopLoop()
    }

    fun onHostResume() {
        hostResumed = true
        startLoopIfReady()
    }

    fun onHostPause() {
        hostResumed = false
        stopLoop()
        controls.cancelAll(world)
        world.pauseForLifecycle()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        controls.onTouch(event, world)
        return true
    }

    private fun startLoopIfReady() {
        if (loop == null && surfaceReady && hostResumed) {
            val t = GameThread()
            loop = t
            t.start()
        }
    }

    private fun stopLoop() {
        val t = loop ?: return
        t.running = false
        try {
            t.join(1000)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
        loop = null
    }

    private inner class GameThread : Thread("WaffleGameLoop") {
        @Volatile
        var running = true

        override fun run() {
            var last = System.nanoTime()
            var acc = 0f
            while (running) {
                val now = System.nanoTime()
                var frame = (now - last) / 1_000_000_000f
                last = now
                if (frame > GameConfig.MAX_FRAME_TIME) frame = GameConfig.MAX_FRAME_TIME // no huge jumps
                acc += frame
                var steps = 0
                while (acc >= GameConfig.STEP && steps < 5) {
                    world.update(GameConfig.STEP)
                    acc -= GameConfig.STEP
                    steps++
                }
                if (steps == 5) acc = 0f
                controls.tick(System.currentTimeMillis())
                drawFrame()
                val spentMs = (System.nanoTime() - now) / 1_000_000L
                val napMs = 16L - spentMs
                if (napMs > 0L) {
                    try {
                        Thread.sleep(napMs)
                    } catch (e: InterruptedException) {
                        return
                    }
                }
            }
        }
    }

    private fun drawFrame() {
        val canvas: Canvas? = try {
            holder.lockCanvas()
        } catch (e: Exception) {
            null
        }
        if (canvas == null) return
        try {
            renderer.draw(canvas, world, controls)
        } finally {
            try {
                holder.unlockCanvasAndPost(canvas)
            } catch (e: Exception) {
                // The surface went away mid-frame; the next surfaceCreated restarts drawing.
            }
        }
    }
}
