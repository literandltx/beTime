package com.literandltx.betime.ui.screens.preset.timers

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.literandltx.betime.R
import com.literandltx.betime.ui.shared.CloseIconButton
import com.literandltx.betime.ui.shared.ConfirmIconButton
import com.literandltx.betime.ui.shared.EditableTextField

@Composable
fun EditorTimerCard(
    initialValue: Int,
    onConfirm: (value: Int) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = stringResource(R.string.timerValuePlaceholder),
    shape: Shape = MaterialTheme.shapes.large
) {
    val initialValueString = if (initialValue == 0) "" else initialValue.toString()
    val timerState = rememberTextFieldState(initialValueString)

    LaunchedEffect(initialValue) {
        val updatedValueString = if (initialValue == 0) "" else initialValue.toString()
        if (timerState.text.toString() != updatedValueString) {
            timerState.setTextAndPlaceCursorAtEnd(updatedValueString)
        }
    }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val parsedValue = timerState.text.toString().toIntOrNull()
    val isConfirmEnabled = timerState.text.isNotBlank() && parsedValue != null

    val submitForm = {
        if (isConfirmEnabled) {
            onConfirm(parsedValue)
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(R.dimen.card_elevation)
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(R.dimen.padding_content_horizontal),
                    vertical = dimensionResource(R.dimen.padding_small)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EditableTextField(
                state = timerState,
                placeholder = placeholder,
                inputTransformation = {
                    val text = asCharSequence().toString()
                    if (text.isNotEmpty() && text.toIntOrNull() == null) {
                        revertAllChanges()
                    }
                },
                cursorColor = MaterialTheme.colorScheme.primary,
                keyboardType = KeyboardType.Number,
                onConfirm = submitForm,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
            )
            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_standard)))
            ConfirmIconButton(
                onClick = submitForm,
                isEnabled = isConfirmEnabled,
                contentDescription = stringResource(R.string.confirmAction)
            )
            CloseIconButton(
                onClick = onCancel,
                contentDescription = stringResource(R.string.cancelAction)
            )
        }
    }
}
