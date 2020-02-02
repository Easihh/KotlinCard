package com.game.asura.card

import kotlin.random.Random

abstract class BaseMinion(private val primaryId: Int,
                          private val secondaryId: Int = Random.nextInt(),
                          private val cardCost: Int,
                          private val cardType: CardType,
                          private val owner: String) : Minion {
    override fun getOwner(): String {
        return owner
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

    override fun getCost(): Int {
        return cardCost
    }
}