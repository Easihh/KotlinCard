package com.game.asura

import com.game.asura.card.AllCard
import com.game.asura.card.BaseCard
import com.game.asura.card.Card
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
        deck.push(ServerMinionCard(AllCard.MONSTER_A.id, cardCost = AllCard.MONSTER_A.cost,
                cardType = AllCard.MONSTER_A.cardType))
        deck.push(ServerMinionCard(AllCard.FIRST_MONSTER.id, cardCost = AllCard.FIRST_MONSTER.cost,
                cardType = AllCard.FIRST_MONSTER.cardType))
        //deck.push(ServerCard(AllCard.FIRST_SPELL.id, cardCost = AllCard.FIRST_SPELL.cost,
        //      cardType = AllCard.FIRST_SPELL.cardType))
        deck.push(ServerSpellCard(AllCard.FIRST_TARGET_SPELL.id, cardCost = AllCard.FIRST_TARGET_SPELL.cost,
                cardType = AllCard.FIRST_TARGET_SPELL.cardType))
    }

    fun playCard(card: Card) {
        handManager.removeFromHand(card)
        heroPlayer.updateMana(card.getCost())
    }
}