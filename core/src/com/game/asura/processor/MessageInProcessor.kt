package com.game.asura.processor

import com.game.asura.*
import com.game.asura.messagein.CardDrawnIn
import com.game.asura.messagein.CardPlayedIn
import com.game.asura.messagein.MatchInfoIn
import com.game.asura.messagein.PlayerInfoIn

class MessageInProcessor(private val player: ClientPlayer,
                         private val uiManager: UIManager) {

    fun onMessage(message: DecodedMessage) {
        println("Processing In message on thread:${Thread.currentThread().name}")
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
                player.addToPlayerHand(card)
                uiManager.addCardToHand(card)
            }
            is CardPlayedIn -> {
                //only assume 1 player for now
                val card = player.getCardFromHand(message.secondaryId) ?: return
                println("Removing card to player hand.")
                player.removeFromHand(card)
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
            else -> {
                println("Unable to process message:$message missing logic.")
            }
        }
    }
}