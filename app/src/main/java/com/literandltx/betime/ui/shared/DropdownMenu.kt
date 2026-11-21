package com.literandltx.betime.ui.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.literandltx.betime.R

@Composable
fun OptionsMenu(
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        MoreOptionsIconButton(
            onClick = { expanded = true },
            modifier = Modifier.size(36.dp)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.editAction)) },
                onClick = {
                    expanded = false
                    onEditClick()
                },
                leadingIcon = { DropdownMenuIcon(imageVector = Icons.Outlined.Edit) }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.deleteAction)) },
                onClick = {
                    expanded = false
                    onDeleteClick()
                },
                leadingIcon = { DropdownMenuIcon(imageVector = Icons.Outlined.Delete) }
            )
        }
    }
}
