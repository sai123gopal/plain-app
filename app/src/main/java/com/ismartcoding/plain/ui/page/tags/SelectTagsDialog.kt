package com.ismartcoding.plain.ui.page.tags

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ismartcoding.plain.R
import com.ismartcoding.plain.db.DTag
import com.ismartcoding.plain.db.DTagRelation
import com.ismartcoding.plain.ui.base.PSelectionChip
import com.ismartcoding.plain.ui.components.NewTagButton
import com.ismartcoding.plain.ui.components.TagNameDialog
import com.ismartcoding.plain.ui.models.TagsViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SelectTagsDialog(
    tagsViewModel: TagsViewModel,
    tagsState: List<DTag>,
    tagsMap: Map<String, List<DTagRelation>>,
    id: String,
    onDismiss: () -> Unit,
) {
    val tagIds = remember {
        mutableStateListOf<String>()
    }

    TagNameDialog(tagsViewModel)

    LaunchedEffect(Unit) {
        tagIds.addAll(tagsMap[id]?.map { it.tagId } ?: emptyList())
    }

    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        title = {
            Text(text = stringResource(id = R.string.select_tags),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            FlowRow(
                modifier =
                Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                tagsState.forEach { tag ->
                    PSelectionChip(
                        selected = tagIds.contains(tag.id),
                        onClick = {
                            tagsViewModel.toggleTag(id, tag.id)
                            if (tagIds.contains(tag.id)) {
                                tagIds.remove(tag.id)
                            } else {
                                tagIds.add(tag.id)
                            }
                        },
                        text = tag.name
                    )
                }
                NewTagButton(click = {
                    tagsViewModel.showAddDialog()
                })
            }
        }, confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                },
            ) {
                Text(stringResource(id = R.string.close))
            }
        }
    )
}


