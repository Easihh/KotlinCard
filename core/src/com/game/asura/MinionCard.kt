package com.game.asura

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.game.asura.card.AllCard
import com.game.asura.card.CardEffect
import com.game.asura.card.CardType
import com.game.asura.card.Minion
import kotlin.random.Random

val INVALID_MINION_CARD = MinionCard(-1, -1, 99, CardType.INVALID, -1, -1, -1)

class MinionCard(primaryId: Int,
                 secondaryId: Int = Random.nextInt(),
                 cardCost: Int,
                 cardType: CardType,
                 private val attack: Int = AllCard.getCard(primaryId).attributes.attack,
                 private var health: Int = AllCard.getCard(primaryId).attributes.health,
                 private var maxHealth: Int = AllCard.getCard(primaryId).attributes.maxHealth) : DrawableCard(primaryId, secondaryId, cardCost, cardType), Minion {

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

    override fun getAttack(): Int? {
        return attack
    }

    override fun getHealth(): Int {
        return health
    }

    override fun getMaxHealth(): Int {
        return maxHealth
    }

    override fun takeDamage(dmg: Int) {
        health -= dmg
    }
}