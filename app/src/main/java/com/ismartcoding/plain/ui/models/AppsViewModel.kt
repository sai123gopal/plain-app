package com.ismartcoding.plain.ui.models

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import com.ismartcoding.plain.R
import com.ismartcoding.plain.features.PackageHelper
import com.ismartcoding.plain.features.file.FileSortBy
import com.ismartcoding.plain.features.locale.LocaleHelper.getString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(SavedStateHandleSaveableApi::class)
class AppsViewModel(private val savedStateHandle: SavedStateHandle) : ISearchableViewModel<VPackage>, ViewModel() {
    private val _itemsFlow = MutableStateFlow(mutableStateListOf<VPackage>())
    val itemsFlow: StateFlow<List<VPackage>> get() = _itemsFlow
    var showLoading = mutableStateOf(true)
    var offset = mutableIntStateOf(0)
    var limit = mutableIntStateOf(50)
    var noMore = mutableStateOf(false)
    var appType = mutableStateOf("")
    var total = mutableIntStateOf(0)
    var totalSystem = mutableIntStateOf(0)
    val showSortDialog = mutableStateOf(false)
    val sortBy = mutableStateOf(FileSortBy.NAME_ASC)
    var tabs = mutableStateOf(listOf<VTabData>())

    override val showSearchBar = mutableStateOf(false)
    override val searchActive = mutableStateOf(false)
    override val queryText = mutableStateOf("")

    suspend fun moreAsync() {
        offset.value += limit.value
        val items = PackageHelper.searchAsync(getQuery(), limit.value, offset.value, sortBy.value).map { VPackage.from(it) }
        _itemsFlow.value.addAll(items)
        showLoading.value = false
        noMore.value = items.size < limit.value
    }

    suspend fun loadAsync() {
        offset.value = 0
        _itemsFlow.value = PackageHelper.searchAsync(getQuery(), limit.value, 0, sortBy.value).map { VPackage.from(it) }.toMutableStateList()
        total.value = PackageHelper.count(queryText.value)
        totalSystem.value = PackageHelper.count("${queryText.value} type:system")
        noMore.value = _itemsFlow.value.size < limit.value
        tabs.value = listOf(
            VTabData(getString(R.string.all), "", total.value),
            VTabData(getString(R.string.app_type_system), "system", totalSystem.value),
            VTabData(getString(R.string.app_type_user), "user", total.value - totalSystem.value)
        )
        showLoading.value = false
    }

    private fun getQuery(): String {
        var query = queryText.value
        if (appType.value.isNotEmpty()) {
            query += " type:${appType.value}"
        }
        return query
    }
}
