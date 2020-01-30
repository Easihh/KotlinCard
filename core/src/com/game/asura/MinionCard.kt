package com.game.asura

import com.badlogic.gdx.graphics.Texture
import com.game.asura.card.CardEffect
import com.game.asura.card.CardType
import kotlin.random.Random

val INVALID_MINION_CARD = MinionCard(-1, -1, 99, CardType.INVALID, -1, -1, -1)

class MinionCard(primaryId: Int,
                 private val secondaryId: Int = Random.nextInt(),
                 cardCost: Int,
                 cardType: CardType,
                 attack: Int,
                 health: Int,
                 maxHealth: Int) : MonsterDrawableCard(primaryId, secondaryId, cardCost, cardType, attack, health, maxHealth) {


    override fun isSummonSick(): Boolean {
        return summonSickness
    }

    override fun removeSummonSickness() {
        summonSickness = false
    }

    override fun initCardTexture(texture: Texture) {
        actor = HandCard(texture, secondaryId)
    }

    override fun transformActor(texture: Texture) {
        ///destroy current actor
        actor.remove()
        actor = BoardCard(texture, getSecondayId())
    }

    override fun toString(): String {
        return "cardType=${getCardType()},cardPrimaryId=${getPrimaryId()},cardSecondaryId=${getSecondayId()}," +
                "attack=${getAttack()},health=${getHealth()},maxHealth=${getMaxHealth()}"
    }
}