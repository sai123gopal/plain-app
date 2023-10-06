package com.ismartcoding.plain.data.enums

enum class DataType(val value: Int) {
    DEFAULT(0),
    AUDIO(1),
    VIDEO(2),
    IMAGE(3),
    SMS(4),
    CONTACT(5),
    NOTE(6),
    FEED_ENTRY(7),
    CALL(8),
    BOOK(9),
    AI_CHAT(10),
    PACKAGE(21),
    FILE(22),
    ; // starts from 21, not used for tag

    companion object {
        fun fromInt(value: Int) = values().first { it.value == value }
    }
}
