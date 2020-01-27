package com.game.asura

enum class Phase(val value: Byte) {
    MAIN('0'.toByte()),
    ATTACK('1'.toByte()),
    POST_ATTACK('2'.toByte());

    companion object {
        private val FIELD_NUMBER_TO_NAME: Map<Byte, Phase> = values().map { it.value to it }.toMap()
        fun getPhase(value: Byte): Phase {
            return FIELD_NUMBER_TO_NAME[value] ?: return MAIN
        }
    }
}