package com.ismartcoding.plain.ui.page.feeds


import android.annotation.SuppressLint
import android.content.ClipData
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material.icons.outlined.SaveAs
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.ismartcoding.lib.extensions.cut
import com.ismartcoding.lib.helpers.CoroutinesHelper.withIO
import com.ismartcoding.lib.helpers.JsonHelper.jsonEncode
import com.ismartcoding.plain.R
import com.ismartcoding.plain.clipboardManager
import com.ismartcoding.plain.enums.DataType
import com.ismartcoding.plain.extensions.timeAgo
import com.ismartcoding.plain.features.NoteHelper
import com.ismartcoding.plain.features.feed.FeedEntryHelper
import com.ismartcoding.plain.features.feed.FeedHelper
import com.ismartcoding.plain.features.feed.fetchContentAsync
import com.ismartcoding.plain.features.locale.LocaleHelper
import com.ismartcoding.plain.helpers.ShareHelper
import com.ismartcoding.plain.ui.base.ActionButtonMoreWithMenu
import com.ismartcoding.plain.ui.base.BottomSpace
import com.ismartcoding.plain.ui.base.PDropdownMenuItem
import com.ismartcoding.plain.ui.base.PIconButton
import com.ismartcoding.plain.ui.base.PScaffold
import com.ismartcoding.plain.ui.base.PTopAppBar
import com.ismartcoding.plain.ui.base.VerticalSpace
import com.ismartcoding.plain.ui.base.markdowntext.MarkdownText
import com.ismartcoding.plain.ui.base.pullrefresh.PullToRefresh
import com.ismartcoding.plain.ui.base.pullrefresh.PullToRefreshContent
import com.ismartcoding.plain.ui.base.pullrefresh.RefreshContentState
import com.ismartcoding.plain.ui.base.pullrefresh.rememberRefreshLayoutState
import com.ismartcoding.plain.ui.components.mediaviewer.previewer.MediaPreviewer
import com.ismartcoding.plain.ui.components.mediaviewer.previewer.rememberPreviewerState
import com.ismartcoding.plain.ui.nav.navigateText
import com.ismartcoding.plain.ui.helpers.DialogHelper
import com.ismartcoding.plain.ui.helpers.WebHelper
import com.ismartcoding.plain.ui.models.FeedEntryViewModel
import com.ismartcoding.plain.ui.models.TagsViewModel
import com.ismartcoding.plain.ui.page.tags.SelectTagsDialog
import com.ismartcoding.plain.ui.theme.PlainTheme
import com.ismartcoding.plain.ui.theme.buttonTextLarge
import com.ismartcoding.plain.ui.theme.largeBlockButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import kotlin.math.abs

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, FlowPreview::class, ExperimentalLayoutApi::class)
@Composable
fun FeedEntryPage(
    navController: NavHostController,
    id: String,
    viewModel: FeedEntryViewModel = viewModel(),
    tagsViewModel: TagsViewModel = viewModel(),
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val tagsState by tagsViewModel.itemsFlow.collectAsState()
    val tagsMapState by tagsViewModel.tagsMapFlow.collectAsState()
    val tagIds = tagsMapState[id]?.map { it.tagId } ?: emptyList()
    val scrollState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(canScroll = {
        scrollState.firstVisibleItemIndex > 0
    })
    val previewerState = rememberPreviewerState()
    val topRefreshLayoutState =
        rememberRefreshLayoutState {
            scope.launch {
                viewModel.item.value?.let { m ->
                    val r = withIO {
                        m.fetchContentAsync()
                    }
                    if (r.isOk()) {
                        viewModel.content.value = m.content
                        setRefreshState(RefreshContentState.Finished)
                    } else {
                        setRefreshState(RefreshContentState.Failed)
                        DialogHelper.showErrorDialog(r.errorMessage())
                    }
                }.also {
                    if (it == null) {
                        setRefreshState(RefreshContentState.Finished)
                    }
                }
            }
        }

    LaunchedEffect(Unit) {
        tagsViewModel.dataType.value = DataType.FEED_ENTRY
        scope.launch(Dispatchers.IO) {
            viewModel.item.value = FeedEntryHelper.getAsync(id)
            val m = viewModel.item.value ?: return@launch
            viewModel.content.value = m.content
            viewModel.feed.value = FeedHelper.getById(m.feedId)
        }
    }

    if (viewModel.showSelectTagsDialog.value) {
        val m = viewModel.item.value
        if (m != null) {
            SelectTagsDialog(tagsViewModel, tagsState, tagsMapState, data = m) {
                viewModel.showSelectTagsDialog.value = false
            }
        }
    }

    BackHandler(previewerState.visible) {
        scope.launch {
            previewerState.close()
        }
    }

    PScaffold(
        topBar = {
            PTopAppBar(
                modifier = Modifier.combinedClickable(onClick = {}, onDoubleClick = {
                    scope.launch {
                        scrollState.scrollToItem(0)
                    }
                }),
                navController = navController,
                title = "",
                scrollBehavior = scrollBehavior,
                actions = {
                    PIconButton(
                        icon = Icons.AutoMirrored.Outlined.Label,
                        contentDescription = stringResource(R.string.select_tags),
                        tint = MaterialTheme.colorScheme.onSurface,
                    ) {
                        viewModel.showSelectTagsDialog.value = true
                    }
                    PIconButton(
                        icon = Icons.Outlined.Share,
                        contentDescription = stringResource(R.string.share),
                        tint = MaterialTheme.colorScheme.onSurface,
                    ) {
                        val m = viewModel.item.value ?: return@PIconButton
                        ShareHelper.shareText(context, m.title.let { it + "\n" } + m.url)
                    }
                    ActionButtonMoreWithMenu { dismiss ->
                        PDropdownMenuItem(text = { Text(stringResource(R.string.open_in_web)) }, leadingIcon = {
                            Icon(
                                Icons.Outlined.OpenInBrowser,
                                contentDescription = stringResource(id = R.string.open_in_web)
                            )
                        }, onClick = {
                            dismiss()
                            val m = viewModel.item.value ?: return@PDropdownMenuItem
                            WebHelper.open(context, m.url)
                        })
                        PDropdownMenuItem(text = { Text(stringResource(R.string.save_to_notes)) }, leadingIcon = {
                            Icon(
                                Icons.Outlined.SaveAs,
                                contentDescription = stringResource(id = R.string.save_to_notes)
                            )
                        }, onClick = {
                            dismiss()
                            val m = viewModel.item.value ?: return@PDropdownMenuItem
                            scope.launch(Dispatchers.IO) {
                                val c = "# ${m.title}\n\n" + m.content.ifEmpty { m.description }
                                NoteHelper.saveToNotesAsync(m.id) {
                                    title = c.cut(100).replace("\n", "")
                                    content = c
                                }
                                DialogHelper.showMessage(R.string.saved)
                            }
                        })
                        PDropdownMenuItem(text = { Text(stringResource(R.string.copy_link)) }, leadingIcon = {
                            Icon(
                                Icons.Outlined.Link,
                                contentDescription = stringResource(id = R.string.copy_link)
                            )
                        }, onClick = {
                            dismiss()
                            val m = viewModel.item.value ?: return@PDropdownMenuItem
                            val clip = ClipData.newPlainText(LocaleHelper.getString(R.string.link), m.url)
                            clipboardManager.setPrimaryClip(clip)
                            DialogHelper.showTextCopiedMessage(m.url)
                        })
                    }
                },
            )
        },
        modifier = Modifier
            .imePadding(),
        content = { paddingValues ->
            val m = viewModel.item.value ?: return@PScaffold
            PullToRefresh(
                refreshLayoutState = topRefreshLayoutState,
                refreshContent = remember {
                    {
                        PullToRefreshContent(
                            createText = {
                                when (it) {
                                    RefreshContentState.Failed -> stringResource(id = R.string.fetch_failed)
                                    RefreshContentState.Finished -> stringResource(id = R.string.fetched)
                                    RefreshContentState.Refreshing -> stringResource(id = R.string.fetching_content)
                                    RefreshContentState.Dragging -> {
                                        if (abs(getRefreshContentOffset()) < getRefreshContentThreshold()) {
                                            stringResource(id = R.string.pull_down_to_fecth_content)
                                        } else {
                                            stringResource(id = R.string.release_to_fetch)
                                        }
                                    }
                                }
                            }
                        )
                    }
                },
            ) {
                LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    state = scrollState,
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .clip(RoundedCornerShape(PlainTheme.CARD_RADIUS))
                                .combinedClickable(
                                    onDoubleClick = {
                                        navController.navigateText("JSON", jsonEncode(m, pretty = true), "json")
                                    },
                                    onClick = {
                                        WebHelper.open(context, m.url)
                                    }),
                        ) {
                            Text(
                                text = m.title,
                                modifier = Modifier
                                    .padding(8.dp),
                                style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }
                    item {
                        VerticalSpace(dp = 8.dp)
                        val tags = tagsState.filter { tagIds.contains(it.id) }
                        FlowRow(
                            modifier = Modifier.padding(horizontal = PlainTheme.PAGE_HORIZONTAL_MARGIN),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = arrayOf(viewModel.feed.value?.name ?: "", m.author, m.publishedAt.timeAgo()).filter {
                                    it.isNotEmpty()
                                }.joinToString(" · "),
                                style = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp, color = MaterialTheme.colorScheme.secondary),
                            )
                            tags.forEach { tag ->
                                Text(
                                    text = AnnotatedString("#" + tag.name),
                                    modifier = Modifier
                                        .wrapContentHeight()
                                        .align(Alignment.Bottom),
                                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp, color = MaterialTheme.colorScheme.primary),
                                )
                            }
                        }
                        VerticalSpace(dp = 16.dp)
                    }
                    item {
                        MarkdownText(
                            text = viewModel.content.value.ifEmpty { m.description },
                            modifier = Modifier.padding(horizontal = PlainTheme.PAGE_HORIZONTAL_MARGIN),
                            previewerState = previewerState,
                        )
                    }
                    if (viewModel.content.value.isEmpty() && topRefreshLayoutState.refreshContentState.value == RefreshContentState.Finished) {
                        item {
                            VerticalSpace(dp = 32.dp)
                            if (viewModel.fetchingContent.value) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(32.dp),
                                        color = MaterialTheme.colorScheme.secondary,
                                        strokeWidth = 3.dp
                                    )
                                }
                            } else {
                                OutlinedButton(
                                    onClick = {
                                        scope.launch {
                                            viewModel.item.value?.let { m ->
                                                viewModel.fetchingContent.value = true
                                                val r = withIO {
                                                    m.fetchContentAsync()
                                                }
                                                viewModel.fetchingContent.value = false
                                                if (r.isOk()) {
                                                    viewModel.content.value = m.content
                                                } else {
                                                    DialogHelper.showErrorDialog(r.errorMessage())
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .largeBlockButton(),
                                    enabled = !viewModel.fetchingContent.value,
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.load_full_content),
                                        style = MaterialTheme.typography.buttonTextLarge()
                                    )
                                }
                            }

                        }
                    }

                    item {
                        BottomSpace(paddingValues)
                    }
                }
            }
        },
    )

    MediaPreviewer(state = previewerState)
}
