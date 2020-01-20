package com.game.asura

enum class MatchResult(val value: Byte) {
    UNKNOWN('0'.toByte()),
    WIN('1'.toByte()),
    LOSS('2'.toByte()),
    TIE('3'.toByte());

    companion object {
        private val fieldNumberToName: Map<Byte, MatchResult> = values().map { it.value to it }.toMap()
        fun getMatchResultType(value: Byte): MatchResult {
            return fieldNumberToName[value] ?: return UNKNOWN
        }
    }
}