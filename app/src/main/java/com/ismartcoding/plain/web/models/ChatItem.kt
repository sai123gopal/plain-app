package com.ismartcoding.plain.web.models

import com.ismartcoding.plain.db.*
import com.ismartcoding.plain.helpers.FileHelper
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ChatItem(
    val id: ID,
    val isMe: Boolean,
    val content: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    @Transient private val _content: DMessageContent? = null,
    @Contextual var data: ChatItemContent? = null,
) {
    fun getContentData(): ChatItemContent? {
        return when (_content?.value) {
            is DMessageImages -> {
                ChatItemContent.MessageImages((_content.value as DMessageImages).items.map { FileHelper.getFileId(it.uri) })
            }

            is DMessageFiles -> {
                ChatItemContent.MessageFiles((_content.value as DMessageFiles).items.map { FileHelper.getFileId(it.uri) })
            }

            else -> {
                null
            }
        }
    }
}

@Polymorphic
@Serializable
sealed class ChatItemContent() {
    @Serializable
    data class MessageImages(val ids: List<String>) : ChatItemContent()
    @Serializable
    data class MessageFiles(val ids: List<String>) : ChatItemContent()
}

fun DChat.toModel(): ChatItem {
    return ChatItem(ID(id), isMe, content.toJSONString(), createdAt, updatedAt, content)
}