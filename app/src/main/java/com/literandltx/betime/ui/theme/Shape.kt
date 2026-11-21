package com.literandltx.betime.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val CardCornerRadius = 12.dp
val DialogCornerRadius = 24.dp

val ChipCornerRadius = 8.dp
val BadgeCornerRadius = 50.dp

val CardShape = RoundedCornerShape(CardCornerRadius)
val DialogShape = RoundedCornerShape(DialogCornerRadius)
val ChipShape = RoundedCornerShape(ChipCornerRadius)
val BadgeShape = RoundedCornerShape(BadgeCornerRadius)

val SmallElementCornerRadius = 6.dp
val SmallElementShape = RoundedCornerShape(SmallElementCornerRadius)

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(50.dp),
    medium = RoundedCornerShape(bottomStart = 16.dp, topEnd = 16.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp)
)
