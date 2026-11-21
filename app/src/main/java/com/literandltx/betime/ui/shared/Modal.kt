package com.literandltx.betime.ui.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun GenericModal(
    isVisible: Boolean = true,
    onDismissRequest: () -> Unit,
    title: String,
    containerShape: Shape = MaterialTheme.shapes.medium,
    containerElevation: Dp = 8.dp,
    modalInnerPadding: PaddingValues = PaddingValues(20.dp),
    spaceBetweenHeaderAndContent: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    if (isVisible) {
        Dialog(onDismissRequest = onDismissRequest) {
            Surface(
                shape = containerShape,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = containerElevation
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(modalInnerPadding)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        CloseIconButton(
                            onClick = onDismissRequest
                        )
                    }

                    if (spaceBetweenHeaderAndContent > 0.dp) {
                        Spacer(modifier = Modifier.height(spaceBetweenHeaderAndContent))
                    }

                    content()
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GenericModalPreview() {
    MaterialTheme {
        GenericModal(
            isVisible = true,
            onDismissRequest = {},
            title = "Settings",
            content = {
                Text(
                    text = "This is some sample content inside the generic modal. You can place any composable here.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        )
    }
}
