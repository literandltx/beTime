package com.literandltx.betime.ui.shared

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun EditableTextField(
    placeholder: String,
    modifier: Modifier = Modifier,
    state: TextFieldState = rememberTextFieldState(),
    inputTransformation: InputTransformation? = null,
    cursorColor: Color = MaterialTheme.colorScheme.primary,
    keyboardType: KeyboardType = KeyboardType.Text,
    onConfirm: () -> Unit = {}
) {
    BasicTextField(
        state = state,
        modifier = modifier,
        inputTransformation = inputTransformation,
        textStyle = MaterialTheme.typography.displaySmall.copy(
            color = MaterialTheme.colorScheme.onSurface
        ),
        cursorBrush = SolidColor(cursorColor),
        lineLimits = TextFieldLineLimits.SingleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        onKeyboardAction = { performDefaultAction ->
            if (state.text.isNotBlank()) {
                onConfirm()
            }
            performDefaultAction()
        },
        decorator = { innerTextField ->
            if (state.text.isEmpty()) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            innerTextField()
        }
    )
}
