package com.game.asura

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.game.asura.card.CardEffect
import com.game.asura.card.CardType
import kotlin.random.Random

val INVALID_MINION_CARD = MinionCard(-1, -1, 99, CardType.INVALID)

class MinionCard(primaryId: Int,
                 private val secondaryId: Int = Random.nextInt(),
                 cardCost: Int,
                 cardType: CardType) : MonsterDrawableCard(primaryId, secondaryId, cardCost, cardType) {

    private lateinit var currentActor: Actor

    override fun initCardTexture(texture: Texture) {
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