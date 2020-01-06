package com.game.asura.processor

import com.game.asura.*
import com.game.asura.card.CardType
import com.game.asura.messagein.*
import com.game.asura.parsing.DecodedMessage

class MessageInProcessor(private val player: ClientPlayer,
                         private val uiManager: UIManager,
                         private val cardStore: CardStore) {

    fun onMessage(message: DecodedMessage) {
        when (message) {
            is PlayerInfoIn -> {
                player.update(message.getChangedFields())
            }
            is MatchInfoIn -> {
                val matchId = message.matchId ?: return
                val gameType = message.gameType
                val match = Match<ClientPlayer>(matchId, gameType)
                player.setMatch(match)
            }
            is CardDrawnIn -> {
                val card = ClientCard(primaryId = message.primaryId, secondaryId = message.secondaryId,
                        cardCost = message.cardCost, cardType = message.cardType)
                println("Adding card to player hand.")
                player.handManager.addToPlayerHand(card)
                uiManager.addCardToHand(card)
                cardStore.add(card)
            }
            is CardPlayedIn -> {
                //only assume 1 player for now
                val card = player.handManager.getCardFromHand(message.secondaryId) ?: return
                println("Removing card:$card from player hand.")
                player.handManager.removeFromHand(card)
                //if it was a monster put it in play
                when (card.getCardType()) {
                    CardType.MONSTER -> {
                        if (message.boardIndex == null) {
                            println("Error, card is of type:${card.getCardType()} but no board index present.")
                            return
                        }
                        val myCard = card as DrawableCard
                        player.boardManager.updatePlayerBoard(myCard, message.boardIndex)
                        uiManager.moveCardToBoard(myCard, message.boardIndex)
                    }
                    else -> {
                        uiManager.removeCardfromHand(card as DrawableCard)
                    }
                }
            }
            is HeroPowerIn -> {
                val target = cardStore.getCard(message.cardTarget) ?: return
                println("Handle effect of card:$target being the target of hero power.")
            }
            is StartTurnIn -> {
                uiManager.startTurnTimer()
            }
            is EndTurnIn -> {
                println("End Turn Received from server.")
                //cancel all pending action etc here
            }
            else -> {
                println("Unable to process message:$message missing logic.")
            }
        }
    }
}