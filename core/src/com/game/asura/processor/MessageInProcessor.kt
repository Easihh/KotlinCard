package com.game.asura.processor

import com.game.asura.*
import com.game.asura.messagein.CardDrawnIn
import com.game.asura.messagein.CardPlayedIn
import com.game.asura.messagein.MatchInfoIn
import com.game.asura.messagein.PlayerInfoIn

class MessageInProcessor(private val playerAccount: PlayerAccount,
                         private val uiManager: UIManager) {

    fun onMessage(message: DecodedMessage) {
        println("Processing In message on thread:${Thread.currentThread().name}")
        when (message) {
            is PlayerInfoIn -> {
                playerAccount.player.update(message.getChangedFields())
            }
            is MatchInfoIn -> {
                val matchId = message.matchId ?: return
                val gameType = message.gameType
                val match = Match<ClientPlayer>(matchId, gameType)
                playerAccount.setMatch(match)
            }
            is CardDrawnIn -> {
                val card = ClientCard(primaryId = message.primaryId, secondaryId = message.secondaryId,
                        cardCost = message.cardCost, cardType = message.cardType)
                println("Adding card to player hand.")
                playerAccount.player.addToPlayerHand(card)
                uiManager.addCardToHand(card)
            }
            is CardPlayedIn -> {
                //only assume 1 player for now
                val card = playerAccount.player.getCardFromHand(message.secondaryId) ?: return
                println("Removing card to player hand.")
                playerAccount.player.removeFromHand(card)
                uiManager.removeCardfromHand(card as DrawableCard)
            }
            else -> {
                println("Unable to process message:$message missing logic.")
            }
        }
    }
}