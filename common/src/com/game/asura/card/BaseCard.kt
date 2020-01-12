package com.game.asura.card

import kotlin.random.Random

abstract class BaseCard(private val primaryId: Int,
                        private val secondaryId: Int = Random.nextInt(),
                        private val cardCost: Int,
                        private val cardType: CardType) : Card {

    private val effects: MutableList<CardEffect> = ArrayList()

    init {
        effects.add(AllCard.getCard(primaryId).effect)
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

    override fun getEffect(): List<CardEffect> {
        return effects
    }
}