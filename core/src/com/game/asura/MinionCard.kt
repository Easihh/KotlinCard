package com.game.asura

import com.badlogic.gdx.graphics.Texture
import com.game.asura.card.CardType
import kotlin.random.Random

class MinionCard(texture: Texture,
                 primaryId: Int,
                 secondaryId: Int = Random.nextInt(),
                 cardCost: Int,
                 cardType: CardType,
                 attack: Int,
                 health: Int,
                 maxHealth: Int,
                 owner:String) : MonsterDrawableCard(texture, primaryId, secondaryId, cardCost, cardType, attack, health, maxHealth,owner) {


    override fun isSummonSick(): Boolean {
        return summonSickness
    }

    override fun removeSummonSickness() {
        summonSickness = false
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