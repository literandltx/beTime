package com.literandltx.betime.ui.screens.logs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.literandltx.betime.R
import com.literandltx.betime.data.preferences.AppThemeMode
import com.literandltx.betime.data.preferences.AppThemeModeProvider
import com.literandltx.betime.ui.shared.AddIconButton
import com.literandltx.betime.ui.shared.MoreOptionsIconButton
import com.literandltx.betime.ui.shared.SectionHeader
import com.literandltx.betime.ui.theme.TimerTheme

@Composable
fun LogsHeader(
    modifier: Modifier = Modifier,
    showAddButton: Boolean = true,
    onStartAddClick: () -> Unit = {},
    onExportCsv: () -> Unit = {},
    onImportCsv: () -> Unit = {}
) {
    Column(modifier = modifier) {
        SectionHeader(title = stringResource(R.string.historyHeaderTitle)) {
            if (showAddButton) {
                AddIconButton(onClick = onStartAddClick)
            }
            LogsMoreOptionsDropdown(
                onExportCsv = onExportCsv,
                onImportCsv = onImportCsv
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_standard)),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Composable
private fun LogsMoreOptionsDropdown(
    onExportCsv: () -> Unit,
    onImportCsv: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        MoreOptionsIconButton(onClick = { expanded = true })

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.exportCSVAction)) },
                onClick = {
                    expanded = false
                    onExportCsv()
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.importCSVAction)) },
                onClick = {
                    expanded = false
                    onImportCsv()
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LogsHeaderPreview(
    @PreviewParameter(AppThemeModeProvider::class) themeMode: AppThemeMode
) {
    TimerTheme(themeMode = themeMode) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                LogsHeader(
                    showAddButton = true,
                    onStartAddClick = {},
                    onExportCsv = {},
                    onImportCsv = {}
                )
            }
        }
    }
}
