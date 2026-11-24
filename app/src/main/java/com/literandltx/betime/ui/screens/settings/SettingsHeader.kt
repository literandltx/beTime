package com.literandltx.betime.ui.screens.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.literandltx.betime.R
import com.literandltx.betime.ui.shared.SectionHeader

@Composable
fun SettingsHeader(
    modifier: Modifier = Modifier
) {
    SectionHeader(
        title = stringResource(R.string.settingsHeaderTitle),
        modifier = modifier.fillMaxWidth()
    )
}
