package com.game.asura.messaging

enum class MessageField(val fieldNumber: Int,
                        val type: Byte) {
    UNKNOWN(0, 'X'.toByte()),
    MESSAGE_TYPE(1, 'B'.toByte()),
    GAME_TYPE(2, 'B'.toByte()),
    CONN_STATUS(3, 'B'.toByte()),
    CARD_HEALTH(4, 'I'.toByte()),
    CARD_MAX_HEALTH(5, 'I'.toByte()),
    PLAYER_CURRENT_ARMOR(6, 'I'.toByte()),
    BOARD_POSITION(7, 'I'.toByte()),
    PRIMARY_CARD_ID(8, 'I'.toByte()),
    CARD_COST(9, 'I'.toByte()),
    PLAYER_CURRENT_MANA(10, 'I'.toByte()),
    PLAYER_MAX_MANA(11, 'I'.toByte()),
    CARD_TYPE(12, 'B'.toByte()),
    ACCOUNT_NAME(13, 'S'.toByte()),
    CONNECTION_IDENTIFIER(14, 'L'.toByte()),
    ENEMY_ACCOUNT_NAME(15, 'S'.toByte()),
    SECONDARY_CARD_ID(16, 'I'.toByte()),
    DECK_SIZE(17, 'I'.toByte()),
    CARD_TARGET(18, 'I'.toByte()),
    END_MESSAGE(19, 'C'.toByte()),
    MATCH_TURN(20, 'I'.toByte()),
    HERO_PRIMARY_ID(21, 'I'.toByte()),
    HERO_SECONDARY_ID(22, 'I'.toByte()),
    ENEMY_HERO_PRIMARY_ID(23, 'I'.toByte()),
    ENEMY_HERO_SECONDARY_ID(24, 'I'.toByte()),
    CARD_ATTACK(25, 'I'.toByte()),
    MATCH_RESULT(26, 'B'.toByte()),
    ;

    companion object {
        private val fieldNumberToName: Map<Int, MessageField> = values().map { it.fieldNumber to it }.toMap()

        fun getMessageField(fieldNumber: Int): MessageField {
            return fieldNumberToName[fieldNumber] ?: return UNKNOWN
        }
    }
}