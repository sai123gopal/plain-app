package com.ismartcoding.plain.ui.base.mdeditor

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LooksOne
import androidx.compose.material.icons.outlined.LooksTwo
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ismartcoding.plain.R
import com.ismartcoding.plain.features.locale.LocaleHelper
import com.ismartcoding.plain.ui.base.PIconButton
import com.ismartcoding.plain.ui.components.ColorPickerDialog
import com.ismartcoding.plain.ui.extensions.inlineWrap
import com.ismartcoding.plain.ui.helpers.DialogHelper
import com.ismartcoding.plain.ui.models.MdEditorViewModel
import com.ismartcoding.plain.ui.theme.bottomAppBarContainer
import com.ismartcoding.plain.ui.theme.palette.checkColorHex

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun MdEditorBottomAppBar(
    viewModel: MdEditorViewModel,
) {
    val scrollState = rememberScrollState()
    val scrollState2 = rememberScrollState()
    val context = LocalContext.current

    if (viewModel.showSettings) {
        MdEditorSettingsDialog(viewModel = viewModel)
    }
    if (viewModel.showInsertImage) {
        MdEditorInsertImageDialog(viewModel = viewModel)
    }
    if (viewModel.showColorPicker) {
        ColorPickerDialog(
            stringResource(id = R.string.pick_color),
            initValue = "FFFFFFFF",
            onDismiss = {
                viewModel.showColorPicker = false
            }, onConfirm = {
                val hex = it.checkColorHex()
                if (hex != null) {
                    viewModel.insertColor("#$hex")
                } else {
                    DialogHelper.showMessage(LocaleHelper.getString(R.string.invalid_value))
                }
            })
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.bottomAppBarContainer()),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (viewModel.level == 0) {
            Row(
                modifier =
                Modifier
                    .weight(1f)
                    .horizontalScroll(scrollState),
            ) {
                MdEditorViewModel.mdAccessoryItems.forEach { button ->
                    TextButton(onClick = {
                        viewModel.textFieldState.edit { inlineWrap(button.before, button.after) }
                    }) {
                        Text(button.text, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp))
                    }
                }
            }
        } else {
            Row(
                modifier =
                Modifier
                    .weight(1f)
                    .horizontalScroll(scrollState2),
            ) {
                MdEditorViewModel.mdAccessoryItems2.forEach { button ->
                    PIconButton(
                        icon = button.icon,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.primary,
                        onClick = {
                            button.click(viewModel)
                        },
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.CenterEnd
        ) {
            PIconButton(
                icon = if (viewModel.level == 0) Icons.Outlined.LooksOne else Icons.Outlined.LooksTwo,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onPrimary,
                onClick = {
                    viewModel.toggleLevel(context)
                },
            )
        }
    }
}