package com.game.asura

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import kotlin.random.Random

val INVALID_CLIENT_CARD = ClientCard(-1, -1, 99, CardType.INVALID)

class ClientCard(private val primaryId: Int,
                 private val secondaryId: Int = Random.nextInt(),
                 private var cardCost: Int,
                 private val cardType: CardType) : DrawableCard {

    private var cardActor: CardActor

    init {
        val picture = if (cardType == CardType.MONSTER) {
            "monsterCard.png"
        } else "card.png"
        val texture = Texture("core/assets/$picture")
        cardActor = CardActor(texture, secondaryId)
    }

    override fun getActor(): Actor {
        return cardActor
    }

    override fun getCost(): Int {
        return cardCost
    }

    override fun getPrimaryId(): Int {
        return primaryId
    }

    override fun getSecondayId(): Int {
        return secondaryId
    }

    override fun getCardType(): CardType {
        return cardType
    }

    override fun getEffect(): List<CardEffect> {
        return ArrayList()
    }
}