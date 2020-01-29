package com.game.asura.processor

import com.badlogic.gdx.Screen
import com.game.asura.*
import com.game.asura.card.CardType
import com.game.asura.messagein.*
import com.game.asura.parsing.DecodedMessage
import com.game.asura.screen.MatchScreen
import com.game.asura.screen.PreMatchScreen
import ktx.app.KtxGame

class MessageInProcessor(private val player: ClientPlayer,
                         private val uiManager: UIManager,
                         private val cardStore: CardStore,
                         private val parentScreen: KtxGame<Screen>,
                         private val opponent: ClientPlayer) : MessageProcessor<DecodedMessage> {

    override fun onMessage(msg: DecodedMessage) {
        when (msg) {
            is CardInfoIn -> {
                val card = cardStore.getCard(msg.secondaryCardId) ?: return
                if (card.getCardType() == CardType.MONSTER) {
                    val monster = card as MonsterDrawableCard
                    monster.update(msg.getChangedFields())
                }
            }
            is PlayerInfoIn -> {
                if (isOurMessage(msg.playerName)) {
                    player.maxMana = msg.playerMaxMana
                    player.currentMana = msg.playerCurrentMana
                    player.playerLifePoint = msg.playerHealth
                }
                if (!isOurMessage(msg.playerName)) {
                    opponent.maxMana = msg.playerMaxMana
                    opponent.currentMana = msg.playerCurrentMana
                    opponent.playerLifePoint = msg.playerHealth
                }
            }
            is CardDrawnIn -> {
                val card = SpellCard(primaryId = msg.primaryId, secondaryId = msg.secondaryId,
                        cardCost = msg.cardCost, cardType = msg.cardType)

                player.handManager.addToPlayerHand(card)
                uiManager.addCardToHand(card)
                cardStore.add(card)
            }
            is MonsterCardDrawnIn -> {
                val card = MinionCard(primaryId = msg.primaryId, secondaryId = msg.secondaryId,
                        cardCost = msg.cardCost, cardType = msg.cardType, attack = msg.attack,
                        health = msg.health, maxHealth = msg.maxHealth)
                player.handManager.addToPlayerHand(card)
                uiManager.addCardToHand(card)
                cardStore.add(card)
            }
            is MonsterCardPlayedIn -> {
                if (!isOurMessage(msg.accountName)) {
                    //opponent played card
                    val card = MinionCard(primaryId = msg.primaryId, secondaryId = msg.secondaryId,
                            cardCost = msg.cardCost, cardType = CardType.MONSTER, attack = msg.attack,
                            health = msg.health, maxHealth = msg.maxHealth)
                    opponent.boardManager.updatePlayerBoard(card, msg.boardIndx)
                    uiManager.initCardTexture(card)
                    uiManager.addEnemyMonsterToBoard(card, msg.boardIndx)
                    cardStore.add(card)
                    return
                }
                val card = player.handManager.getCardFromHand(msg.secondaryId) ?: return
                if (card !is MinionCard) {
                    println("Error, card $card should be a minion.")
                    return
                }
                println("Removing card:$card from player hand.")
                player.handManager.removeFromHand(card)
                // put it in play
                player.boardManager.updatePlayerBoard(card, msg.boardIndx)
                uiManager.addMonsterToBoard(card, msg.boardIndx)
                cardStore.add(card)

            }
            is StartTurnIn -> {
                uiManager.startTurnTimer()
                player.myTurn = true
                player.currentPhase = msg.phase
            }
            is EndTurnIn -> {
                println("End Turn Received from server.")
                player.myTurn = false
                //cancel all pending action etc here
            }
            is PhaseChangeIn -> {
                player.currentPhase = msg.nextPhase
            }
            is MatchEndIn -> {
                println("Match result:${msg.matchResult}")
                parentScreen.removeScreen<MatchScreen>()
                parentScreen.setScreen<PreMatchScreen>()
            }
            is MonsterEvolveIn -> {
                val evolved = MinionCard(msg.primaryCardId, msg.secondaryCardId, msg.cardCost, msg.cardType, msg.attack, msg.health, msg.maxHealth)
                val firstMonster = cardStore.getCard(msg.firstMonsterId) ?: return
                val secondMonster = cardStore.getCard(msg.secondMonsterId) ?: return
                if (firstMonster !is MinionCard || secondMonster !is MinionCard) {
                    println("Error, card $firstMonster and card $secondMonster should be minions.")
                    return
                }
                firstMonster.actor.remove()
                secondMonster.actor.remove()
                uiManager.initCardTexture(evolved)
                if (!isOurMessage(msg.accountName)) {
                    uiManager.addEnemyMonsterToBoard(evolved, msg.boardPosition)
                    opponent.boardManager.removeCard(firstMonster)
                    opponent.boardManager.removeCard(secondMonster)
                    opponent.boardManager.updatePlayerBoard(evolved, msg.boardPosition)
                    return
                }
                player.boardManager.removeCard(firstMonster)
                player.boardManager.removeCard(secondMonster)
                uiManager.addMonsterToBoard(evolved, msg.boardPosition)
                player.boardManager.updatePlayerBoard(evolved, msg.boardPosition)
            }
            else -> {
                println("Unable to process message:$msg missing logic.")
            }
        }
    }

    private fun isOurMessage(accountName: String): Boolean {
        return accountName.toLowerCase() == player.playerName.toLowerCase()
    }
}