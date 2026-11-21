package com.literandltx.betime.ui.shared

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.literandltx.betime.R
import com.literandltx.betime.data.model.Label

@Composable
fun AppIconButton(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    isEnabled: Boolean = true,
    contentDescription: String? = null,
    tint: Color = MaterialTheme.colorScheme.onSurface
) {
    IconButton(
        onClick = onClick,
        enabled = isEnabled,
        modifier = modifier.size(dimensionResource(R.dimen.standard_button_size))
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(dimensionResource(R.dimen.standard_icon_size))
        )
    }
}

@Composable
fun AppIconButton(
    painter: Painter,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    isEnabled: Boolean = true,
    contentDescription: String? = null,
    tint: Color = MaterialTheme.colorScheme.onSurface
) {
    IconButton(
        onClick = onClick,
        enabled = isEnabled,
        modifier = modifier.size(dimensionResource(R.dimen.standard_button_size))
    ) {
        Icon(
            painter = painter,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(dimensionResource(R.dimen.standard_icon_size))
        )
    }
}

@Composable
fun AddIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    contentDescription: String? = null
) {
    AppIconButton(
        imageVector = Icons.Default.Add,
        contentDescription = contentDescription,
        modifier = modifier,
        onClick = onClick
    )
}

@Composable
fun EditIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    contentDescription: String? = null
) {
    AppIconButton(
        painter = painterResource(R.drawable.ic_edit),
        contentDescription = contentDescription,
        modifier = modifier,
        onClick = onClick
    )
}

@Composable
fun MoreOptionsIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    contentDescription: String? = null
) {
    AppIconButton(
        imageVector = Icons.Default.MoreVert,
        contentDescription = contentDescription,
        modifier = modifier,
        onClick = onClick
    )
}

@Composable
fun ConfirmIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    isEnabled: Boolean = true,
    contentDescription: String? = null
) {
    AppIconButton(
        imageVector = Icons.Default.Check,
        contentDescription = contentDescription,
        isEnabled = isEnabled,
        modifier = modifier,
        onClick = onClick
    )
}

@Composable
fun CloseIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    contentDescription: String? = null
) {
    AppIconButton(
        imageVector = Icons.Default.Close,
        contentDescription = contentDescription,
        modifier = modifier,
        onClick = onClick
    )
}

@Composable
fun PreviousIconButton(
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    contentDescription: String? = null
) {
    AppIconButton(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
        contentDescription = contentDescription,
        isEnabled = isEnabled,
        modifier = modifier,
        onClick = onClick
    )
}

@Composable
fun NextIconButton(
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    contentDescription: String? = null
) {
    AppIconButton(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = contentDescription,
        isEnabled = isEnabled,
        modifier = modifier,
        onClick = onClick
    )
}

@Composable
fun TimerIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    contentDescription: String? = null
) {
    AppIconButton(
        imageVector = Icons.Outlined.Timer,
        contentDescription = contentDescription,
        modifier = modifier,
        onClick = onClick
    )
}

@Composable
fun LabelIconButton(
    modifier: Modifier = Modifier,
    label: Label? = null,
    onClick: () -> Unit = {},
    contentDescription: String? = null
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(dimensionResource(R.dimen.standard_button_size))
    ) {
        LabelIcon(
            color = label?.composeColor,
            contentDescription = contentDescription
        )
    }
}

@Composable
fun LabelIconTextButton(
    label: Label?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    contentDescription: String? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        LabelIcon(
            color = label?.composeColor,
            contentDescription = contentDescription
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label?.name ?: stringResource(R.string.noLabelMessage)
        )
    }
}
