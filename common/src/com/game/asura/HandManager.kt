package com.game.asura

import com.game.asura.card.Card

class HandManager {

    private val cardsInHand: MutableList<Card> = ArrayList()

    fun getCardsInHand(): List<Card> {
        return cardsInHand
    }

    fun addToPlayerHand(card: Card) {
        cardsInHand.add(card)
    }

    fun getCardFromHand(cardSecondaryId: Int): Card? {
        val optional = cardsInHand.stream().filter { it.getSecondayId() == cardSecondaryId }.findFirst()
        if (optional.isPresent) {
            return optional.get()
        }
        return null
    }

    fun removeFromHand(card: Card) {
        cardsInHand.remove(card)
    }

    fun getCardHandIndex(secondayId: Int): Int {
        for (i in 0 until cardsInHand.size) {
            if (cardsInHand[i].getSecondayId() == secondayId) {
                return i
            }
        }
        //should never happen
        return INVALID_INDEX_
    }
}