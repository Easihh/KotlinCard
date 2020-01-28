package com.game.asura

import com.badlogic.gdx.graphics.Texture
import com.game.asura.card.CardEffect
import com.game.asura.card.CardType
import kotlin.random.Random

val INVALID_SPELL_CARD = SpellCard(-1, -1, 99, CardType.INVALID)

class SpellCard(primaryId: Int,
                private val secondaryId: Int = Random.nextInt(),
                cardCost: Int,
                cardType: CardType) : DrawableCard(primaryId, secondaryId, cardCost, cardType) {


    override fun initCardTexture(texture: Texture) {
        actor = HandCard(texture, secondaryId)
    }


    override fun transformActor(texture: Texture) {
        ///destroy current actor
        actor.remove()
        actor = BoardCard(texture, getSecondayId())
    }

    override fun getEffect(): List<CardEffect> {
        return ArrayList()
    }

    override fun toString(): String {
        return "cardType=${getCardType()},cardPrimaryId=${getPrimaryId()}"
    }

}