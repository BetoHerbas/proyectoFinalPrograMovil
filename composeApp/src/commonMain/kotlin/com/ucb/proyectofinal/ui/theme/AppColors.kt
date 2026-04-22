package com.ucb.proyectofinal.ui.theme

import androidx.compose.ui.graphics.Color

object AppColors {
    // ── Fondos ───────────────────────────────────────────────────────────────
    val Background        = Color(0xFF0D1117)
    val Surface           = Color(0xFF161B22)
    val SurfaceVariant    = Color(0xFF1C2128)
    val SurfaceHighlight  = Color(0xFF21262D)

    // ── Acento principal (Cian/Teal) ─────────────────────────────────────────
    val Primary           = Color(0xFF00E5C3)
    val PrimaryDim        = Color(0x2600E5C3)
    val PrimaryContainer  = Color(0xFF003D35)

    // ── Texto ─────────────────────────────────────────────────────────────────
    val TextPrimary       = Color(0xFFE6EDF3)
    val TextSecondary     = Color(0xFF8B949E)
    val TextDisabled      = Color(0xFF484F58)

    // ── Estado / Feedback ─────────────────────────────────────────────────────
    val Error             = Color(0xFFFF5252)
    val ErrorContainer    = Color(0xFF3D1414)
    val Success           = Color(0xFF00E5C3)

    // ── Colores de etiquetas (tags) ─────────────────────────────────────────
    val TagPurple         = Color(0xFF9D50FF)
    val TagPurpleBg       = Color(0x229D50FF)
    val TagBlue           = Color(0xFF00D1FF)
    val TagBlueBg         = Color(0x2200D1FF)
    val TagGreen          = Color(0xFF00FF94)
    val TagGreenBg        = Color(0x2200FF94)
    val TagRed            = Color(0xFFFF6B6B)
    val TagRedBg          = Color(0x22FF6B6B)
    val TagOrange         = Color(0xFFFF8C42)
    val TagOrangeBg       = Color(0x22FF8C42)

    // ── Gradientes comunes ────────────────────────────────────────────────────
    val GradientBg = listOf(Color(0xFF0D1117), Color(0xFF161B22))
    val GradientAccent = listOf(Color(0xFF00E5C3), Color(0xFF00B8A0))
}
