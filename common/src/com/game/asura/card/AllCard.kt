package com.game.asura.card

enum class AllCard(val id: Int,
                   val cardType: CardType,
                   val cost: Int,
                   val effect: CardEffect) {
    UNKNOWN(0, CardType.UNKNOWN, 0, CardEffect.UNKNOWN),
    FIRST_MONSTER(1, CardType.MONSTER, 1, CardEffect.NONE),
    FIRST_TARGET_SPELL(2, CardType.TARGET_SPELL, 2, CardEffect.DEAL_DMG),
    FIRST_SPELL(3, CardType.SPELL, 3, CardEffect.CARD_DRAW),
    MONSTER_A(4, CardType.MONSTER, 4, CardEffect.NONE),
    MAGE_HERO(5, CardType.HERO, 4, CardEffect.NONE),
    ;

    companion object {
        private val fieldNumberToName: Map<Int, AllCard> = values().map { it.id to it }.toMap()
        fun getCard(id: Int): AllCard {
            return fieldNumberToName[id] ?: return UNKNOWN
        }
    }
}