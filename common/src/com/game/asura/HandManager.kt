package com.game.asura

import com.game.asura.card.BaseCard
import com.game.asura.card.Card

class HandManager<T:BaseCard> {

    private val cardsInHand: MutableList<T> = ArrayList()

    fun getCardsInHand(): List<T> {
        return cardsInHand
    }

    fun addToPlayerHand(card: T) {
        cardsInHand.add(card)
    }

    fun getCardFromHand(cardSecondaryId: Int): T? {
        val optional = cardsInHand.stream().filter { it.getSecondayId() == cardSecondaryId }.findFirst()
        if (optional.isPresent) {
            return optional.get()
        }
        return null
    }

    fun removeFromHand(card: T) {
        cardsInHand.remove(card)
    }

    fun cardIsInHand(secondayId: Int): Boolean {
        return cardsInHand.stream().anyMatch { c->c.getSecondayId()==secondayId}
    }
}