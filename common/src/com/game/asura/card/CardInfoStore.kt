package com.game.asura.card

class CardInfoStore {

    private val cardInfoMap: Map<Int, CardInfo>

    init {
        val jsonCardList = JsonCardList()
        cardInfoMap = jsonCardList.getAllCardInfo().map { it.id to it }.toMap()
    }

    fun getCardInfo(id: Int): CardInfo? {
        return cardInfoMap[id]
    }

    fun getValues(): Collection<CardInfo> {
        return cardInfoMap.values
    }
}