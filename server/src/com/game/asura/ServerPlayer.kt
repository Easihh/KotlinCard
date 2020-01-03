package com.game.asura

import java.util.*

class ServerPlayer(playerName: String) : Player(playerName) {

    private val deck: Stack<Card> = Stack()
    val boardManager = BoardManager<Card>(create = { INVALID_SERVER_CARD })

    fun draw(): Card? {
        if (deck.isNotEmpty()) {
            val card = deck.pop()
            handManager.addToPlayerHand(card)
            return card
        }
        return null
    }

    fun cardRemaining(): Int {
        return deck.size
    }

    fun initializeDeck() {
        deck.clear()
        deck.push(ServerCard(AllCard.FIRST_MONSTER.id, cardCost = AllCard.FIRST_MONSTER.cost,
                cardType = AllCard.FIRST_MONSTER.cardType))
        deck.push(ServerCard(AllCard.FIRST_MONSTER.id, cardCost = AllCard.FIRST_MONSTER.cost,
                cardType = AllCard.FIRST_MONSTER.cardType))
        //deck.push(ServerCard(AllCard.FIRST_SPELL.id, cardCost = AllCard.FIRST_SPELL.cost,
        //      cardType = AllCard.FIRST_SPELL.cardType))
        deck.push(ServerCard(AllCard.FIRST_TARGET_SPELL.id, cardCost = AllCard.FIRST_TARGET_SPELL.cost,
                cardType = AllCard.FIRST_TARGET_SPELL.cardType))
    }


    fun takeDmg(dmg: Int) {
        playerLife -= dmg
    }

    fun updateMana(cost: Int) {
        currentPlayerMana -= cost
    }

    fun playCard(card: Card) {
        handManager.removeFromHand(card)
        currentPlayerMana -= card.getCost()
    }
}