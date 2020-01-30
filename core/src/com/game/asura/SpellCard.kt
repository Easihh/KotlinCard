package com.game.asura

import com.badlogic.gdx.graphics.Texture
import com.game.asura.card.CardType
import kotlin.random.Random

val INVALID_SPELL_CARD = SpellCard(NULL_CARDTEXTURE, -1, -1, 99, CardType.INVALID)

class SpellCard(texture: Texture,
                primaryId: Int,
                secondaryId: Int = Random.nextInt(),
                cardCost: Int,
                cardType: CardType) : DrawableCard(texture, primaryId, secondaryId, cardCost, cardType) {

    override fun toString(): String {
        return "cardType=${getCardType()},cardPrimaryId=${getPrimaryId()}"
    }
}