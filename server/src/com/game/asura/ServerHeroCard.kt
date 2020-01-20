package com.game.asura

import com.game.asura.card.BaseCard
import com.game.asura.card.CardType
import com.game.asura.card.Minion

class ServerHeroCard(primaryId: Int,
                     secondaryId: Int,
                     cardCost: Int = 0,
                     cardType: CardType = CardType.HERO) : BaseCard(primaryId, secondaryId, cardCost, cardType), Minion {

    private var attack: Int = 0
    private var health: Int = 30
    private var maxHealth: Int = 30
    private var currentMana: Int = 0
    private var maxMana: Int = 10


    fun updateMana(cost: Int) {
        currentMana -= cost
    }

    fun getCurrentMana(): Int {
        return currentMana
    }
    fun getMaxMana(): Int {
        return maxMana
    }
    override fun getAttack(): Int {
        return attack
    }

    override fun takeDamage(dmg: Int) {
        health -= dmg
    }

    override fun getHealth(): Int {
        return health
    }

    override fun getMaxHealth(): Int {
        return maxHealth
    }

    override fun isAlive(): Boolean {
        return health > 0
    }
}