package com.game.asura

class CardStore : CardCache {


    private val cardMap: MutableMap<Int, DrawableCard> = HashMap()

    override fun add(card: DrawableCard) {
        cardMap[card.getSecondayId()] = card
    }

    override fun remove(card: DrawableCard) {
        cardMap.remove(card.getSecondayId())
    }

    override fun getCard(key: Int): DrawableCard? {
        return cardMap[key]
    }
}