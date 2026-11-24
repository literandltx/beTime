package com.literandltx.betime.ui.shared

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DragIndicator
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.literandltx.betime.R

@Composable
fun LabelIcon(
    modifier: Modifier = Modifier,
    tint: Color? = MaterialTheme.colorScheme.onSurface,
    size: Dp = dimensionResource(R.dimen.standard_icon_size),
    contentDescription: String? = null
) {
    Icon(
        imageVector = Icons.Outlined.LocalOffer,
        contentDescription = contentDescription,
        tint = tint ?: MaterialTheme.colorScheme.onSurface,
        modifier = modifier.size(size)
    )
}

@Composable
fun DeleteIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onErrorContainer,
    size: Dp = dimensionResource(R.dimen.standard_icon_size),
    iconScale: Float = 1f,
    alphaAnim: Float = 1f,
    contentDescription: String? = null
) {
    Icon(
        imageVector = Icons.Outlined.Delete,
        contentDescription = contentDescription,
        tint = tint.copy(alpha = alphaAnim.coerceIn(0f, 1f)),
        modifier = modifier
            .size(size)
            .scale(iconScale)
    )
}

@Composable
fun DropdownMenuIcon(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    size: Dp = dimensionResource(R.dimen.standard_icon_size),
    contentDescription: String? = null
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier.size(size)
    )
}

@Composable
fun HourglassIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    size: Dp = dimensionResource(R.dimen.medium_icon_size),
    contentDescription: String? = null
) {
    Icon(
        imageVector = Icons.Filled.HourglassEmpty,
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier.size(size)
    )
}

@Composable
fun ReorderHandleIcon(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    size: Dp = dimensionResource(R.dimen.standard_icon_size),
    contentDescription: String? = null
) {
    Icon(
        imageVector = Icons.Outlined.DragIndicator,
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier.size(size)
    )
}
