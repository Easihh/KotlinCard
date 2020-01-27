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
                if (msg.playerName.toLowerCase() == player.playerName.toLowerCase()) {
                    player.maxMana = msg.playerMaxMana
                    player.currentMana = msg.playerCurrentMana
                    player.playerLifePoint = msg.playerHealth
                }
                if (msg.playerName.toLowerCase() == opponent.playerName.toLowerCase()) {
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
            is CardPlayedIn -> {
                //only assume 1 player for now
                val card = player.handManager.getCardFromHand(msg.secondaryId) ?: return
                println("Removing card:$card from player hand.")
                player.handManager.removeFromHand(card)
                //if it was a monster put it in play
                when (card.getCardType()) {
                    CardType.MONSTER -> {
                        if (msg.boardIndex == null) {
                            println("Error, card is of type:${card.getCardType()} but no board index present.")
                            return
                        }
                        val myCard = card as DrawableCard
                        player.boardManager.updatePlayerBoard(myCard, msg.boardIndex)
                        uiManager.moveCardToBoard(myCard, msg.boardIndex)
                    }
                    else -> {
                        uiManager.removeCardfromHand(card as DrawableCard)
                    }
                }
            }
            is StartTurnIn -> {
                uiManager.startTurnTimer()
            }
            is EndTurnIn -> {
                println("End Turn Received from server.")
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
                firstMonster.getActor().remove()
                secondMonster.getActor().remove()
                player.boardManager.removeCard(firstMonster)
                player.boardManager.removeCard(secondMonster)
                uiManager.initCardTexture(evolved)
                uiManager.moveCardToBoard(evolved, msg.boardPosition)
                player.boardManager.updatePlayerBoard(evolved, msg.boardPosition)
            }
            else -> {
                println("Unable to process message:$msg missing logic.")
            }
        }
    }
}