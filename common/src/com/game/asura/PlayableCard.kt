package com.game.asura

import kotlin.random.Random

class PlayableCard(private val primaryId: Int,
                   private val secondaryId: Int = Random.nextInt(),
                   private var cardCost: Int,
                   private val cardType: CardType) : Card {
    private val effects: MutableList<CardEffect> = ArrayList()

    init {
        effects.add(AllCard.getCard(primaryId).effect)
    }

    override fun getEffect(): List<CardEffect> {
        return effects
    }

    override fun getPrimaryId(): Int {
        return primaryId
    }

    override fun getSecondayId(): Int {
        return secondaryId
    }

    override fun getCost(): Int {
        return cardCost
    }

    override fun getCardType(): CardType {
        return cardType
    }
}