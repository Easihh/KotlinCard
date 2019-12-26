package com.game.asura

enum class CardEffect(private val id: Int) {
    UNKNOWN(0),
    NONE(1),
    DEAL_DMG(2),
    MINION_DMG(3),
    CARD_DRAW(4);

    companion object {
        private val fieldNumberToName: Map<Int, CardEffect> = values().map { it.id to it }.toMap()
        fun getCardEffect(id: Int): CardEffect {
            return fieldNumberToName[id] ?: return UNKNOWN
        }
    }
}