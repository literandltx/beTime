package com.literandltx.betime.ui.screens.preset.labels

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.literandltx.betime.R
import com.literandltx.betime.ui.shared.CloseIconButton
import com.literandltx.betime.ui.shared.ConfirmIconButton
import com.literandltx.betime.ui.shared.EditableTextField
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditorLabelCard(
    initialName: String,
    initialColor: Color,
    onConfirm: (name: String, color: Color) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = stringResource(R.string.newLabelPlaceholder),
    shape: Shape = MaterialTheme.shapes.large
) {
    var textFieldValue by remember(initialName) {
        mutableStateOf(
            TextFieldValue(
                text = initialName,
                selection = TextRange(initialName.length)
            )
        )
    }
    var selectedColor by remember(initialColor) {
        mutableStateOf(initialColor)
    }

    val focusRequester = remember { FocusRequester() }
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val imeBottom = WindowInsets.ime.getBottom(LocalDensity.current)

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        delay(100.milliseconds)
        bringIntoViewRequester.bringIntoView()
    }

    LaunchedEffect(imeBottom) {
        if (imeBottom > 0) {
            delay(50.milliseconds)
            bringIntoViewRequester.bringIntoView()
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .bringIntoViewRequester(bringIntoViewRequester),
        shape = shape,
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(R.dimen.card_elevation)
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_large))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocalOffer,
                    contentDescription = null,
                    tint = selectedColor,
                    modifier = Modifier.size(dimensionResource(R.dimen.medium_icon_size)),
                )
                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_standard)))
                EditableTextField(
                    value = textFieldValue,
                    onValueChange = { textFieldValue = it },
                    placeholder = placeholder,
                    cursorColor = selectedColor,
                    onConfirm = { onConfirm(textFieldValue.text.trim(), selectedColor) },
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester)
                )
                ConfirmIconButton(
                    onClick = { onConfirm(textFieldValue.text.trim(), selectedColor) },
                    isEnabled = textFieldValue.text.isNotBlank(),
                    contentDescription = stringResource(R.string.confirmAction)
                )
                CloseIconButton(
                    onClick = onCancel,
                    contentDescription = stringResource(R.string.cancelAction)
                )
            }

            LabelSelector(
                selectedColor = selectedColor,
                onColorSelected = { selectedColor = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 32.dp)
            )
        }
    }
}
