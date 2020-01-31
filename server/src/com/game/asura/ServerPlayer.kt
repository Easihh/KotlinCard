package com.game.asura

import com.game.asura.card.Card
import com.game.asura.card.CardType
import com.game.asura.card.Minion
import java.util.*

class ServerPlayer(val playerName: String,
                   val accountKey: String,
                   private val cardInfoStore: CardInfoStore) {


    private val deck: Stack<Card> = Stack()
    val handManager = HandManager<Card>()
    val boardManager = BoardManager<Minion>()
    var playerLifePoint: Int = 30
    var currentMana: Int = 0
    var maxMana: Int = 10
    var currentPhase: Phase = Phase.MAIN


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
        val slime = cardInfoStore.getCardInfo(1) ?: return
        val slimeHealth = slime.health ?: return
        val slimeMaxHealth = slime.maxHealth ?: return
        val slimeAttack = slime.attack ?: return
        deck.push(ServerMinionCard(1, cardCost = 1, attack = slimeAttack,
                health = slimeHealth, maxHealth = slimeMaxHealth, evolveId = slime.evolveId))
        deck.push(ServerMinionCard(1, cardCost = 1, attack = slimeAttack,
                health = slimeHealth, maxHealth = slimeMaxHealth, evolveId = slime.evolveId))
        deck.push(ServerMinionCard(1, cardCost = 1, attack = slimeAttack,
                health = slimeHealth, maxHealth = slimeMaxHealth, evolveId = slime.evolveId))
        val spellOne = cardInfoStore.getCardInfo(2) ?: return
        deck.push(ServerSpellCard(2, cardCost = spellOne.cost,
                cardType = CardType.SPELL, ability = spellOne.ability))
        deck.push(ServerMinionCard(1, cardCost = 1, attack = slimeAttack,
                health = slimeHealth, maxHealth = slimeMaxHealth, evolveId = slime.evolveId))
    }

    fun playCard(card: Card) {
        handManager.removeFromHand(card)
        currentMana -= card.getCost()
    }

    fun playMinionCard(card: Minion, boardPosition: Int) {
        playCard(card)
        boardManager.updatePlayerBoard(card, boardPosition)
    }
}