package com.game.asura

import com.game.asura.card.BaseCard
import com.game.asura.card.CardType
import java.util.*
import kotlin.random.Random

class ServerPlayer(val playerName: String,
                   val accountKey: String,
                   primary: Int,
                   secondary: Int = Random.nextInt(),
                   val cardInfoStore: CardInfoStore) {

    private val deck: Stack<BaseCard> = Stack()
    val heroPlayer = ServerHeroCard(primary, secondary)
    val handManager = HandManager<BaseCard>()
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
        val slime = cardInfoStore.getCardInfo(1) ?: return
        val slimeHealth = slime.health ?: return
        val slimeMaxHealth = slime.maxHealth ?: return
        val slimeAttack = slime.attack ?: return
        deck.push(ServerMinionCard(1, cardCost = 1, attack = slimeAttack,
                health = slimeHealth, maxHealth = slimeMaxHealth, evolveId = slime.evolveId))
        deck.push(ServerMinionCard(1, cardCost = 1, attack = slimeAttack,
                health = slimeHealth, maxHealth = slimeMaxHealth, evolveId = slime.evolveId))
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