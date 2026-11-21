package com.literandltx.betime.ui.screens.preset.timers

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
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
    var textFieldValue by remember(initialValue) {
        val initialValueString = if (initialValue == 0) "" else initialValue.toString()
        mutableStateOf(
            TextFieldValue(
                text = initialValueString,
                selection = TextRange(initialValueString.length)
            )
        )
    }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val submitForm = {
        val value = textFieldValue.text.toIntOrNull()
        if (textFieldValue.text.isNotBlank() && value != null) {
            onConfirm(value)
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
                value = textFieldValue,
                onValueChange = { textFieldValue = it },
                placeholder = placeholder,
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
                isEnabled = textFieldValue.text.isNotBlank() && textFieldValue.text.toIntOrNull() != null,
                contentDescription = stringResource(R.string.confirmAction)
            )
            CloseIconButton(
                onClick = onCancel,
                contentDescription = stringResource(R.string.cancelAction)
            )
        }
    }
}
