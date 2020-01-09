package com.game.asura

import com.game.asura.card.AllCard
import com.game.asura.card.Card
import java.util.*

class ServerPlayer(val playerName: String) {

    private val deck: Stack<Card> = Stack()
    val heroPlayer = ServerHero(AllCard.MAGE_HERO.id)
    val handManager = HandManager()
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
        deck.push(ServerCard(AllCard.MONSTER_A.id, cardCost = AllCard.MONSTER_A.cost,
                cardType = AllCard.MONSTER_A.cardType))
        deck.push(ServerCard(AllCard.FIRST_MONSTER.id, cardCost = AllCard.FIRST_MONSTER.cost,
                cardType = AllCard.FIRST_MONSTER.cardType))
        //deck.push(ServerCard(AllCard.FIRST_SPELL.id, cardCost = AllCard.FIRST_SPELL.cost,
        //      cardType = AllCard.FIRST_SPELL.cardType))
        deck.push(ServerCard(AllCard.FIRST_TARGET_SPELL.id, cardCost = AllCard.FIRST_TARGET_SPELL.cost,
                cardType = AllCard.FIRST_TARGET_SPELL.cardType))
    }

    fun playCard(card: Card) {
        handManager.removeFromHand(card)
        heroPlayer.updateMana(card.getCost())
    }
}