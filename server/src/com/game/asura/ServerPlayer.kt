package com.game.asura

import com.game.asura.card.BaseCard
import com.game.asura.card.CardType
import java.util.*
import kotlin.random.Random

class ServerPlayer(val playerName: String,
                   primary: Int,
                   secondary: Int = Random.nextInt()) {

    private val deck: Stack<BaseCard> = Stack()
    val heroPlayer = ServerHeroCard(primary, secondary)
    val handManager = HandManager()
    val boardManager = BoardManager<BaseCard>(create = { INVALID_MINION_CARD })

    fun draw(): BaseCard? {
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
        deck.push(ServerMinionCard(4, cardCost = 4))
        deck.push(ServerMinionCard(1, cardCost = 1))
        //deck.push(ServerCard(AllCard.FIRST_SPELL.id, cardCost = AllCard.FIRST_SPELL.cost,
        //      cardType = AllCard.FIRST_SPELL.cardType))
        deck.push(ServerSpellCard(2, cardCost = 2,
                cardType = CardType.TARGET_SPELL))
    }

    fun playCard(card: BaseCard, boardPosition: Int?) {
        handManager.removeFromHand(card)
        heroPlayer.updateMana(card.getCost())
        if (card.getCardType() == CardType.MONSTER) {
            if (boardPosition == null) {
                return
            }
            boardManager.updatePlayerBoard(card, boardPosition)
        }
    }
}