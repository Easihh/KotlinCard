package com.game.asura.card

enum class CardType(val value: Byte) {
    UNKNOWN('0'.toByte()),
    MONSTER('1'.toByte()),
    SPELL('2'.toByte()),
    TARGET_SPELL('3'.toByte()),
    HERO('4'.toByte()),
    INVALID('5'.toByte());

    companion object {
        private val fieldNumberToName: Map<Byte, CardType> = values().map { it.value to it }.toMap()
        fun getCardType(value: Byte): CardType {
            return fieldNumberToName[value] ?: return UNKNOWN
        }
    }
}