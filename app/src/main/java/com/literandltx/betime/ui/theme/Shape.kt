package com.literandltx.betime.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val CardCornerRadius = 12.dp
val DialogCornerRadius = 24.dp

val ChipCornerRadius = 8.dp
val BadgeCornerRadius = 50.dp
val SmallElementCornerRadius = 6.dp

val CardShape = RoundedCornerShape(CardCornerRadius)
val DialogShape = RoundedCornerShape(DialogCornerRadius)
val ChipShape = RoundedCornerShape(ChipCornerRadius)
val BadgeShape = RoundedCornerShape(BadgeCornerRadius)
val SmallElementShape = RoundedCornerShape(SmallElementCornerRadius)

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = ChipShape,
    medium = CardShape,
    large = RoundedCornerShape(16.dp),
    extraLarge = DialogShape
)
