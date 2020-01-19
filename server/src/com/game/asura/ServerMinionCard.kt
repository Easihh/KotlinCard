package com.game.asura

import com.game.asura.card.BaseCard
import com.game.asura.card.CardType
import com.game.asura.card.Minion
import kotlin.random.Random

val INVALID_MINION_CARD = ServerMinionCard(-1, -1, 99, CardType.INVALID, null, -1, -1)

class ServerMinionCard(primaryId: Int,
                       secondaryId: Int = Random.nextInt(),
                       cardCost: Int = 0,
                       cardType: CardType = CardType.MONSTER,
                       private val attack: Int? = null,
                       private var health: Int =0,
                       private var maxHealth: Int = 0) : BaseCard(primaryId, secondaryId, cardCost, cardType), Minion {

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

    override fun isAlive(): Boolean {
        return health > 0
    }
}