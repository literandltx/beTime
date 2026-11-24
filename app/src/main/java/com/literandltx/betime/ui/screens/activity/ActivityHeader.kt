package com.literandltx.betime.ui.screens.activity

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.literandltx.betime.R
import com.literandltx.betime.ui.shared.EditIconButton
import com.literandltx.betime.ui.shared.SectionHeader

@Composable
fun ActivityHeader(
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit = {}
) {
    SectionHeader(
        title = stringResource(R.string.activityHeaderTitle),
        modifier = modifier,
        dividerPadding = PaddingValues(0.dp)
    ) {
        EditIconButton(onClick = onEditClick)
    }
}
