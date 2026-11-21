package com.literandltx.betime.ui.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.literandltx.betime.data.preferences.AppThemeMode
import com.literandltx.betime.data.preferences.AppThemeModeProvider
import com.literandltx.betime.ui.theme.TimerTheme

@Composable
fun CustomSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier
) {
    val containerColor = MaterialTheme.colorScheme.inverseSurface
    val contentColor = MaterialTheme.colorScheme.surface

    Snackbar(
        modifier = modifier.padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        containerColor = containerColor,
        contentColor = contentColor,
        action = {
            snackbarData.visuals.actionLabel?.let { actionLabel ->
                TextButton(
                    onClick = { snackbarData.performAction() }
                ) {
                    Text(
                        text = actionLabel,
                        color = MaterialTheme.colorScheme.inversePrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = snackbarData.visuals.message,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.inverseOnSurface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CustomSnackbarPreview(
    @PreviewParameter(AppThemeModeProvider::class) themeMode: AppThemeMode
) {
    TimerTheme(themeMode = themeMode) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column {
                CustomSnackbar(
                    snackbarData = createMockSnackbarData(
                        message = "Record deleted",
                        actionLabel = "Undo"
                    )
                )
                CustomSnackbar(
                    snackbarData = createMockSnackbarData(
                        message = "CSV exported successfully"
                    )
                )
            }
        }
    }
}

private fun createMockSnackbarData(
    message: String,
    actionLabel: String? = null
): SnackbarData = object : SnackbarData {
    override val visuals: SnackbarVisuals = object : SnackbarVisuals {
        override val message: String = message
        override val actionLabel: String? = actionLabel
        override val withDismissAction: Boolean = false
        override val duration: SnackbarDuration = SnackbarDuration.Short
    }

    override fun performAction() {}
    override fun dismiss() {}
}
