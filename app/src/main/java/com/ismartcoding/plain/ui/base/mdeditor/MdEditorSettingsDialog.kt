package com.ismartcoding.plain.ui.base.mdeditor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.ismartcoding.plain.R
import com.ismartcoding.plain.preference.EditorShowLineNumbersPreference
import com.ismartcoding.plain.preference.EditorSyntaxHighlightPreference
import com.ismartcoding.plain.preference.EditorWrapContentPreference
import com.ismartcoding.plain.ui.base.PDialogListItem
import com.ismartcoding.plain.ui.base.PSwitch
import com.ismartcoding.plain.ui.models.MdEditorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MdEditorSettingsDialog(
    viewModel: MdEditorViewModel,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = {
            viewModel.showSettings = false
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.showSettings = false
                }
            ) {
                Text(stringResource(id = R.string.close))
            }
        },
        title = {
            Text(text = stringResource(id = R.string.editor_settings),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column {
                PDialogListItem(
                    title = stringResource(id = R.string.show_line_numbers),
                ) {
                    PSwitch(
                        activated = viewModel.showLineNumbers,
                    ) {
                        viewModel.showLineNumbers = it
                        scope.launch(Dispatchers.IO) {
                            EditorShowLineNumbersPreference.putAsync(context, it)
                        }
                    }
                }
                PDialogListItem(
                    title = stringResource(id = R.string.wrap_content),
                ) {
                    PSwitch(
                        activated = viewModel.wrapContent,
                    ) {
                        viewModel.wrapContent = it
                        scope.launch(Dispatchers.IO) {
                            EditorWrapContentPreference.putAsync(context, it)
                        }
                    }
                }
//                PDialogListItem(
//                    title = stringResource(id = R.string.syntax_highlight),
//                ) {
//                    PSwitch(
//                        activated = viewModel.syntaxHighLight,
//                    ) {
//                        viewModel.syntaxHighLight = it
//                        scope.launch(Dispatchers.IO) {
//                            EditorSyntaxHighlightPreference.putAsync(context, it)
//                        }
//                    }
//                }
            }
        })
}