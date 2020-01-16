package com.game.asura

import com.game.asura.card.BaseCard
import com.game.asura.card.Card

class HandManager {

    private val cardsInHand: MutableList<BaseCard> = ArrayList()

    fun getCardsInHand(): List<BaseCard> {
        return cardsInHand
    }

    fun addToPlayerHand(card: BaseCard) {
        cardsInHand.add(card)
    }

    fun getCardFromHand(cardSecondaryId: Int): BaseCard? {
        val optional = cardsInHand.stream().filter { it.getSecondayId() == cardSecondaryId }.findFirst()
        if (optional.isPresent) {
            return optional.get()
        }
        return null
    }

    fun removeFromHand(card: BaseCard) {
        cardsInHand.remove(card)
    }

    fun cardIsInHand(secondayId: Int): Boolean {
        return cardsInHand.stream().anyMatch { c->c.getSecondayId()==secondayId}
    }
}