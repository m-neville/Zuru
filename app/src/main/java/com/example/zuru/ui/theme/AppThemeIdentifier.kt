package com.example.zuru.ui.theme

import androidx.compose.ui.graphics.Color

enum class AppThemeIdentifier(val id: String, val color: Color) {
    DEFAULT("default", Color(0xFF00897B)),        // Teal
    OCEAN_BLUE("ocean_blue", Color(0xFF0277BD)),  // Blue
    FOREST_GREEN("forest_green", Color(0xFF388E3C)), // Green
    SUNSET_ORANGE("sunset_orange", Color(0xFFF57C00)); // Orange

    companion object {
        fun fromId(id: String): AppThemeIdentifier {
            return entries.firstOrNull { it.id == id } ?: DEFAULT
        }
    }
}
