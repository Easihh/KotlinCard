package com.game.asura

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.game.asura.card.CardEffect
import com.game.asura.card.CardType
import kotlin.random.Random

val INVALID_SPELL_CARD = SpellCard(-1, -1, 99, CardType.INVALID)

class SpellCard(primaryId: Int,
                secondaryId: Int = Random.nextInt(),
                cardCost: Int,
                cardType: CardType) : DrawableCard(primaryId, secondaryId, cardCost, cardType) {


    private var currentActor: Actor

    init {
        val picture = if (cardType == CardType.MONSTER) {
            "monsterCard.png"
        } else "card.png"
        val texture = Texture("core/assets/$picture")
        currentActor = HandCard(texture, secondaryId)
    }

    override fun getActor(): Actor {
        return currentActor
    }

    override fun transformActor(texture: Texture) {
        ///destroy current actor
        currentActor.remove()
        currentActor = BoardCard(texture, getSecondayId())
    }

    override fun getEffect(): List<CardEffect> {
        return ArrayList()
    }

    override fun toString(): String {
        return "cardType=${getCardType()},cardPrimaryId=${getPrimaryId()}"
    }

}