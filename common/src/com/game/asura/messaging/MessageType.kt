package com.game.asura.messaging

enum class MessageType(val value: Byte) {
    UNKNOWN('u'.toByte()),
    LOGIN_REQUEST('l'.toByte()),
    GAME_REQUEST('g'.toByte()),
    CONNECTION_STATE('s'.toByte()),
    CARD_INFO('i'.toByte()),
    CARD_PLAYED('c'.toByte()),
    CARD_DRAWN('d'.toByte()),
    MATCH_INFO('m'.toByte()),
    START_TURN('s'.toByte()),
    END_TURN('e'.toByte()),
    HERO_POWER('h'.toByte()),
    MONSTER_ATTACK('a'.toByte());


    companion object {
        private val fieldNumberToName: Map<Byte, MessageType> = values().map { it.value to it }.toMap()
        fun getMessageType(value: Byte): MessageType {
            return fieldNumberToName[value] ?: return UNKNOWN
        }
    }
}