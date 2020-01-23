package com.game.asura.messaging

enum class MessageType(val value: Byte) {
    UNKNOWN('u'.toByte()),
    LOGIN_REQUEST('l'.toByte()),
    GAME_REQUEST('g'.toByte()),
    LOGIN_REPLY('r'.toByte()),
    CARD_INFO('i'.toByte()),
    PLAYER_INFO('p'.toByte()),
    CARD_PLAYED('c'.toByte()),
    CARD_DRAWN('d'.toByte()),
    MATCH_START('m'.toByte()),
    MATCH_END('t'.toByte()),
    START_TURN('s'.toByte()),
    END_TURN('e'.toByte()),
    MONSTER_ATTACK('a'.toByte()),
    MONSTER_EVOLVE('v'.toByte());


    companion object {
        private val fieldNumberToName: Map<Byte, MessageType> = values().map { it.value to it }.toMap()
        fun getMessageType(value: Byte): MessageType {
            return fieldNumberToName[value] ?: return UNKNOWN
        }
    }
}