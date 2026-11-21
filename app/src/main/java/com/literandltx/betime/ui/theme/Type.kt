package com.literandltx.betime.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.literandltx.betime.R

val Montserrat = FontFamily(
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_bold, FontWeight.Bold)
)

val RobotoMono = FontFamily(
    Font(R.font.roboto_mono_regular, FontWeight.Normal)
)

private val BaseSans = TextStyle(fontFamily = Montserrat, fontWeight = FontWeight.Normal)
private val BaseMono = TextStyle(fontFamily = RobotoMono, fontWeight = FontWeight.Normal)

val Typography = Typography(
    // Monospace
    displayLarge = BaseMono.copy(fontSize = 36.sp),
    headlineMedium = BaseMono.copy(fontSize = 20.sp),
    headlineSmall = BaseMono.copy(fontSize = 14.sp),

    // Headers
    displayMedium = BaseSans.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
    displaySmall = BaseSans.copy(fontWeight = FontWeight.Medium, fontSize = 16.sp),

    // Body & Labels
    bodyLarge = BaseSans.copy(fontSize = 14.sp),
    bodyMedium = BaseSans.copy(fontSize = 12.sp),
    bodySmall = BaseSans.copy(fontWeight = FontWeight.Light, fontSize = 10.sp),

    labelMedium = BaseSans.copy(fontSize = 14.sp),
    labelSmall = BaseSans.copy(fontWeight = FontWeight.Light, fontSize = 12.sp)
)
