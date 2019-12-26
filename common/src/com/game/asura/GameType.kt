package com.game.asura

enum class GameType(val value: Byte) {
    UNKNOWN('0'.toByte()),
    NORMAL('1'.toByte()),
    EXTRA('2'.toByte());

    companion object {
        private val fieldNumberToName: Map<Byte, GameType> = values().map { it.value to it }.toMap()
        fun getGameType(value: Byte): GameType {
            return fieldNumberToName[value] ?: return UNKNOWN
        }
    }
}