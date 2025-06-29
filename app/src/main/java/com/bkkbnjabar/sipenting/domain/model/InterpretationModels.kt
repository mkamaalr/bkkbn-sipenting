package com.bkkbnjabar.sipenting.domain.model

import androidx.annotation.ColorRes
import com.bkkbnjabar.sipenting.R

// A default color can be set if needed
val defaultTextColor = R.color.black

// A data class to hold the title and value for a single health interpretation item.
data class Interpretation(
    val title: String,
    val value: String,
    val isRisky: Boolean? // true = Beresiko, false = Tidak Beresiko, null = N/A
)

// Data class to hold the text, color, and an optional recommendation message for each interpretation
data class InterpretationResult(
    val text: String,
    @ColorRes val color: Int = defaultTextColor,
    val recommendation: String? = null
)



