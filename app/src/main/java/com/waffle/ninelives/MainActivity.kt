package com.waffle.ninelives

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import com.waffle.ninelives.ui.GameView

/**
 * The app's single screen. It just hosts the GameView, keeps the screen on,
 * hides the system bars, and forwards the app's pause/resume to the game.
 * Keyboard keys (emulator / dev convenience): WASD or arrows = move, Space or J = claw swipe,
 * K = Spirit Pounce, P or Esc = pause, Enter = tap/start.
 */
class MainActivity : Activity() {
    private lateinit var gameView: GameView
    private var up = false
    private var down = false
    private var left = false
    private var right = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (Build.VERSION.SDK_INT >= 28) {
            val lp = window.attributes
            lp.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = lp
        }
        gameView = GameView(this)
        setContentView(gameView)
    }

    override fun onResume() {
        super.onResume()
        gameView.onHostResume()
    }

    override fun onPause() {
        up = false; down = false; left = false; right = false
        gameView.onHostPause()
        super.onPause()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemBars()
    }

    @Suppress("DEPRECATION")
    private fun hideSystemBars() {
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            )
    }

    // ---- development keyboard controls ----

    private fun applyKeyMove() {
        val input = gameView.world.input
        input.moveX = (if (right) 1f else 0f) - (if (left) 1f else 0f)
        input.moveY = (if (down) 1f else 0f) - (if (up) 1f else 0f)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val input = gameView.world.input
        val fresh = event.repeatCount == 0
        when (keyCode) {
            KeyEvent.KEYCODE_W, KeyEvent.KEYCODE_DPAD_UP -> up = true
            KeyEvent.KEYCODE_S, KeyEvent.KEYCODE_DPAD_DOWN -> down = true
            KeyEvent.KEYCODE_A, KeyEvent.KEYCODE_DPAD_LEFT -> left = true
            KeyEvent.KEYCODE_D, KeyEvent.KEYCODE_DPAD_RIGHT -> right = true
            KeyEvent.KEYCODE_SPACE, KeyEvent.KEYCODE_J -> if (fresh) input.pressAttack()
            KeyEvent.KEYCODE_K -> if (fresh) input.pressAbility()
            KeyEvent.KEYCODE_P, KeyEvent.KEYCODE_ESCAPE -> if (fresh) input.requestPauseToggle()
            KeyEvent.KEYCODE_ENTER -> if (fresh) input.requestTap()
            else -> return super.onKeyDown(keyCode, event)
        }
        applyKeyMove()
        return true
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_W, KeyEvent.KEYCODE_DPAD_UP -> up = false
            KeyEvent.KEYCODE_S, KeyEvent.KEYCODE_DPAD_DOWN -> down = false
            KeyEvent.KEYCODE_A, KeyEvent.KEYCODE_DPAD_LEFT -> left = false
            KeyEvent.KEYCODE_D, KeyEvent.KEYCODE_DPAD_RIGHT -> right = false
            KeyEvent.KEYCODE_SPACE, KeyEvent.KEYCODE_J, KeyEvent.KEYCODE_K,
            KeyEvent.KEYCODE_P, KeyEvent.KEYCODE_ESCAPE, KeyEvent.KEYCODE_ENTER -> return true
            else -> return super.onKeyUp(keyCode, event)
        }
        applyKeyMove()
        return true
    }
}
