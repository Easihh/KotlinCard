package com.game.asura

import com.game.asura.card.BaseMinion
import com.game.asura.card.CardType
import kotlin.random.Random

class ServerMinionCard(primaryId: Int,
                       secondaryId: Int = Random.nextInt(),
                       cardCost: Int = 0,
                       cardType: CardType = CardType.MONSTER,
                       private val attack: Int = 0,
                       private var health: Int = 0,
                       private var maxHealth: Int = 0,
                       val evolveId: Int? = null,
                       owner: String) : BaseMinion(primaryId, secondaryId, cardCost, cardType,owner) {


    private var summonSickness = true

    override fun removeSummonSickness() {
        summonSickness = false
    }

    override fun isSummonSick(): Boolean {
        return summonSickness
    }

    fun canEvolve(): Boolean {
        return evolveId != null
    }

    override fun getAttack(): Int {
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