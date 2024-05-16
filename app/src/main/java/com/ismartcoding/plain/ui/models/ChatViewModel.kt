package com.ismartcoding.plain.ui.models

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ismartcoding.lib.channel.sendEvent
import com.ismartcoding.lib.extensions.toAppUrl
import com.ismartcoding.plain.TempData
import com.ismartcoding.plain.data.IData
import com.ismartcoding.plain.db.AppDatabase
import com.ismartcoding.plain.db.DChat
import com.ismartcoding.plain.db.DMessageFile
import com.ismartcoding.plain.db.DMessageFiles
import com.ismartcoding.plain.db.DMessageImages
import com.ismartcoding.plain.db.DMessageType
import com.ismartcoding.plain.features.ChatHelper
import com.ismartcoding.plain.web.websocket.EventType
import com.ismartcoding.plain.web.websocket.WebSocketEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import org.json.JSONArray

data class VChat(override var id: String, val name: String, val createdAt: Instant, val type: String, var value: Any? = null) : IData {
    companion object {
        fun from(data: DChat): VChat {
            return VChat(data.id, data.name, data.createdAt, data.content.type, data.content.value)
        }
    }
}

class ChatViewModel : ISelectableViewModel<VChat>, ViewModel() {
    private val _itemsFlow = MutableStateFlow(mutableStateListOf<VChat>())
    override val itemsFlow: StateFlow<List<VChat>> get() = _itemsFlow
    val selectedItem = mutableStateOf<VChat?>(null)
    override var selectMode = mutableStateOf(false)
    override val selectedIds = mutableStateListOf<String>()

    fun fetch(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val dao = AppDatabase.instance.chatDao()
            val list = dao.getAll()
            if (!TempData.chatItemsMigrated) {
                TempData.chatItemsMigrated = true
                list.filter { setOf(DMessageType.IMAGES.value, DMessageType.FILES.value).contains(it.content.type) }.forEach {
                    if (it.content.value is DMessageImages) {
                        val c = it.content.value as DMessageImages
                        if (c.items.any { i -> !i.uri.startsWith("app://") }) {
                            it.content.value =
                                DMessageImages(
                                    c.items.map { i ->
                                        DMessageFile(i.uri.toAppUrl(context), i.size, i.duration)
                                    },
                                )
                            dao.update(it)
                        }
                    } else if (it.content.value is DMessageFiles) {
                        val c = it.content.value as DMessageFiles
                        if (c.items.any { i -> !i.uri.startsWith("app://") }) {
                            it.content.value = DMessageFiles(c.items.map { i -> DMessageFile(i.uri.toAppUrl(context), i.size, i.duration) })
                            dao.update(it)
                        }
                    }
                }
            }
            _itemsFlow.value = list.sortedByDescending { it.createdAt }.map { VChat.from(it) }.toMutableStateList()
        }
    }

    fun remove(id: String) {
        _itemsFlow.value.removeIf { it.id == id }
    }

    fun addAll(items: List<DChat>) {
        _itemsFlow.value.addAll(0, items.map { VChat.from(it) })
    }

    fun update(item: DChat) {
        val index = _itemsFlow.value.indexOfFirst { it.id == item.id }
        if (index >= 0) {
            _itemsFlow.value[index] = _itemsFlow.value[index].copy(value = item.content.value)
        }
    }

    fun delete(context: Context, ids: Set<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            val json = JSONArray()
            val items = _itemsFlow.value.filter { ids.contains(it.id) }
            for (m in items) {
                ChatHelper.deleteAsync(context, m.id, m.value)
                json.put(m.id)
            }
            _itemsFlow.update {
                val mutableList = it.toMutableStateList()
                mutableList.removeIf { m -> ids.contains(m.id) }
                mutableList
            }
            sendEvent(WebSocketEvent(EventType.MESSAGE_DELETED, json.toString()))
        }
    }
}
