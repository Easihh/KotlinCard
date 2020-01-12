package com.game.asura

import com.game.asura.card.AllCard
import com.game.asura.card.BaseCard
import com.game.asura.card.CardType
import com.game.asura.card.Minion
import kotlin.random.Random

val INVALID_MINION_CARD = ServerMinionCard(-1, -1, 99, CardType.INVALID, null, -1, -1)

class ServerMinionCard(primaryId: Int,
                       secondaryId: Int = Random.nextInt(),
                       cardCost: Int = 0,
                       cardType: CardType = CardType.MONSTER,
                       private val attack: Int? = AllCard.getCard(primaryId).attributes.attack,
                       private var health: Int = AllCard.getCard(primaryId).attributes.health,
                       private var maxHealth: Int = AllCard.getCard(primaryId).attributes.maxHealth) : BaseCard(primaryId, secondaryId, cardCost, cardType), Minion {

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