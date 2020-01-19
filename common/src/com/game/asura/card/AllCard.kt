package com.game.asura.card

enum class AllCard(val id: Int,
                   val cardType: CardType,
                   val cost: Int,
                   val effect: CardEffect,
                   val attributes: CardAttributes = CardAttributes(mapOf())) {

    UNKNOWN(0, CardType.UNKNOWN, 0, CardEffect.UNKNOWN),
    SLIME(1, CardType.MONSTER, 1, CardEffect.NONE, CardAttributes(mapOf("attack" to 3, "health" to 3, "maxHealth" to 3))),
    FIRST_TARGET_SPELL(2, CardType.TARGET_SPELL, 2, CardEffect.DEAL_DMG, CardAttributes(mapOf("spellDmg" to 5))),
    FIRST_SPELL(3, CardType.SPELL, 3, CardEffect.CARD_DRAW),
    KING_SLIME(4, CardType.MONSTER, 4, CardEffect.NONE, CardAttributes(mapOf("attack" to 1, "health" to 3, "maxHealth" to 3))),
    MAGE_HERO(5, CardType.HERO, 4, CardEffect.NONE,CardAttributes(mapOf("attack" to 0, "health" to 30, "maxHealth" to 30))),
    ;
}