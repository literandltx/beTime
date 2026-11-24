package com.literandltx.betime.ui.shared

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DragIndicator
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.literandltx.betime.R

@Composable
fun LabelIcon(
    modifier: Modifier = Modifier,
    color: Color? = null,
    contentDescription: String? = null
) {
    val tintColor = color ?: MaterialTheme.colorScheme.onSurface

    Icon(
        imageVector = Icons.Outlined.LocalOffer,
        contentDescription = contentDescription,
        tint = tintColor,
        modifier = modifier.size(dimensionResource(R.dimen.standard_icon_size))
    )
}
@Composable
fun DeleteIcon(
    modifier: Modifier = Modifier,
    iconScale: Float = 1f,
    alphaAnim: Float = 1f,
    contentDescription: String? = null
) {
    Icon(
        imageVector = Icons.Outlined.Delete,
        contentDescription = contentDescription,
        tint = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = alphaAnim.coerceIn(0f, 1f)),
        modifier = modifier.scale(iconScale)
    )
}

@Composable
fun DropdownMenuIcon(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier.size(20.dp)
    )
}

@Composable
fun HourglassIcon(
    modifier: Modifier = Modifier,
    color: Color? = null,
    contentDescription: String? = null
) {
    val tintColor = color ?: MaterialTheme.colorScheme.onSurface

    Icon(
        imageVector = Icons.Filled.HourglassEmpty,
        contentDescription = contentDescription,
        tint = tintColor,
        modifier = modifier.size(dimensionResource(R.dimen.medium_icon_size))
    )
}

@Composable
fun ReorderHandleIcon(
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    Icon(
        imageVector = Icons.Outlined.DragIndicator,
        contentDescription = contentDescription,
        modifier = modifier.size(20.dp)
    )
}
