package com.waffle.ninelives.game

/** All colors in one place (ARGB). A small, coherent palette keeps the look consistent. */
object Palette {
    private fun c(v: Long): Int = v.toInt()

    val LETTERBOX = c(0xFF0B0B12)
    val FLOOR_A = c(0xFFC9C2B0)
    val FLOOR_B = c(0xFFBEB7A4)
    val FLOOR_SPECK = c(0xFFA69F8C)
    val FENCE = c(0xFF8A5A3B)
    val FENCE_DARK = c(0xFF5E3A24)
    val FENCE_LIGHT = c(0xFFA87550)
    val BUSH = c(0xFF3F8F4A)
    val BUSH_DARK = c(0xFF2E6B38)
    val GATE = c(0xFF6B4423)
    val GATE_DARK = c(0xFF3F2713)
    val LOCK = c(0xFFFFD34D)

    val ORANGE = c(0xFFFF9A2E)
    val ORANGE_DARK = c(0xFFD96F12)
    val CREAM = c(0xFFFFE3B8)
    val PINK = c(0xFFFF9FB2)
    val EYE = c(0xFF2A1A0E)
    val SPIRIT = c(0xFF7FF2FF)
    val SPIRIT_DARK = c(0xFF2BB5D6)

    val DOG = c(0xFF9A6B3F)
    val DOG_DARK = c(0xFF6B4526)
    val DOG_LIGHT = c(0xFFC49A6C)
    val BOSS = c(0xFF7C8591)
    val BOSS_DARK = c(0xFF4F5560)
    val BOSS_LIGHT = c(0xFFA9B1BC)
    val CROW = c(0xFF1F2233)
    val CROW_LIGHT = c(0xFF3A3F5C)
    val BEAK = c(0xFFFFB347)

    val HEART = c(0xFFE8334A)
    val HEART_DARK = c(0xFF5A1A26)
    val KEY = c(0xFFFFD34D)
    val SILVER = c(0xFFD8DEE9)
    val SILVER_DARK = c(0xFF8E97A8)

    val WHITE = c(0xFFFFFFFF)
    val SHADOW = c(0x55000000)
    val RED = c(0xFFFF4040)
    val DAMAGE = c(0xFFFF5555)
    val UI_BG = c(0xCC10101C)
    val TEXT = c(0xFFFFFFFF)
    val TEXT_DIM = c(0xFFB8B8C8)

    /** Returns [color] with its alpha replaced by [a] (0..255). */
    fun alpha(color: Int, a: Int): Int = (a.coerceIn(0, 255) shl 24) or (color and 0x00FFFFFF)
}
