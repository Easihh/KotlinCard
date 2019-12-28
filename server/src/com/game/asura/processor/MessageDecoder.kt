package com.game.asura.processor

import com.game.asura.*
import com.game.asura.account.Account
import com.game.asura.messagein.CardPlayedIn
import com.game.asura.messagein.GameRequestIn
import com.game.asura.messagein.LoginRequestIn

class MessageDecoder(private val queue: InsertableQueue) : CoreMessageParser() {

    fun decode(playerAccount: Account) {
        val tokenizer = playerAccount.getTokenizer()
        parseMessage(tokenizer)
        var decodedMessage: DecodedMessage? = null
        when (val msgType = getMessageType()) {
            MessageType.LOGIN_REQUEST -> {
                decodedMessage = LoginRequestIn(playerAccount)
            }
            MessageType.GAME_REQUEST -> {
                val gameRequestData = getGameRequestData()
                decodedMessage = GameRequestIn(playerAccount.getAccountKey(), gameRequestData.gameType)
            }
            MessageType.CARD_PLAYED -> {
                val cardPlayedData = getCardPlayedData()
                //should validate important field here
                val cardId = cardPlayedData.cardPrimaryId ?: return
                val secCardId = cardPlayedData.cardSecondaryId ?: return
                val matchId = cardPlayedData.matchId ?: return
                decodedMessage = CardPlayedIn(playerAccount.getAccountKey(), cardId, secCardId, matchId,
                        cardPlayedData.cardTarget, cardPlayedData.boardIndex)
            }
            else -> {
                println("Message of type $msgType has no decode logic.")
            }
        }
        tokenizer.clear()
        if (decodedMessage == null) {
            println("Cannot process Decoded Message since it is null.")
            return
        }
        queue.addMessage(decodedMessage)
    }
}